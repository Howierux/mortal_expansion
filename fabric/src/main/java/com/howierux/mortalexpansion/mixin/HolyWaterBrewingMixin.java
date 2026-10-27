package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;import net.minecraft.world.item.*;
/** Fabric's recipe builder cannot create a potion component from empty glass bottles. Keep native brew timing and bucket remainder. */
@Mixin(net.minecraft.world.item.alchemy.PotionBrewing.class)
public abstract class HolyWaterBrewingMixin {
 @Inject(method="isIngredient",at=@At("HEAD"),cancellable=true) private void mortal$ingredient(ItemStack s,CallbackInfoReturnable<Boolean> ci){if(s.is(com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET))ci.setReturnValue(true);}
 @Inject(method="hasMix",at=@At("HEAD"),cancellable=true) private void mortal$recipe(ItemStack input,ItemStack ingredient,CallbackInfoReturnable<Boolean> ci){if(com.howierux.mortalexpansion.haunt.HolyWaterBrewing.recipe(input,ingredient))ci.setReturnValue(true);}
 @Inject(method="mix",at=@At("HEAD"),cancellable=true) private void mortal$mix(ItemStack ingredient,ItemStack input,CallbackInfoReturnable<ItemStack> ci){if(com.howierux.mortalexpansion.haunt.HolyWaterBrewing.recipe(input,ingredient))ci.setReturnValue(com.howierux.mortalexpansion.haunt.HolyWaterBrewing.bottle());}
}
