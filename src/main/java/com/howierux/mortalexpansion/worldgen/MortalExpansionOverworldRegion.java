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

import java.util.function.Consumer;

/**
 * TerraBlender region responsible for placing Mortal Expansion biomes into
 * normal Overworld climate generation.
 *
 * For the first Spiritwood implementation, Spiritwood Forest occupies forest
 * climate points whenever this low-weight Mortal Expansion region is selected.
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
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.FOREST, ModBiomes.SPIRITWOOD_FOREST);
            builder.replaceBiome(Biomes.PLAINS, ModBiomes.GRAVEYARD);
        });
    }
}
