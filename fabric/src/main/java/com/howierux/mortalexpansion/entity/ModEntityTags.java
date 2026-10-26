package com.howierux.mortalexpansion.entity;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * Entity type tags used by Mortal Expansion's supernatural systems.
 */
public final class ModEntityTags {
    private ModEntityTags() {}

    /**
     * Authoritative marker for entities that are considered intangible.
     *
     * Combat rules, detection tools, targeting logic, and future spectral
     * mechanics should check this tag instead of hard-coding individual mobs.
     */
    public static final TagKey<EntityType<?>> INTANGIBLE = TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("mortal_expansion", "intangible")
    );
}
