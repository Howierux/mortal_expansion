package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Client chunk writes skip block onPlace/onRemove; observe completed transitions instead. */
@Mixin(Level.class)
public abstract class SpiritTorchClientUpdatesMixin {
    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    private void mortalExpansion$updateClientTorchIndex(BlockPos pos, BlockState oldState,
            BlockState newState, CallbackInfo callback) {
        Level level = (Level)(Object)this;
        if (level.isClientSide) SpiritTorchBarrierTracker.onBlockChanged(level, pos, oldState, newState);
    }
}
