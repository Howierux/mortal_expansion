package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.HitResult;
@Mixin(ThrownPotion.class)
public abstract class GolemPotionMixin {@Inject(method="onHit",at=@At("HEAD")) private void mortalExpansion$regenerateFrame(HitResult hit,CallbackInfo ci){var potion=(ThrownPotion)(Object)this;if(potion.level() instanceof net.minecraft.server.level.ServerLevel level)com.howierux.mortalexpansion.haunt.MortalGolemFormation.regeneration(level,potion.getItem(),hit.getLocation());}}
