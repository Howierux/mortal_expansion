package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class SpiritEntity extends AbstractSpiritMob {
    public SpiritEntity(EntityType<? extends SpiritEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(18.0D, 0.24D);
    }
}
