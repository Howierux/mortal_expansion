package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.combat.GhostGarbBreathing;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
/** Only the air branch sees walls as water. Native oxygen bonus, water breathing and refill remain intact. */
@Mixin(LivingEntity.class)
public abstract class GarbBreathingMixin {
 @ModifyExpressionValue(method="baseTick",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
 private boolean mortalExpansion$wallAir(boolean underwater){return underwater||((Object)this instanceof Player p&&GhostGarbBreathing.headInsideWall(p));}
}
