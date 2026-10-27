package com.howierux.mortalexpansion.effect;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public final class SanctifiedEffect extends MobEffect {
    public SanctifiedEffect() {
        super(MobEffectCategory.NEUTRAL, 0xE5E5E5);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Entity age keeps the interval stable while the enclosure refreshes duration.
        if (!entity.level().isClientSide && entity.tickCount % 40 == 0
                && entity.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)) {
            entity.hurt(new net.minecraft.world.damagesource.DamageSource(entity.registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(ModDamageTypes.SANCTIFICATION)), 1.0F);
        }
        return true;
    }
}
