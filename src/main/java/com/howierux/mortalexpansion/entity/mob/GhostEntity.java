package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class GhostEntity extends AbstractMortalMonster implements HorizontalPhasingMob {
    public GhostEntity(EntityType<? extends GhostEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(24.0D, 0.28D, 5.0D);
    }
}
