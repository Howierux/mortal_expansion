package com.howierux.mortalexpansion.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.worldgen.RegionUtils;

import java.util.function.Consumer;

/**
 * TerraBlender region responsible for placing Mortal Expansion biomes into
 * normal Overworld climate generation.
 */
public final class MortalExpansionOverworldRegion extends Region {
    private static final long INLAND_MIN =
            Climate.quantizeCoord(-0.11F);

    public MortalExpansionOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(
            Registry<Biome> registry,
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper
    ) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.FOREST, ModBiomes.SPIRITWOOD_FOREST);

            // Graveyards inherit only the inland subset of vanilla Plains
            // climate points. This excludes ocean/deep-ocean/coast parameter
            // bands, so Graveyards do not replace beach or ocean terrain.
            RegionUtils.getVanillaParameterPoints(Biomes.PLAINS).stream()
                    .filter(point -> point.continentalness().min() >= INLAND_MIN)
                    .forEach(point -> builder.replaceBiome(point, ModBiomes.GRAVEYARD));
        });
    }
}
