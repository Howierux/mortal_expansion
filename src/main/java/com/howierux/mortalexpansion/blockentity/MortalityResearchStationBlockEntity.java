package com.howierux.mortalexpansion.blockentity;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.haunt.MortalityResearch;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
public final class MortalityResearchStationBlockEntity extends BlockEntity {
    private final ItemStackHandler items=new ItemStackHandler(1){@Override public boolean isItemValid(int slot,ItemStack item){return MortalityResearch.amount(item)>0;}@Override protected void onContentsChanged(int slot){setChanged();}};
    public MortalityResearchStationBlockEntity(BlockPos pos,BlockState state){super(ModBlockEntities.MORTALITY_RESEARCH_STATION.get(),pos,state);}
    public ItemStackHandler getItems(){return items;}
    public void dropInventory(ServerLevel level){Containers.dropItemStack(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,items.getStackInSlot(0).copy());items.setStackInSlot(0,ItemStack.EMPTY);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.put("Items",items.serializeNBT(registries));}
    @Override public void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);if(tag.contains("Items"))items.deserializeNBT(registries,tag.getCompound("Items"));}
}
