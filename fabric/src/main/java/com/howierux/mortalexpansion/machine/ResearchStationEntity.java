package com.howierux.mortalexpansion.machine;
import com.howierux.mortalexpansion.guide.*;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class ResearchStationEntity extends BlockEntity implements Container,ExtendedScreenHandlerFactory<BlockPos> {
 private ItemStack input=ItemStack.EMPTY;
 public ResearchStationEntity(BlockPos pos,BlockState state){super(Machines.RESEARCH,pos,state);}
 public ContainerData data(Player p){return new ContainerData(){public int get(int i){return p instanceof ServerPlayer s?(i==0?MortalityResearch.bonus(s):MortalityResearch.ecto(s)):0;}public void set(int i,int value){}public int getCount(){return 2;}};}
 @Override public int getContainerSize(){return 1;}@Override public boolean isEmpty(){return input.isEmpty();}@Override public ItemStack getItem(int slot){return input;}
 @Override public ItemStack removeItem(int slot,int count){var removed=input.split(count);setChanged();return removed;}
 @Override public ItemStack removeItemNoUpdate(int slot){var old=input;input=ItemStack.EMPTY;return old;}
 @Override public void setItem(int slot,ItemStack stack){input=stack;setChanged();}
 @Override public void clearContent(){input=ItemStack.EMPTY;setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&MortalityResearch.amount(stack)>0;}
 @Override public boolean stillValid(Player p){return !isRemoved()&&level!=null&&level.getBlockEntity(worldPosition)==this&&p.distanceToSqr(worldPosition.getCenter())<=64;}
 public void dropInventory(){if(level instanceof ServerLevel)Containers.dropItemStack(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,input);clearContent();}
 @Override public Component getDisplayName(){return Component.translatable("block.mortal_expansion.mortality_research_station");}
 @Override public BlockPos getScreenOpeningData(ServerPlayer p){return worldPosition;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player p){return new ResearchMenu(id,inv,this,data(p));}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(!input.isEmpty())tag.put("Input",input.save(lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);input=ItemStack.parseOptional(lookup,tag.getCompound("Input"));}
}
