package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.combat.CombatRules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Fabric's damage veto cannot change the amount; isolate pre-armor multipliers here. */
@Mixin(LivingEntity.class)
public abstract class SpectralDamageMixin {
 @ModifyVariable(method="hurt",at=@At("HEAD"),argsOnly=true)
 private float spectralAmount(float amount,DamageSource source,float original){return CombatRules.amount((LivingEntity)(Object)this,source,amount);}
 @Inject(method="canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z",at=@At("HEAD"),cancellable=true)
 private void protectGolem(LivingEntity target,CallbackInfoReturnable<Boolean> ci){if(target instanceof com.howierux.mortalexpansion.entity.mob.MortalGolemEntity&&((Object)this instanceof net.minecraft.world.entity.monster.Spider||(Object)this instanceof net.minecraft.world.entity.monster.Creeper||(Object)this instanceof net.minecraft.world.entity.monster.EnderMan))ci.setReturnValue(false);}
}

