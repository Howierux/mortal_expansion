package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class GhostEntity extends AbstractMortalMonster implements HorizontalPhasingMob {
    public GhostEntity(EntityType<? extends GhostEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void registerGoals(){
        super.registerGoals();
        targetSelector.addGoal(4,new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(
                this,net.minecraft.world.entity.Mob.class,10,true,false,
                target->!isAllied() && (target instanceof SpiritEntity || target instanceof ElementalEntity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(24.0D, 0.28D, 5.0D);
    }
}
