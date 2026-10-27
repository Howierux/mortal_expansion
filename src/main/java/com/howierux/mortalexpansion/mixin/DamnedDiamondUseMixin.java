package com.howierux.mortalexpansion.mixin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Vanilla diamonds have zero use duration; Greed needs native held-use tracking, not client timers. */
@Mixin(Item.class)
public abstract class DamnedDiamondUseMixin {
    @Inject(method="getUseDuration",at=@At("HEAD"),cancellable=true)
    private void mortal$chargeDiamond(ItemStack stack,LivingEntity entity,CallbackInfoReturnable<Integer> ci){
        if(stack.is(Items.DIAMOND) && entity instanceof net.minecraft.world.entity.player.Player p
                && com.howierux.mortalexpansion.guide.DamnedSecret.GREED.unlocked(p))ci.setReturnValue(72000);
    }
}
