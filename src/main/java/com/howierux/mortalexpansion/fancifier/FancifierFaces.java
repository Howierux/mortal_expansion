package com.howierux.mortalexpansion.fancifier;

import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** Native face occlusion sees the stored material instead of the generic wrapper. */
public final class FancifierFaces implements BlockGetter {
    private final BlockGetter level;
    private final int opacity;
    public FancifierFaces(BlockGetter level,int opacity) { this.level=level;this.opacity=opacity; }
    @Override public BlockState getBlockState(BlockPos pos) {
        var state=level.getBlockState(pos);
        if (state.getBlock() instanceof com.howierux.mortalexpansion.block.FancifiedBlock
                && level.getBlockEntity(pos) instanceof FancifiedBlockEntity be && be.opacity()==opacity)
            return be.original();
        if(level instanceof net.minecraft.world.level.Level world){int neighbor=FadingWorld.opacity(world,pos);if(neighbor<100 && neighbor!=opacity)return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();}
        return state;
    }
    @Override public BlockEntity getBlockEntity(BlockPos pos) { return level.getBlockEntity(pos); }
    @Override public FluidState getFluidState(BlockPos pos) { return level.getFluidState(pos); }
    @Override public int getHeight() { return level.getHeight(); }
    @Override public int getMinBuildHeight() { return level.getMinBuildHeight(); }
}
