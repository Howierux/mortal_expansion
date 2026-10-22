package com.howierux.mortalexpansion.fancifier;
import com.howierux.mortalexpansion.block.FancifiedBlock;
import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
/** Texture connections see native materials; opacity affects the later draw/occlusion pass only. */
public final class FancifierConnections implements BlockAndTintGetter {
    private final BlockAndTintGetter level;
    public FancifierConnections(BlockAndTintGetter level){this.level=level;}
    @Override public BlockState getBlockState(BlockPos pos){
        var state=level.getBlockState(pos);
        if(state.getBlock() instanceof FancifiedBlock && level.getBlockEntity(pos) instanceof FancifiedBlockEntity be)return be.original();
        return state;
    }
    @Override public BlockEntity getBlockEntity(BlockPos pos){return level.getBlockEntity(pos);}
    @Override public FluidState getFluidState(BlockPos pos){return getBlockState(pos).getFluidState();}
    @Override public int getHeight(){return level.getHeight();}
    @Override public int getMinBuildHeight(){return level.getMinBuildHeight();}
    @Override public float getShade(Direction direction,boolean shade){return level.getShade(direction,shade);}
    @Override public float getShade(float x,float y,float z,boolean shade){return level.getShade(x,y,z,shade);}
    @Override public int getBlockTint(BlockPos pos,ColorResolver resolver){return level.getBlockTint(pos,resolver);}
    @Override public int getBrightness(LightLayer layer,BlockPos pos){return level.getBrightness(layer,pos);}
    @Override public int getRawBrightness(BlockPos pos,int amount){return level.getRawBrightness(pos,amount);}
    @Override public boolean canSeeSky(BlockPos pos){return level.canSeeSky(pos);}
    @Override public LevelLightEngine getLightEngine(){return level.getLightEngine();}
}
