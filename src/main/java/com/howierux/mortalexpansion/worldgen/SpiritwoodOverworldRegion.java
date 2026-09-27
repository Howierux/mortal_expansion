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
 * Independent TerraBlender region for Spiritwood Forest.
 *
 * This gives Spiritwood its own region-selection odds. Vanilla Forest remains
 * untouched unless this low-weight Spiritwood region itself is selected.
 */
public final class SpiritwoodOverworldRegion extends Region {
    private static final long INLAND_MIN =
            Climate.quantizeCoord(-0.11F);

    public SpiritwoodOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(
            Registry<Biome> registry,
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper
    ) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            RegionUtils.getVanillaParameterPoints(Biomes.FOREST).stream()
                    .filter(point -> point.continentalness().min() >= INLAND_MIN)
                    .forEach(point -> builder.replaceBiome(
                            point,
                            ModBiomes.SPIRITWOOD_FOREST
                    ));
        });
    }
}
