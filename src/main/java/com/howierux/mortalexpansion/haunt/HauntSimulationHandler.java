package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Slow, loaded-chunk-only haunt simulation.
 *
 * Haunt is stored as fine-grained points inside named tier bands:
 * Void 0-9, Natural 10-19, ... Eldritch 60-69.
 *
 * World simulation now moves individual haunt points rather than whole tiers.
 */
public final class HauntSimulationHandler {
    private HauntSimulationHandler() {}

    private static final long TICKS_PER_DAY = 24_000L;

    private static final long SPREAD_INTERVAL = TICKS_PER_DAY / 3;
    private static final float BASE_SPREAD_CHANCE = 2.0F / 3.0F;

    /** Zero never spreads. All nonzero tiers reach 100% at new moon. */
    public static float spreadChance(int value, int moonPhase) {
        int phase = Math.floorMod(moonPhase, 8);
        float moonWeight = 0.5F + 0.25F * (4 - Math.abs(phase - 4));
        return value <= HauntTier.MIN_VALUE ? 0.0F : Math.min(1.0F, BASE_SPREAD_CHANCE * moonWeight);
    }

    /** One pass on a forward interval crossing; skipped intervals never catch up. */
    public static boolean spreadCheckDue(long previousTime, long currentTime) {
        return currentTime > previousTime
                && Math.floorDiv(currentTime, SPREAD_INTERVAL) > Math.floorDiv(previousTime, SPREAD_INTERVAL);
    }

    /** Native day times: dawn 0, sunset 12000, midnight 18000. Never replay skipped checks. */
    public static boolean bloodstoneCheckDue(long previousTime, long currentTime) {
        if (currentTime <= previousTime) return false;
        return Math.floorDiv(currentTime, TICKS_PER_DAY) > Math.floorDiv(previousTime, TICKS_PER_DAY)
                || Math.floorDiv(currentTime - 12_000L, TICKS_PER_DAY) > Math.floorDiv(previousTime - 12_000L, TICKS_PER_DAY)
                || Math.floorDiv(currentTime - 18_000L, TICKS_PER_DAY) > Math.floorDiv(previousTime - 18_000L, TICKS_PER_DAY);
    }

    /** 10% daily chance to lose one haunt point: 90% chance to remain unchanged. */
    private static final float DECAY_CHANCE = 0.10F;

    /**
     * Player haunt leakage is checked every 200 ticks (10 seconds).
     *
     * Each named personal haunt tier contributes 0.25% leak chance per check.
     * A successful leak transfers exactly one haunt point from the player into
     * the current chunk.
     */
    private static final long PLAYER_LEAK_CHECK_INTERVAL = 200L;
    private static final float PLAYER_LEAK_CHANCE_PER_TIER = 0.0025F;

    /** Each eligible tier adds 0.5 percentage points per dawn/sunset/midnight check. */
    public static float bloodstoneConversionChance(int hauntValue) {
        int tier = HauntTier.fromValue(hauntValue).id();
        return tier < HauntTier.DEAD_RISING.id() ? 0.0F
                : 0.005F * (tier - HauntTier.DEAD_RISING.id() + 1);
    }

