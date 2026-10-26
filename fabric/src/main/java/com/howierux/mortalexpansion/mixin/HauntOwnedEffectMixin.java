package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.*;
import net.minecraft.world.effect.MobEffectInstance;
@Mixin(LivingEntity.class)
public abstract class HauntOwnedEffectMixin {
 @Inject(method="addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD")) private void mortalExpansion$ownership(MobEffectInstance effect,Entity source,CallbackInfoReturnable<Boolean> cir){com.howierux.mortalexpansion.haunt.effects.HauntProtection.externalEffect((LivingEntity)(Object)this,effect);}
}
