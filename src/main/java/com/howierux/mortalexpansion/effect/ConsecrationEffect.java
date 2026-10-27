package com.howierux.mortalexpansion.effect;

import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;

/** Instant delivery uses native drink/splash targeting; blessing and cleansing remain authoritative in one place. */
public final class ConsecrationEffect extends MobEffect {
    public ConsecrationEffect(){super(MobEffectCategory.BENEFICIAL,0xD3F7FF);}
    @Override public boolean isInstantenous(){return true;}
    @Override public void applyInstantenousEffect(@Nullable Entity direct,@Nullable Entity owner,LivingEntity target,int amplifier,double potency){
        if(potency>0 && target instanceof ServerPlayer player && player.isAlive() && !player.isSpectator())HauntProtection.bless(player);
    }
}
