package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.world.entity.Mob.class)
public abstract class SanctificationMixin {@Inject(method="tick",at=@At("TAIL")) private void mortal$sanctify(CallbackInfo ci){com.howierux.mortalexpansion.sanctification.SanctifiedAreaHandler.tick((net.minecraft.world.entity.Mob)(Object)this);}}
