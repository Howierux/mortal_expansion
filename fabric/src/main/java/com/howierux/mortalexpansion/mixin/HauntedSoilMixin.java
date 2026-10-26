package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Vanilla hard-codes Blocks.FARMLAND in these plants; a FarmBlock subclass alone is insufficient. */
@Mixin({CropBlock.class,StemBlock.class,AttachedStemBlock.class,PitcherCropBlock.class})
public abstract class HauntedSoilMixin {
 @Inject(method="mayPlaceOn",at=@At("HEAD"),cancellable=true) private void mortalExpansion$soil(BlockState state,BlockGetter level,BlockPos pos,CallbackInfoReturnable<Boolean> cir){if(state.is(CatalogBlocks.HAUNTED_FARMLAND))cir.setReturnValue(true);}
}
