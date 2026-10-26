package com.howierux.mortalexpansion.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

/**
 * Independent TerraBlender region for Graveyards.
 *
 * Graveyards use a narrower inland climate envelope than Spiritwood so they
 * remain moderate in size and are more likely to terminate at rivers and
 * major terrain transitions.
 */
public final class MortalExpansionOverworldRegion extends Region {
    public MortalExpansionOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(
            Registry<Biome> registry,
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper
    ) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder ->
                InlandBiomeRegionMapper.replaceGraveyardLand(
                        builder,
                        ModBiomes.GRAVEYARD
                )
        );
    }
}
