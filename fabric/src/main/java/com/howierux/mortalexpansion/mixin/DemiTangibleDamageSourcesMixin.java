package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.combat.DemiTangibleDamage;
import com.howierux.mortalexpansion.effect.ModDamageTypes;
import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Damage events cannot replace their source; convert melee factories before damage processing. */
@Mixin(DamageSources.class)
public abstract class DemiTangibleDamageSourcesMixin {
    @Shadow @Final public Registry<DamageType> damageTypes;

    @Inject(method = "playerAttack", at = @At("RETURN"), cancellable = true)
    private void mortalExpansion$playerMagic(Player player, CallbackInfoReturnable<DamageSource> callback) {
        if (DemiTangibleDamage.isDemiTangibleWeapon(player.getMainHandItem())) {
            callback.setReturnValue(new DamageSource(damageTypes.getHolderOrThrow(ModDamageTypes.DEMI_TANGIBLE_PLAYER), player));
        }
    }

    @Inject(method = "mobAttack", at = @At("RETURN"), cancellable = true)
    private void mortalExpansion$mobMagic(LivingEntity mob, CallbackInfoReturnable<DamageSource> callback) {
        if (mob instanceof com.howierux.mortalexpansion.entity.mob.MortalGolemEntity
                || DemiTangibleDamage.isDemiTangibleWeapon(mob.getMainHandItem())) {
            callback.setReturnValue(new DamageSource(damageTypes.getHolderOrThrow(ModDamageTypes.DEMI_TANGIBLE_MOB), mob));
        }
    }
}
