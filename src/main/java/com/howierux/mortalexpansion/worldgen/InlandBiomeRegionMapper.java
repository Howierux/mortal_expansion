package com.howierux.mortalexpansion.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ModifiedVanillaOverworldBuilder;
import terrablender.worldgen.RegionUtils;

import java.util.List;

/**
 * Shared climate mapping for Mortal Expansion biomes.
 *
 * Spiritwood is intentionally allowed across a broad inland land envelope.
 * Graveyard uses a narrower cemetery-friendly envelope so it remains a
 * moderate patch and is more likely to terminate at rivers and terrain changes.
 */
public final class InlandBiomeRegionMapper {
    private InlandBiomeRegionMapper() {}

    private static final long INLAND_MIN =
            Climate.quantizeCoord(-0.11F);

    private static final List<ResourceKey<Biome>> SPIRITWOOD_LAND_BIOMES = List.of(
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
            Biomes.MEADOW,
            Biomes.CHERRY_GROVE,
            Biomes.GROVE,
            Biomes.WINDSWEPT_FOREST
    );

    private static final List<ResourceKey<Biome>> GRAVEYARD_LAND_BIOMES = List.of(
            Biomes.PLAINS,
            Biomes.SUNFLOWER_PLAINS,
            Biomes.MEADOW
    );

    public static void replaceSpiritwoodLand(
            ModifiedVanillaOverworldBuilder builder,
            ResourceKey<Biome> replacement
    ) {
        replace(builder, replacement, SPIRITWOOD_LAND_BIOMES);
    }

    public static void replaceGraveyardLand(
            ModifiedVanillaOverworldBuilder builder,
            ResourceKey<Biome> replacement
    ) {
        replace(builder, replacement, GRAVEYARD_LAND_BIOMES);
    }

    private static void replace(
            ModifiedVanillaOverworldBuilder builder,
            ResourceKey<Biome> replacement,
            List<ResourceKey<Biome>> vanillaBiomes
    ) {
        for (ResourceKey<Biome> vanillaBiome : vanillaBiomes) {
            RegionUtils.getVanillaParameterPoints(vanillaBiome).stream()
                    .filter(point -> point.continentalness().min() >= INLAND_MIN)
                    .forEach(point -> builder.replaceBiome(point, replacement));
        }
    }
}
