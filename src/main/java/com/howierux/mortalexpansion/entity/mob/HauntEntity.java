package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public class HauntEntity extends AbstractMortalMonster implements HorizontalPhasingMob {
    public HauntEntity(EntityType<? extends HauntEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(60.0D, 0.30D, 9.0D);
    }
}
