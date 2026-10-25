package com.howierux.mortalexpansion.machine;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.HauntManager;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import java.util.*;

public final class PurificationStationEntity extends BlockEntity implements WorldlyContainer,ExtendedScreenHandlerFactory<BlockPos> {
 public static final int PROCESS_TICKS=60,FE_PER_CRAFT=100000;
 public static final TagKey<Item> REAGENTS=TagKey.create(Registries.ITEM,Catalog.id("purification_reagents"));
 private final List<ItemStack> items=new ArrayList<>(Collections.nCopies(4,ItemStack.EMPTY));
 private int progress,feUsed;
 public final SimpleEnergyStorage energy=new SimpleEnergyStorage(400000,10000,0){@Override protected void onFinalCommit(){setChanged();}};
 public final SingleVariantStorage<FluidVariant> water=new SingleVariantStorage<>() {
  @Override protected FluidVariant getBlankVariant(){return FluidVariant.blank();}
  @Override protected long getCapacity(FluidVariant v){return 10*FluidConstants.BUCKET;}
  @Override protected boolean canInsert(FluidVariant v){return v.equals(FluidVariant.of(Fluids.WATER));}
  @Override protected boolean canExtract(FluidVariant v){return false;}
  @Override protected void onFinalCommit(){setChanged();}
 };
 public PurificationStationEntity(BlockPos pos,BlockState state){super(Machines.STATION,pos,state);}
 public int progress(){return progress;}
 public int feUsed(){return feUsed;}
 private boolean outputSpace(){var stack=items.get(2);return stack.isEmpty()||stack.is(CatalogItems.PURE_ECTOPLASM)&&stack.getCount()<stack.getMaxStackSize();}
 public void tick(ServerLevel level){
  if(items.get(3).is(Items.WATER_BUCKET)&&water.amount<=water.getCapacity()-FluidConstants.BUCKET){
   water.variant=FluidVariant.of(Fluids.WATER);water.amount+=FluidConstants.BUCKET;items.set(3,new ItemStack(Items.BUCKET));setChanged();
  }
  if(progress==0){
   if(items.get(0).is(CatalogItems.RAW_ECTOPLASM)&&items.get(1).is(REAGENTS)&&water.amount>=FluidConstants.BUCKET&&outputSpace()){
    items.get(0).shrink(1);items.get(1).shrink(1);water.amount-=FluidConstants.BUCKET;if(water.amount==0)water.variant=FluidVariant.blank();progress=1;feUsed=0;setChanged();
   }
   return;
  }
  if(!outputSpace())return;
  int target=(int)Math.ceil((double)FE_PER_CRAFT*(progress+1)/PROCESS_TICKS),needed=Math.max(0,target-feUsed);
  int consumed=(int)Math.min(needed,energy.amount);energy.amount-=consumed;feUsed+=consumed;
  if(consumed<needed){setChanged();return;}
  if(++progress>=PROCESS_TICKS){
   if(items.get(2).isEmpty())items.set(2,new ItemStack(CatalogItems.PURE_ECTOPLASM));else items.get(2).grow(1);
   HauntManager.addPoints(level,worldPosition,1);progress=feUsed=0;
  }
  setChanged();
 }
 public ContainerData menuData(){return new ContainerData(){
  public int get(int index){int value=switch(index/2){case 0->(int)(water.amount/FluidMachineEntity.MB);case 1->10000;case 2->(int)energy.amount;case 3->400000;case 4->progress;case 5->PROCESS_TICKS;case 6->feUsed;default->0;};return(index%2==0?value:value>>>16)&65535;}
  public void set(int index,int value){}public int getCount(){return PurificationStationMenu.DATA_COUNT;}
 };}
 @Override public int getContainerSize(){return 4;}
 @Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}
 @Override public ItemStack getItem(int slot){return items.get(slot);}
 @Override public ItemStack removeItem(int slot,int count){var stack=items.get(slot).split(count);if(!stack.isEmpty())setChanged();return stack;}
 @Override public ItemStack removeItemNoUpdate(int slot){return items.set(slot,ItemStack.EMPTY);}
 @Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);setChanged();}
 @Override public void clearContent(){Collections.fill(items,ItemStack.EMPTY);setChanged();}
 @Override public boolean stillValid(Player p){return level!=null&&level.getBlockEntity(worldPosition)==this&&p.distanceToSqr(worldPosition.getCenter())<=64;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return switch(slot){case 0->stack.is(CatalogItems.RAW_ECTOPLASM);case 1->stack.is(REAGENTS);case 3->stack.is(Items.WATER_BUCKET);default->false;};}
 @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3};}
 @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==2||slot==3&&stack.is(Items.BUCKET);}
 public void dropInventory(){if(level instanceof ServerLevel)for(var stack:items)Containers.dropItemStack(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,stack);clearContent();}
 @Override public Component getDisplayName(){return Component.translatable("gui.mortal_expansion.purification_station.title");}
 @Override public BlockPos getScreenOpeningData(ServerPlayer p){return worldPosition;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new PurificationStationMenu(id,inventory,this,menuData());}
 public CompoundTag portable(){var tag=new CompoundTag();tag.putLong("StoredWater",water.amount);tag.putLong("StoredEnergy",energy.amount);return tag;}
 public void restorePortable(CompoundTag tag){water.amount=Math.max(0,Math.min(water.getCapacity(),tag.getLong("StoredWater")));water.variant=water.amount>0?FluidVariant.of(Fluids.WATER):FluidVariant.blank();energy.amount=Math.max(0,Math.min(400000,tag.getLong("StoredEnergy")));setChanged();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
  super.saveAdditional(tag,lookup);tag.putLong("Energy",energy.amount);tag.putLong("Water",water.amount);tag.putInt("Progress",progress);tag.putInt("FeUsed",feUsed);
  for(int i=0;i<4;i++)if(!items.get(i).isEmpty())tag.put("Slot"+i,items.get(i).save(lookup));
 }
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
  super.loadAdditional(tag,lookup);energy.amount=Math.max(0,Math.min(400000,tag.getLong("Energy")));water.amount=Math.max(0,Math.min(water.getCapacity(),tag.getLong("Water")));water.variant=water.amount>0?FluidVariant.of(Fluids.WATER):FluidVariant.blank();
  progress=Math.max(0,Math.min(PROCESS_TICKS-1,tag.getInt("Progress")));feUsed=progress>0?Math.max(0,Math.min(FE_PER_CRAFT,tag.getInt("FeUsed"))):0;
  for(int i=0;i<4;i++)items.set(i,ItemStack.parseOptional(lookup,tag.getCompound("Slot"+i)));
 }
}
