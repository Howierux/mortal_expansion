package com.howierux.mortalexpansion.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Shared API for querying Mortal Expansion's intangible classification.
 *
 * Keeping callers behind this helper makes it possible to expand the system
 * later without rewriting every combat, spawn, or detector feature.
 */
public final class IntangibleEntities {
    private IntangibleEntities() {}

    public static boolean isIntangible(Entity entity) {
        return isIntangible(entity.getType());
    }

    public static boolean isIntangible(EntityType<?> entityType) {
        return entityType.is(ModEntityTags.INTANGIBLE);
    }
}
