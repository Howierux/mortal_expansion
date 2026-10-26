package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.entity.mob.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Observe successful state transitions, including commands and client packet updates, rather than attempted breaks. */
@Mixin(LevelChunk.class)
public abstract class SpectralBarrierBlockMixin {
 @Inject(method="setBlockState",at=@At("RETURN"))
 private void changed(BlockPos pos,BlockState after,boolean moved,CallbackInfoReturnable<BlockState> ci){var before=ci.getReturnValue();if(before==null)return;var level=((LevelChunk)(Object)this).getLevel();HolySaltBarrierTracker.onBlockChanged(level,pos,before,after);SpiritTorchBarrierTracker.onBlockChanged(level,pos,before,after);}
}
