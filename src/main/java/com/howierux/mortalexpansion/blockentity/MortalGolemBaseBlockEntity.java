package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.block.MortalGolemBaseBlock;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class MortalGolemBaseBlockEntity extends BlockEntity {
    private int flesh;
    public MortalGolemBaseBlockEntity(BlockPos pos,BlockState state){super(ModBlockEntities.MORTAL_GOLEM_BASE.get(),pos,state);}
    public int flesh(){return flesh;}
    public int addFlesh(int amount){
        int added=Math.min(Math.max(0,amount),64-flesh);flesh+=added;
        if(added>0){
            setChanged();
            if(level!=null && !level.isClientSide && getBlockState().hasProperty(MortalGolemBaseBlock.FLESH_STAGE))
                level.setBlock(worldPosition,getBlockState().setValue(MortalGolemBaseBlock.FLESH_STAGE,flesh/16),3);
        }
        return added;
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putInt("Flesh",flesh);}
    @Override public void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);flesh=Math.clamp(tag.getInt("Flesh"),0,64);}
}
