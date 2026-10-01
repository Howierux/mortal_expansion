package com.howierux.mortalexpansion.sanctification;

import com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/** No level/chunk lookups during world generation: chunk indexing reads the supplied sections only. */
public final class SanctifiedAreaHandler {
    private SanctifiedAreaHandler() {}
    private static final Map<Level, Index> INDEXES = new WeakHashMap<>();

    private static final class Index {
        final Map<Long, Set<BlockPos>> chunks = new HashMap<>();
        final Map<Long, Integer> columns = new HashMap<>();
        final Map<Long, Boolean> areas = new HashMap<>();
    }

    private static long chunkKey(int x, int z) { return SaltEnclosure.key(x >> 4, z >> 4); }
    private static Index index(Level level) { return INDEXES.computeIfAbsent(level, l -> new Index()); }

    /** Torch link changes invalidate the same enclosure cache used by salt. */
    public static void invalidateAreas(Level level) {
        Index index = INDEXES.get(level);
        if (index != null) index.areas.clear();
    }

    public static void addSalt(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) return;
        Index index = index(level);
        if (index.chunks.computeIfAbsent(chunkKey(pos.getX(), pos.getZ()), k -> new HashSet<>())
                .add(pos.immutable())) {
            index.columns.merge(SaltEnclosure.key(pos.getX(), pos.getZ()), 1, Integer::sum);
            index.areas.clear();
        }
    }

    public static void removeSalt(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) return;
        Index index = index(level);
        Set<BlockPos> salt = index.chunks.get(chunkKey(pos.getX(), pos.getZ()));
        if (salt != null && salt.remove(pos)) {
            decrement(index, pos);
            index.areas.clear();
        }
    }

    private static void decrement(Index index, BlockPos pos) {
        index.columns.computeIfPresent(SaltEnclosure.key(pos.getX(), pos.getZ()),
                (key, count) -> count == 1 ? null : count - 1);
    }

    private static void forgetChunk(Index index, long key) {
        Set<BlockPos> positions = index.chunks.remove(key);
        if (positions != null) positions.forEach(pos -> decrement(index, pos));
        index.areas.clear();
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getChunk() instanceof LevelChunk chunk)) return;
        Index index = index(level);
        long key = SaltEnclosure.key(chunk.getPos().x, chunk.getPos().z);
        forgetChunk(index, key);
        index.chunks.put(key, new HashSet<>());
        LevelChunkSection[] sections = chunk.getSections();
        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            if (!section.getStates().maybeHas(state -> state.is(ModBlocks.HOLY_SALT.get()))) continue;
            int baseY = chunk.getMinBuildHeight() + i * 16;
            for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++) {
                if (section.getBlockState(x, y, z).is(ModBlocks.HOLY_SALT.get())) {
                    addSalt(level, new BlockPos(chunk.getPos().getMinBlockX() + x, baseY + y,
                            chunk.getPos().getMinBlockZ() + z));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Index index = INDEXES.get(level);
        if (index != null) forgetChunk(index, SaltEnclosure.key(event.getChunk().getPos().x,
                event.getChunk().getPos().z));
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob) || !mob.isAlive()
                || !(mob.level() instanceof ServerLevel level) || mob.tickCount % 10 != 0) return;
        Index index = index(level);
        if (index.columns.isEmpty() && !SpiritTorchBarrierTracker.hasBarriers(level)) return;
        long start = SaltEnclosure.key(mob.blockPosition().getX(), mob.blockPosition().getZ());
        Boolean enclosed = index.areas.get(start);
        if (enclosed == null) {
            SaltEnclosure.Result result = SaltEnclosure.find(start, cell -> index.columns.containsKey(cell)
                            || SpiritTorchBarrierTracker.hasBarrierColumn(level,
                                    SaltEnclosure.x(cell), SaltEnclosure.z(cell)),
                    cell -> index.chunks.containsKey(chunkKey(SaltEnclosure.x(cell), SaltEnclosure.z(cell))));
            // Bound memory for wandering mobs and large servers.
            if (index.areas.size() > 65536) index.areas.clear();
            result.interior().forEach(cell -> index.areas.put(cell, result.enclosed()));
            enclosed = result.enclosed();
        }
        if (enclosed) mob.addEffect(new MobEffectInstance(ModEffects.SANCTIFIED, 12, 0, true, true, true));
    }
}
