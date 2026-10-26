package com.howierux.mortalexpansion.fancifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
public record FancifierFaces(BlockGetter level,int opacity) implements BlockGetter {
 public net.minecraft.world.level.block.state.BlockState getBlockState(BlockPos pos){if(level instanceof net.minecraft.world.level.Level world){int alpha=FadingWorld.opacity(world,pos);if(alpha<100&&alpha!=opacity)return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();}return level.getBlockState(pos);}
 public net.minecraft.world.level.block.entity.BlockEntity getBlockEntity(BlockPos pos){return level.getBlockEntity(pos);}public net.minecraft.world.level.material.FluidState getFluidState(BlockPos pos){return level.getFluidState(pos);}public int getHeight(){return level.getHeight();}public int getMinBuildHeight(){return level.getMinBuildHeight();}
}
