package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.registry.ModItems;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla smelting ignores crafting remainders. Return the vessel only after a successful burn.
 * This shared path covers vanilla furnaces, mod furnaces, and instant Haunt cooking.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FluidBucketSmeltingMixin {
    @Inject(method = "burn", at = @At("HEAD"))
    private static void mortalExpansion$captureBucket(RegistryAccess registries, RecipeHolder<?> recipe,
            NonNullList<ItemStack> inventory, int max, AbstractFurnaceBlockEntity furnace,
            CallbackInfoReturnable<Boolean> cir, @Share("meBucket") LocalBooleanRef bucket) {
        ItemStack input = inventory.get(0);
        bucket.set(input.is(ModItems.HOLY_WATER_BUCKET.get()) || input.is(ModItems.LIQUID_ECTOPLASM_BUCKET.get())
                || input.is(ModItems.PURIFIED_ECTOPLASM_BUCKET.get()));
    }
    @Inject(method = "burn", at = @At("RETURN"))
    private static void mortalExpansion$returnBucket(RegistryAccess registries, RecipeHolder<?> recipe,
            NonNullList<ItemStack> inventory, int max, AbstractFurnaceBlockEntity furnace,
            CallbackInfoReturnable<Boolean> cir, @Share("meBucket") LocalBooleanRef bucket) {
        if (!cir.getReturnValue() || !bucket.get()) return;
        if (inventory.get(0).isEmpty()) inventory.set(0, new ItemStack(Items.BUCKET));
        else if (furnace.getLevel() != null) net.minecraft.world.Containers.dropItemStack(furnace.getLevel(),
                furnace.getBlockPos().getX() + .5, furnace.getBlockPos().getY() + 1, furnace.getBlockPos().getZ() + .5, new ItemStack(Items.BUCKET));
    }
}
