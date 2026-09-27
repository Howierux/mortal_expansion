package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
 * Once per Minecraft day:
 * - haunted loaded chunks have a small chance to spread one level of haunt to
 *   one loaded cardinal neighbor;
 * - haunted chunks have a 10% chance to decay by one tier;
 * - source chunks never decay below their source baseline.
 *
 * Biome-derived haunt is the first source type. Point-of-interest source
 * baselines can be added to determineSourceFloor() later without changing the
 * spread/decay algorithm.
 */
public final class HauntSimulationHandler {
    private HauntSimulationHandler() {}

    private static final long TICKS_PER_DAY = 24_000L;

    /** 7.5% daily chance for each haunted loaded chunk to attempt one spread. */
    private static final float SPREAD_CHANCE = 0.075F;

    /** 10% daily chance to lose one tier: 90% chance to remain unchanged. */
    private static final float DECAY_CHANCE = 0.10F;

    private static final int[][] CARDINAL_OFFSETS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    /**
     * We track chunk positions from load/unload events so simulation never
     * requests or generates chunks merely to inspect haunt.
     */
    private static final Map<ServerLevel, Set<Long>> LOADED_CHUNKS = new WeakHashMap<>();

    /** Last observed Minecraft day for each loaded ServerLevel. */
    private static final Map<ServerLevel, Long> LAST_DAY = new WeakHashMap<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !level.dimension().equals(Level.OVERWORLD)
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
                || !level.dimension().equals(Level.OVERWORLD)) {
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
                || !level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        long currentDay = Math.floorDiv(level.getDayTime(), TICKS_PER_DAY);
        Long previousDay = LAST_DAY.put(level, currentDay);

        // Establish the baseline after a server/world load; don't run an
        // artificial "new day" pass immediately on startup.
        if (previousDay == null || previousDay == currentDay) {
            return;
        }

        runDailySimulation(level);
    }

    /**
     * Runs one daily pass against a snapshot of the currently loaded chunks.
     * Changes are collected first, then applied, preventing same-day chain
     * reactions where newly spread haunt immediately spreads again.
     */
    public static void runDailySimulation(ServerLevel level) {
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

        // Snapshot all tiers before making any rolls.
        Map<Long, HauntTier> startingTiers = new HashMap<>();
        for (LevelChunk chunk : loadedChunks) {
            startingTiers.put(
                    chunk.getPos().toLong(),
                    HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER))
            );
        }

        Map<Long, HauntTier> resultingTiers = new HashMap<>(startingTiers);
        RandomSource random = level.getRandom();

        for (LevelChunk chunk : loadedChunks) {
            long sourceKey = chunk.getPos().toLong();
            HauntTier sourceTier = startingTiers.getOrDefault(sourceKey, HauntTier.VOID);

            if (sourceTier == HauntTier.VOID) {
                continue;
            }

            // Slow outward spread. Only one cardinal neighbor is attempted,
            // and the target must already be loaded.
            if (random.nextFloat() < SPREAD_CHANCE) {
                int[] offset = CARDINAL_OFFSETS[random.nextInt(CARDINAL_OFFSETS.length)];
                ChunkPos sourcePos = chunk.getPos();
                ChunkPos targetPos = new ChunkPos(sourcePos.x + offset[0], sourcePos.z + offset[1]);
                long targetKey = targetPos.toLong();
                LevelChunk targetChunk = chunksByPos.get(targetKey);

                if (targetChunk != null) {
                    HauntTier targetTier = startingTiers.getOrDefault(targetKey, HauntTier.VOID);

                    // Any haunted chunk can add haunt pressure to a neighbor,
                    // even when the target is already at or above the source's
                    // current tier. This lets a stable source such as a
                    // Supernatural Spiritwood Forest very slowly push nearby
                    // ordinary chunks into higher tiers over long periods.
                    //
                    // A target can still gain at most one tier in a daily pass,
                    // and Eldritch remains the hard maximum.
                    HauntTier alreadyPlanned = resultingTiers.getOrDefault(targetKey, targetTier);
                    HauntTier spreadTier = HauntTier.fromId(targetTier.id() + 1);

                    if (spreadTier.id() > alreadyPlanned.id()) {
                        resultingTiers.put(targetKey, spreadTier);
                    }
                }
            }

            // Slow decay. Sources have a floor; ordinary spread-created haunt
            // can eventually dissipate completely.
            if (random.nextFloat() < DECAY_CHANCE) {
                HauntTier floor = determineSourceFloor(chunk);
                HauntTier planned = resultingTiers.getOrDefault(sourceKey, sourceTier);

                if (planned.id() > floor.id()) {
                    resultingTiers.put(
                            sourceKey,
                            HauntTier.fromId(Math.max(floor.id(), planned.id() - 1))
                    );
                }
            }
        }

        for (Map.Entry<Long, HauntTier> entry : resultingTiers.entrySet()) {
            HauntTier original = startingTiers.get(entry.getKey());
            HauntTier result = entry.getValue();

            if (original != null && result != original) {
                LevelChunk chunk = chunksByPos.get(entry.getKey());
                if (chunk != null) {
                    chunk.setData(ModAttachments.HAUNT_TIER, result.id());
                }
            }
        }
    }

    /**
     * Minimum haunt supplied by permanent world sources.
     *
     * Currently biome-derived sources are implemented. Future haunted POIs
     * should raise this floor here rather than being special-cased in decay.
     */
    public static HauntTier determineSourceFloor(LevelChunk chunk) {
        return NaturalHauntGenerator.determineInitialTier(chunk);
    }
}
