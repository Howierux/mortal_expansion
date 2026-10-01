package com.howierux.mortalexpansion.haunt;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Calculates the biome-derived starting haunt tier for a chunk.
 *
 * This reads biome data directly from the chunk instead of querying the level.
 * That is important during world generation: asking ServerLevel for height or
 * neighboring chunk data from a chunk-load event can recursively request more
 * generation work and stall the spawn-region generator.
 */
public final class NaturalHauntGenerator {
    private NaturalHauntGenerator() {}

    private static final int[] SAMPLE_OFFSETS = {2, 8, 13};

    /**
     * Y-level used for the first-pass surface-biome classification.
     *
     * Minecraft stores biomes in 4x4x4 quart cells. For the current purpose we
     * sample around ordinary sea-level terrain without touching heightmaps.
     * Later, if needed, this can be expanded into a more detailed vertical
     * classification without requesting other chunks.
     */
    private static final int SAMPLE_Y = 64;

    public static HauntTier determineInitialTier(ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        HauntTier highest = HauntTier.NATURAL;
        int quartY = QuartPos.fromBlock(SAMPLE_Y);

        for (int localX : SAMPLE_OFFSETS) {
            for (int localZ : SAMPLE_OFFSETS) {
                int blockX = chunkPos.getMinBlockX() + localX;
                int blockZ = chunkPos.getMinBlockZ() + localZ;

                int quartX = QuartPos.fromBlock(blockX);
                int quartZ = QuartPos.fromBlock(blockZ);

                Holder<Biome> biome = chunk.getNoiseBiome(quartX, quartY, quartZ);

                HauntTier sampleTier = tierForBiome(biome);
                if (sampleTier.id() > highest.id()) {
                    highest = sampleTier;

                    // Check every sample: a later Graveyard sample outranks Spiritwood.
                }
            }
        }

        return highest;
    }

    private static HauntTier tierForBiome(Holder<Biome> biome) {
        if (biome.is(HauntBiomeTags.DEAD_RISING_HAUNT)) {
            return HauntTier.DEAD_RISING;
        }

        if (biome.is(HauntBiomeTags.SUPERNATURAL_HAUNT)) {
            return HauntTier.SUPERNATURAL;
        }

        if (biome.is(HauntBiomeTags.NATURAL_HAUNT)) {
            return HauntTier.NATURAL;
        }

        return HauntTier.NATURAL;
    }
}
