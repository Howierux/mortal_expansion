package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class ElementalEntity extends AbstractSpiritMob {
    public ElementalEntity(EntityType<? extends ElementalEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(36.0D, 0.27D);
    }
}
