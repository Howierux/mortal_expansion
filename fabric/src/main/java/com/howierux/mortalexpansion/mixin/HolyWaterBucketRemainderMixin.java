package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
/** Native doBrew queries getItem after shrinking to zero, losing a single bucket's remainder. Capture before consumption. */
@Mixin(net.minecraft.world.level.block.entity.BrewingStandBlockEntity.class)
public abstract class HolyWaterBucketRemainderMixin {
 @WrapMethod(method="doBrew") private static void mortal$returnBucket(net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos,net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> items,Operation<Void> original){boolean bucket=items.get(3).is(com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET);original.call(level,pos,items);if(bucket&&items.get(3).isEmpty())items.set(3,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BUCKET));}
}
