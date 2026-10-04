package com.howierux.mortalexpansion.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class IntangibleEntities {
    private IntangibleEntities() {}

    public static boolean isIntangible(Entity entity) {
        if (entity instanceof net.minecraft.world.entity.player.Player player
                && com.howierux.mortalexpansion.haunt.effects.HauntProtection.fullGarb(player)) return true;
        if (entity instanceof DynamicIntangible dynamic
                && dynamic.isCurrentlyIntangible()) {
            return true;
        }

        return isIntangible(entity.getType());
    }

    public static boolean isIntangible(EntityType<?> entityType) {
        return entityType.is(ModEntityTags.INTANGIBLE);
    }
}
