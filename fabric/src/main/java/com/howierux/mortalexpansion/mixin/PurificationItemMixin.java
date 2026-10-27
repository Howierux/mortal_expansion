package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.world.entity.item.ItemEntity.class)
public abstract class PurificationItemMixin {@Inject(method="tick",at=@At("TAIL")) private void mortal$cauldron(CallbackInfo ci){var e=(net.minecraft.world.entity.item.ItemEntity)(Object)this;if(!e.level().isClientSide&&e.tickCount%10==0)com.howierux.mortalexpansion.haunt.NaturalPurificationHandler.itemTick(e);}}
