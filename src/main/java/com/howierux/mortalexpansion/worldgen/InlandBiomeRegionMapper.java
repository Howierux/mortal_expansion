package com.howierux.mortalexpansion.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ModifiedVanillaOverworldBuilder;
import terrablender.worldgen.RegionUtils;

import java.util.List;

/**
 * Shared climate mapping for Mortal Expansion's minimum-size biome regions.
 *
 * When one of our low-weight TerraBlender regions is selected, it is allowed
 * to continue across ordinary inland land-biome climate changes rather than
 * disappearing as soon as Forest changes to Plains (or vice versa).
 *
 * Ocean, beach, river, and other water/coastal biomes are intentionally not
 * included.
 */
public final class InlandBiomeRegionMapper {
    private InlandBiomeRegionMapper() {}

    private static final long INLAND_MIN =
            Climate.quantizeCoord(-0.11F);

    private static final List<ResourceKey<Biome>> INLAND_LAND_BIOMES = List.of(
            Biomes.PLAINS,
            Biomes.SUNFLOWER_PLAINS,
            Biomes.FOREST,
            Biomes.FLOWER_FOREST,
            Biomes.BIRCH_FOREST,
            Biomes.OLD_GROWTH_BIRCH_FOREST,
            Biomes.DARK_FOREST,
            Biomes.TAIGA,
            Biomes.OLD_GROWTH_PINE_TAIGA,
            Biomes.OLD_GROWTH_SPRUCE_TAIGA,
            Biomes.SNOWY_TAIGA,
            Biomes.SAVANNA,
            Biomes.SAVANNA_PLATEAU,
            Biomes.WINDSWEPT_SAVANNA,
            Biomes.JUNGLE,
            Biomes.SPARSE_JUNGLE,
            Biomes.BAMBOO_JUNGLE,
            Biomes.DESERT,
            Biomes.SWAMP,
            Biomes.MANGROVE_SWAMP,
            Biomes.BADLANDS,
            Biomes.ERODED_BADLANDS,
            Biomes.WOODED_BADLANDS,
            Biomes.MEADOW,
            Biomes.CHERRY_GROVE,
            Biomes.GROVE,
            Biomes.SNOWY_PLAINS,
            Biomes.ICE_SPIKES,
            Biomes.WINDSWEPT_HILLS,
            Biomes.WINDSWEPT_GRAVELLY_HILLS,
            Biomes.WINDSWEPT_FOREST,
            Biomes.STONY_PEAKS,
            Biomes.JAGGED_PEAKS,
            Biomes.FROZEN_PEAKS
    );

    public static void replaceInlandLand(
            ModifiedVanillaOverworldBuilder builder,
            ResourceKey<Biome> replacement
    ) {
        for (ResourceKey<Biome> vanillaBiome : INLAND_LAND_BIOMES) {
            RegionUtils.getVanillaParameterPoints(vanillaBiome).stream()
                    .filter(point -> point.continentalness().min() >= INLAND_MIN)
                    .forEach(point -> builder.replaceBiome(point, replacement));
        }
    }
}
