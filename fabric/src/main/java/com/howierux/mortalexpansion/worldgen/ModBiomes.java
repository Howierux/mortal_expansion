package com.howierux.mortalexpansion.worldgen;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/**
 * Resource keys for Mortal Expansion biomes.
 *
 * Biome contents are data-driven under data/mortal_expansion/worldgen/biome.
 */
public final class ModBiomes {
    private ModBiomes() {}

    public static final ResourceKey<Biome> SPIRITWOOD_FOREST = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("mortal_expansion", "spiritwood_forest")
    );

    public static final ResourceKey<Biome> GRAVEYARD = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("mortal_expansion", "graveyard")
    );
}
