package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
/** Keep ordinary crop growth rates and hydration bonuses on haunted soil, in addition to its own pulse. */
@Mixin(CropBlock.class)
public abstract class HauntedCropGrowthMixin {
 @WrapOperation(method="getGrowthSpeed",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
 private static boolean mortalExpansion$growth(BlockState state,Block block,Operation<Boolean> original){return original.call(state,block)||(block==Blocks.FARMLAND&&state.is(CatalogBlocks.HAUNTED_FARMLAND));}
}
