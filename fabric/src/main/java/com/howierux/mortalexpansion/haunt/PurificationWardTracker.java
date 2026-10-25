package com.howierux.mortalexpansion.haunt;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Tracks the cleansing coverage of loaded Purification Wards.
 *
 * Each Ward protects and cleanses its own chunk plus the eight surrounding
 * chunks. The Fabric PurificationWards ticket manager keeps this coverage ticking.
 */
public final class PurificationWardTracker {
    private PurificationWardTracker() {}

    private static final Map<ServerLevel, Set<BlockPos>> LOADED_WARDS =
            new WeakHashMap<>();

    public static void add(ServerLevel level, BlockPos pos) {
        LOADED_WARDS
                .computeIfAbsent(level, ignored -> new HashSet<>())
                .add(pos.immutable());
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        Set<BlockPos> wards = LOADED_WARDS.get(level);
        if (wards != null) {
            wards.remove(pos);
            if (wards.isEmpty()) {
                LOADED_WARDS.remove(level);
            }
        }
    }

    public static boolean isChunkProtected(ServerLevel level, ChunkPos chunkPos) {
        Set<BlockPos> wards = LOADED_WARDS.get(level);
        if (wards == null || wards.isEmpty()) {
            return false;
        }

        for (BlockPos wardPos : wards) {
            ChunkPos wardChunk = new ChunkPos(wardPos);
            if (Math.abs(wardChunk.x - chunkPos.x) <= 1
                    && Math.abs(wardChunk.z - chunkPos.z) <= 1) {
                return true;
            }
        }

        return false;
    }

    public static Set<Long> protectedLoadedChunks(
            ServerLevel level,
            Set<Long> loadedChunkKeys
    ) {
        Set<Long> protectedChunks = new HashSet<>();
        Set<BlockPos> wards = LOADED_WARDS.get(level);

        if (wards == null || wards.isEmpty()) {
            return protectedChunks;
        }

        for (BlockPos wardPos : wards) {
            ChunkPos wardChunk = new ChunkPos(wardPos);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    long key = ChunkPos.asLong(
                            wardChunk.x + dx,
                            wardChunk.z + dz
                    );

                    if (loadedChunkKeys.contains(key)) {
                        protectedChunks.add(key);
                    }
                }
            }
        }

        return protectedChunks;
    }
    public static void clear(){LOADED_WARDS.clear();}
}
