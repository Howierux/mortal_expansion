package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Biome tags used to determine the natural haunt baseline of newly generated chunks.
 *
 * These tags are intentionally data-driven so modpacks and datapacks can classify
 * additional biomes without requiring Java compatibility code.
 */
public final class HauntBiomeTags {
    private HauntBiomeTags() {}

    /**
     * Biomes that naturally begin at the Natural haunt tier.
     */
    public static final TagKey<Biome> NATURAL_HAUNT = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "natural_haunt")
    );

    /**
     * Biomes that naturally begin at the Supernatural haunt tier.
     *
     * Spiritwood Forest is intended to be added to this tag when that biome is implemented.
     */
    public static final TagKey<Biome> SUPERNATURAL_HAUNT = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "supernatural_haunt")
    );
}