    private static final int[][] CARDINAL_OFFSETS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    private static final Map<ServerLevel, Set<Long>> LOADED_CHUNKS = new WeakHashMap<>();
    private static final Map<ServerLevel, Long> LAST_SIMULATION_TIME = new WeakHashMap<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !NaturalHauntGenerator.supportsDimension(level)
                || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        LOADED_CHUNKS
                .computeIfAbsent(level, ignored -> new HashSet<>())
                .add(chunk.getPos().toLong());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !NaturalHauntGenerator.supportsDimension(level)) {
            return;
        }

        Set<Long> loaded = LOADED_CHUNKS.get(level);
        if (loaded != null) {
            loaded.remove(event.getChunk().getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !NaturalHauntGenerator.supportsDimension(level)) {
            return;
        }

        runPlayerHauntLeakChecks(level);

        long currentTime = level.getDayTime();
        Long previousTime = LAST_SIMULATION_TIME.put(level, currentTime);
        if (previousTime == null) return;
        boolean spread = spreadCheckDue(previousTime, currentTime);
        boolean bloodstone = bloodstoneCheckDue(previousTime, currentTime);
        if (!spread && !bloodstone) return;
        boolean daily = currentTime > previousTime
                && Math.floorDiv(currentTime, TICKS_PER_DAY) > Math.floorDiv(previousTime, TICKS_PER_DAY);
        runSimulation(level, daily, spread, bloodstone);
    }

    /**
     * Transfers one point of personal haunt into the chunk on a successful roll.
     *
     * Example: a Natural player initialized at value 15 standing in a Void-0
     * chunk can leak three times and become Natural-12 while the chunk becomes
     * Void-3. Neither named tier changes during those three transfers.
     */
    private static void runPlayerHauntLeakChecks(ServerLevel level) {
        if (level.getGameTime() % PLAYER_LEAK_CHECK_INTERVAL != 0L) {
            return;
        }

        RandomSource random = level.getRandom();

        for (ServerPlayer player : level.players()) {
            int playerValue = PlayerHauntManager.getValue(player);
            HauntTier playerTier = HauntTier.fromValue(playerValue);

            if (playerTier == HauntTier.VOID) {
                continue;
            }

            BlockPos pos = player.blockPosition();

            if (PurificationWardTracker.isChunkProtected(
                    level,
                    new ChunkPos(pos)
            )) {
                continue;
            }

            int chunkValue = HauntManager.getValue(level, pos);

            // Haunt flows only from the more-haunted player into the less-haunted world.
            if (chunkValue >= playerValue) {
                continue;
            }

            float leakChance = PLAYER_LEAK_CHANCE_PER_TIER * playerTier.id();
            if (random.nextFloat() >= leakChance) {
                continue;
            }

            HauntManager.addPoints(level, pos, 1);
            PlayerHauntManager.addPoints(player, -1);
        }
    }

    /**
     * Runs one spread pass against a snapshot of currently loaded chunks.
     * Planned changes are applied only after all rolls, preventing chain
     * reactions within a pass.
     */
    private static void runSimulation(ServerLevel level, boolean daily, boolean spread, boolean bloodstone) {
        Set<Long> tracked = LOADED_CHUNKS.get(level);
        if (tracked == null || tracked.isEmpty()) {
            return;
        }

        ArrayList<LevelChunk> loadedChunks = new ArrayList<>();
        Map<Long, LevelChunk> chunksByPos = new HashMap<>();

        for (long packedPos : new HashSet<>(tracked)) {
            ChunkPos pos = new ChunkPos(packedPos);
            LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);

            if (chunk != null) {
                loadedChunks.add(chunk);
                chunksByPos.put(packedPos, chunk);
            }
        }

        if (loadedChunks.isEmpty()) {
            return;
        }

        Map<Long, Integer> startingValues = new HashMap<>();
        for (LevelChunk chunk : loadedChunks) {
            startingValues.put(
                    chunk.getPos().toLong(),
                    HauntManager.getValue(level, chunk.getPos())
            );
        }

        Map<Long, Integer> resultingValues = new HashMap<>(startingValues);
        Set<Long> wardProtectedChunks = PurificationWardTracker.protectedLoadedChunks(
                level,
                chunksByPos.keySet()
        );

        // A Ward cleanses each affected loaded chunk exactly once per day.
        // Multiple overlapping Wards do not stack their daily point reduction.
        if (daily) for (long protectedKey : wardProtectedChunks) {
            int value = resultingValues.getOrDefault(
                    protectedKey,
                    startingValues.getOrDefault(protectedKey, 0)
            );

            if (value > HauntTier.MIN_VALUE) {
                resultingValues.put(protectedKey, value - 1);
            }
        }

        RandomSource random = level.getRandom();
        int moonPhase = level.getMoonPhase();

        for (LevelChunk chunk : loadedChunks) {
            if (daily) GraveyardStoneWeathering.sample(level, chunk);
            long sourceKey = chunk.getPos().toLong();
            int sourceValue = startingValues.getOrDefault(sourceKey, 0);
            HauntTier sourceTier = HauntTier.fromValue(sourceValue);

            boolean sourceWardProtected = wardProtectedChunks.contains(sourceKey);

            if (spread && !sourceWardProtected
                    && sourceValue > 0
                    && random.nextFloat() < spreadChance(sourceValue, moonPhase)) {
                int[] offset = CARDINAL_OFFSETS[random.nextInt(CARDINAL_OFFSETS.length)];
                ChunkPos sourcePos = chunk.getPos();
                ChunkPos targetPos = new ChunkPos(sourcePos.x + offset[0], sourcePos.z + offset[1]);
                long targetKey = targetPos.toLong();

                if (chunksByPos.containsKey(targetKey)
                        && !wardProtectedChunks.contains(targetKey)) {
                    int targetValue = startingValues.getOrDefault(targetKey, 0);
                    int planned = resultingValues.getOrDefault(targetKey, targetValue);

                    if (planned < HauntTier.MAX_VALUE) {
                        resultingValues.put(targetKey, planned + 1);
                    }
                }
            }

            if (daily && !sourceWardProtected && random.nextFloat() < DECAY_CHANCE) {
                int floorValue = determineSourceFloorValue(chunk);
                int planned = resultingValues.getOrDefault(sourceKey, sourceValue);

                if (planned > floorValue) {
                    resultingValues.put(sourceKey, Math.max(floorValue, planned - 1));
                } else if (planned < floorValue) {
                    // Once Ward coverage is gone, biome influence slowly restores
                    // a cleansed chunk toward its ordinary floor.
                    resultingValues.put(sourceKey, Math.min(floorValue, planned + 1));
                }
            }

            if (bloodstone && sourceTier.id() >= HauntTier.DEAD_RISING.id()
                    && random.nextFloat() < bloodstoneConversionChance(sourceValue)
                    && tryConvertDiamondOre(level, chunk, random)) {
                int floorValue = determineSourceFloorValue(chunk);
                int planned = resultingValues.getOrDefault(sourceKey, sourceValue);

                if (planned > floorValue) {
                    resultingValues.put(sourceKey, Math.max(floorValue, planned - 1));
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : resultingValues.entrySet()) {
            int original = startingValues.getOrDefault(entry.getKey(), 0);
            int result = entry.getValue();

            if (result != original && chunksByPos.containsKey(entry.getKey())) {
                HauntManager.addPoints(level, new ChunkPos(entry.getKey()), result - original);
            }
        }
    }

    private static boolean tryConvertDiamondOre(
            ServerLevel level,
            LevelChunk chunk,
            RandomSource random
    ) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos selected = null;
        int candidatesSeen = 0;

        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    cursor.set(
                            chunkPos.getMinBlockX() + localX,
                            y,
                            chunkPos.getMinBlockZ() + localZ
                    );

                    BlockState state = chunk.getBlockState(cursor);
                    if (!state.is(Blocks.DIAMOND_ORE)
                            && !state.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                        continue;
                    }

                    candidatesSeen++;
                    if (random.nextInt(candidatesSeen) == 0) {
                        selected = cursor.immutable();
                    }
                }
            }
        }

        if (selected == null) {
            return false;
        }

        return level.setBlock(
                selected,
                ModBlocks.BLOODSTONE_ORE.get().defaultBlockState(),
                3
        );
    }

    /** Midpoint baseline supplied by permanent biome-derived haunt sources. */
    public static int determineSourceFloorValue(LevelChunk chunk) {
        return NaturalHauntGenerator.determineInitialTier(chunk.getLevel(), chunk).midpointValue();
    }

    /** Named version retained for callers interested only in the tier band. */
    public static HauntTier determineSourceFloor(LevelChunk chunk) {
        return HauntTier.fromValue(determineSourceFloorValue(chunk));
    }
}
