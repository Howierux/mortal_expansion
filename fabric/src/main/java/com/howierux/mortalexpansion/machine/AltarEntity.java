package com.howierux.mortalexpansion.machine;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import java.util.*;

public final class AltarEntity extends BlockEntity implements WorldlyContainer,ExtendedScreenHandlerFactory<BlockPos> {
 public static final int MAX_BURN_TICKS=72000,OFFERING_INTERVAL=250,CLEANSE_INTERVAL=200,CLEANSE_HUNDREDTHS=10;
 private final List<ItemStack> items=new ArrayList<>(Collections.nCopies(2,ItemStack.EMPTY));
 private int burnTicks,offeringDelay,cleanseProgress;private boolean offeringBlessed;
 public AltarEntity(BlockPos pos,BlockState state){super(state.is(CatalogBlocks.ALTAR_OF_IMPURITY)?Machines.IMPURITY:Machines.PURITY,pos,state);}
 public boolean isImpure(){return getBlockState().is(CatalogBlocks.ALTAR_OF_IMPURITY);}
 public int getBurnTicks(){return burnTicks;}
 public boolean hasBlessedOffering(){return offeringBlessed;}
 /** Used by the native positive-event dispatcher for nearby Purity altar benefits. */
 public boolean blessNextOffering(){int duration=PurityOfferings.burnTicks(items.get(0),isImpure());if(isImpure()||offeringBlessed||duration<=0||duration+duration/2>MAX_BURN_TICKS-burnTicks)return false;offeringBlessed=true;setChanged();return true;}
 public boolean addOfferingGift(){var input=items.get(0);if(isImpure()||!input.isEmpty()&&(!ItemStack.isSameItemSameComponents(input,new ItemStack(CatalogItems.HOLY_SALT))||input.getCount()>=input.getMaxStackSize()))return false;if(input.isEmpty())items.set(0,new ItemStack(CatalogItems.HOLY_SALT));else input.grow(1);setChanged();return true;}
 private boolean consumeOffering(){
  var input=items.get(0);
  if(input.is(CatalogItems.RITUAL_STAR)){
   if(!(level instanceof ServerLevel server)||!WorldHauntRituals.activate(server,worldPosition,isImpure()))return false;
   input.shrink(1);offeringDelay=OFFERING_INTERVAL;setChanged();return true;
  }
  int duration=PurityOfferings.burnTicks(input,isImpure());if(offeringBlessed)duration+=duration/2;
  if(duration<=0||duration>MAX_BURN_TICKS-burnTicks)return false;
  Item remainderItem=input.getItem().getCraftingRemainingItem();var remainder=remainderItem==null?ItemStack.EMPTY:new ItemStack(remainderItem);var output=items.get(1);
  if(!remainder.isEmpty()&&!output.isEmpty()&&(!ItemStack.isSameItemSameComponents(output,remainder)||output.getCount()+remainder.getCount()>output.getMaxStackSize()))return false;
  input.shrink(1);if(!remainder.isEmpty()){if(output.isEmpty())items.set(1,remainder);else output.grow(remainder.getCount());}
  burnTicks+=duration;offeringBlessed=false;offeringDelay=OFFERING_INTERVAL;setChanged();return true;
 }
 public static int pulse(ServerLevel level,BlockPos pos,int change){
  var center=new ChunkPos(pos);int changed=0;
  for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){
   var chunk=level.getChunkSource().getChunkNow(center.x+dx,center.z+dz);if(chunk==null)continue;
   int before=HauntManager.getHundredths(level,chunk.getPos());
   if(change<0?before>0:before<FractionalHaunt.MAX_UNITS){HauntManager.addHundredths(level,chunk.getPos(),change);changed++;}
  }
  return changed;
 }
 public void tick(ServerLevel level){
  if(offeringDelay>0){offeringDelay--;setChanged();}if(offeringDelay==0)consumeOffering();
  if(burnTicks>0){burnTicks--;if(++cleanseProgress>=CLEANSE_INTERVAL){cleanseProgress=0;pulse(level,worldPosition,isImpure()?CLEANSE_HUNDREDTHS:-CLEANSE_HUNDREDTHS);}setChanged();}
  var state=getBlockState();if(state.getValue(BlockStateProperties.LIT)!=(burnTicks>0))level.setBlock(worldPosition,state.setValue(BlockStateProperties.LIT,burnTicks>0),3);
 }
 public ContainerData menuData(){return new ContainerData(){public int get(int index){return switch(index){case 0->burnTicks&65535;case 1->burnTicks>>>16;default->offeringDelay;};}public void set(int index,int value){}public int getCount(){return 3;}};}
 @Override public int getContainerSize(){return 2;}
 @Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}
 @Override public ItemStack getItem(int slot){return items.get(slot);}
 @Override public ItemStack removeItem(int slot,int count){var stack=items.get(slot).split(count);if(!stack.isEmpty())setChanged();return stack;}
 @Override public ItemStack removeItemNoUpdate(int slot){return items.set(slot,ItemStack.EMPTY);}
 @Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);setChanged();}
 @Override public void clearContent(){Collections.fill(items,ItemStack.EMPTY);setChanged();}
 @Override public boolean stillValid(Player p){return !isRemoved()&&level!=null&&level.getBlockEntity(worldPosition)==this&&p.distanceToSqr(worldPosition.getCenter())<=64;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&(stack.is(CatalogItems.RITUAL_STAR)?level!=null&&level.dimension().equals(Level.OVERWORLD):PurityOfferings.burnTicks(stack,isImpure())>0);}
 @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}
 @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1;}
 public void dropInventory(){if(level instanceof ServerLevel)for(var stack:items)Containers.dropItemStack(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,stack);clearContent();}
 @Override public Component getDisplayName(){return Component.translatable(getBlockState().getBlock().getDescriptionId());}
 @Override public BlockPos getScreenOpeningData(ServerPlayer p){return worldPosition;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new AltarMenu(id,inv,this,menuData());}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);for(int i=0;i<2;i++)if(!items.get(i).isEmpty())tag.put("Slot"+i,items.get(i).save(lookup));tag.putInt("BurnTicks",burnTicks);tag.putInt("OfferingDelay",offeringDelay);tag.putInt("CleanseProgress",cleanseProgress);tag.putBoolean("OfferingBlessed",offeringBlessed);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);for(int i=0;i<2;i++)items.set(i,ItemStack.parseOptional(lookup,tag.getCompound("Slot"+i)));burnTicks=Math.clamp(tag.getInt("BurnTicks"),0,MAX_BURN_TICKS);offeringDelay=Math.clamp(tag.getInt("OfferingDelay"),0,OFFERING_INTERVAL);cleanseProgress=Math.clamp(tag.getInt("CleanseProgress"),0,CLEANSE_INTERVAL-1);offeringBlessed=!isImpure()&&tag.getBoolean("OfferingBlessed");}
}
