package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class ElementalEntity extends AbstractSpiritMob implements net.minecraft.world.entity.monster.RangedAttackMob {
    public ElementalEntity(EntityType<? extends ElementalEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void registerGoals(){
        super.registerGoals();
        goalSelector.addGoal(2,new net.minecraft.world.entity.ai.goal.RangedAttackGoal(this,1.0,40,16.0F));
        targetSelector.addGoal(1,new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
    }
    @Override public void performRangedAttack(net.minecraft.world.entity.LivingEntity target,float distanceFactor){
        com.howierux.mortalexpansion.entity.SpectralBoltEntity.fire(this,target);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(36.0D, 0.27D);
    }
}
