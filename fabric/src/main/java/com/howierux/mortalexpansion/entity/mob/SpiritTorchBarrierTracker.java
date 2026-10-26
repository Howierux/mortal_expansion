package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.combat.SpiritSignals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.*;

/** Loaded-chunk index; queries never scan world height or force a chunk load. */
public final class SpiritTorchBarrierTracker {
    private static final Map<Level, Index> LEVELS = new java.util.concurrent.ConcurrentHashMap<>();
    private SpiritTorchBarrierTracker() {}

    private static final class Index {
        final Map<Long, Set<BlockPos>> chunks = new HashMap<>();
        final Map<Long, Integer> columns = new HashMap<>();
        Set<Long> walls = Set.of();
        final Set<Long> wallChunks = new HashSet<>();

        void count(BlockPos pos, int change) {
            long column = SpiritTorchLinks.key(pos.getX(), pos.getZ());
            int count = columns.getOrDefault(column, 0) + change;
            if (count > 0) columns.put(column, count);
            else columns.remove(column);
        }
        void forget(long chunk) {
            Set<BlockPos> torches = chunks.remove(chunk);
            if (torches != null) torches.forEach(pos -> count(pos, -1));
        }
        void rebuild(Level level) {
            walls = SpiritTorchLinks.build(columns.keySet(), column ->
                    chunks.containsKey(chunkKey(SpiritTorchLinks.x(column), SpiritTorchLinks.z(column))));
            wallChunks.clear();
            for(long column:walls)wallChunks.add(chunkKey(SpiritTorchLinks.x(column),SpiritTorchLinks.z(column)));
            // Missing function: consecrated-area cache integration when sanctification is ported.
        }
    }

    private static long chunkKey(int x, int z) {
        return SpiritTorchLinks.key(x >> 4, z >> 4);
    }

    /** Applies the actual block transition, including client prediction and server corrections. */
    public static void onBlockChanged(Level level, BlockPos pos,
            net.minecraft.world.level.block.state.BlockState oldState,
            net.minecraft.world.level.block.state.BlockState newState) {
        boolean wasTorch = SpiritSignals.isTorch(oldState), isTorch = SpiritSignals.isTorch(newState);
        if (wasTorch && !isTorch) removeTorch(level, pos);
        else if (!wasTorch && isTorch) addTorch(level, pos);
    }

    public static void addTorch(Level level, BlockPos pos) {
        Index index = LEVELS.computeIfAbsent(level, unused -> new Index());
        Set<BlockPos> torches = index.chunks.computeIfAbsent(
                chunkKey(pos.getX(), pos.getZ()), unused -> new HashSet<>());
        if (torches.add(pos.immutable())) {
            index.count(pos, 1);
            index.rebuild(level);
        }
    }

    public static void removeTorch(Level level, BlockPos pos) {
        Index index = LEVELS.get(level);
        if (index == null) return;
        Set<BlockPos> torches = index.chunks.get(chunkKey(pos.getX(), pos.getZ()));
        if (torches != null && torches.remove(pos)) {
            index.count(pos, -1);
            index.rebuild(level);
        }
    }

    public static boolean hasBarriers(Level level) {
        Index index = LEVELS.get(level);
        return index != null && !index.walls.isEmpty();
    }

    public static boolean hasBarrierColumn(Level level, int x, int z) {
        Index index = LEVELS.get(level);
        return index != null && index.walls.contains(SpiritTorchLinks.key(x, z));
    }

    public static boolean hasBarriersNear(Level level,BlockPos pos,int radius){
        Index index=LEVELS.get(level);if(index==null || index.wallChunks.isEmpty())return false;
        for(int x=(pos.getX()-radius)>>4;x<=(pos.getX()+radius)>>4;x++)
            for(int z=(pos.getZ()-radius)>>4;z<=(pos.getZ()+radius)>>4;z++)
                if(index.wallChunks.contains(SpiritTorchLinks.key(x,z)))return true;
        return false;
    }

    public static void load(Level level,LevelChunk chunk){
        Index index = LEVELS.computeIfAbsent(level, unused -> new Index());
        int baseX = chunk.getPos().getMinBlockX(), baseZ = chunk.getPos().getMinBlockZ();
        long key = chunkKey(baseX, baseZ);
        index.forget(key);
        Set<BlockPos> torches = new HashSet<>();
        index.chunks.put(key, torches);
        LevelChunkSection[] sections = chunk.getSections();
        // Read the supplied chunk directly: Load can fire before FULL status.
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
            if (!section.getStates().maybeHas(SpiritSignals::isTorch)) continue;
            int baseY = chunk.getMinBuildHeight() + sectionIndex * 16;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        if (!SpiritSignals.isTorch(section.getBlockState(x, y, z))) continue;
                        BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                        torches.add(pos);
                        index.count(pos, 1);
                    }
                }
            }
        }
        index.rebuild(level);
    }

    public static void unload(Level level,LevelChunk chunk){var index=LEVELS.get(level);if(index!=null){index.forget(chunk.getPos().toLong());index.rebuild(level);}}
    public static void clear(Level level){LEVELS.remove(level);}
}
