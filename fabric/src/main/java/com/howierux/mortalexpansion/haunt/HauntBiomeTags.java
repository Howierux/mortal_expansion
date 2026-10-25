package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.MortalExpansionFabric;
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
            ResourceLocation.fromNamespaceAndPath(MortalExpansionFabric.MOD_ID, "natural_haunt")
    );

    /**
     * Biomes that naturally begin at the Supernatural haunt tier.
     */
    public static final TagKey<Biome> SUPERNATURAL_HAUNT = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(MortalExpansionFabric.MOD_ID, "supernatural_haunt")
    );

    /** Biomes with a Dead Rising starting tier and natural simulation floor. */
    public static final TagKey<Biome> DEAD_RISING_HAUNT = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(MortalExpansionFabric.MOD_ID, "dead_rising_haunt")
    );
}
