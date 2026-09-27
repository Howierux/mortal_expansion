package com.howierux.mortalexpansion.haunt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Calculates the biome-derived starting haunt tier for a newly generated chunk.
 *
 * The calculation samples several surface locations instead of relying on only
 * the chunk center, because a chunk can overlap multiple biomes.
 */
public final class NaturalHauntGenerator {
    private NaturalHauntGenerator() {}

    private static final int[] SAMPLE_OFFSETS = {2, 8, 13};

    public static HauntTier determineInitialTier(ServerLevel level, ChunkPos chunkPos) {
        HauntTier highest = HauntTier.VOID;

        for (int localX : SAMPLE_OFFSETS) {
            for (int localZ : SAMPLE_OFFSETS) {
                int blockX = chunkPos.getMinBlockX() + localX;
                int blockZ = chunkPos.getMinBlockZ() + localZ;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, blockX, blockZ);

                BlockPos samplePos = new BlockPos(blockX, surfaceY, blockZ);
                Holder<net.minecraft.world.level.biome.Biome> biome = level.getBiome(samplePos);

                HauntTier sampleTier = tierForBiome(biome);
                if (sampleTier.id() > highest.id()) {
                    highest = sampleTier;

                    if (highest == HauntTier.SUPERNATURAL) {
                        return highest;
                    }
                }
            }
        }

        return highest;
    }

    private static HauntTier tierForBiome(Holder<net.minecraft.world.level.biome.Biome> biome) {
        if (biome.is(HauntBiomeTags.SUPERNATURAL_HAUNT)) {
            return HauntTier.SUPERNATURAL;
        }

        if (biome.is(HauntBiomeTags.NATURAL_HAUNT)) {
            return HauntTier.NATURAL;
        }

        return HauntTier.VOID;
    }
}
