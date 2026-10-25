package com.howierux.mortalexpansion.machine;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.HauntManager;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.*;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import java.util.*;

public final class GeneratorEntity extends BlockEntity implements WorldlyContainer,ExtendedScreenHandlerFactory<BlockPos> {
 public final SimpleEnergyStorage energy;
 private final List<ItemStack> items=new ArrayList<>(List.of(ItemStack.EMPTY,ItemStack.EMPTY));
 private int remaining,baseRate,hauntPerBurn;
 public GeneratorEntity(BlockPos pos,BlockState state){
  super(state.is(CatalogBlocks.ECTOPLASMIC_GENERATOR)?Machines.ECTO:Machines.ENCHANT,pos,state);
  energy=new SimpleEnergyStorage(capacity(),0,maxOutput()){@Override protected void onFinalCommit(){setChanged();}};
 }
 public boolean ecto(){return getBlockState().is(CatalogBlocks.ECTOPLASMIC_GENERATOR);}
 public int capacity(){return ecto()?200000:400000;}
 public int maxOutput(){return ecto()?2000:4000;}
 public int remaining(){return remaining;}
 public int hauntTier(){return level instanceof ServerLevel s?HauntManager.getTier(s,worldPosition).id():0;}
 public int rate(){return ecto()?(int)Math.floor(baseRate*(1+hauntTier()*0.25)):baseRate;}
 public static boolean fuel(ItemStack stack){return stack.is(CatalogItems.RAW_ECTOPLASM)||stack.is(CatalogItems.PURE_ECTOPLASM);}
 public boolean accepts(ItemStack stack){return ecto()?fuel(stack):!EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty();}
 public void tick(ServerLevel level){
  if(remaining==0 && (ecto()||items.get(1).isEmpty()) && accepts(items.get(0)))start();
  if(ecto())push(level);
  if(remaining>0){
   int request=ecto()?rate():Math.min(rate(),remaining);
   int accepted=(int)Math.min(request,capacity()-energy.amount);
   if(accepted>0){energy.amount+=accepted;remaining-=ecto()?1:accepted;
    if(remaining==0){if(ecto()&&hauntPerBurn>0)HauntManager.addPoints(level,worldPosition,hauntPerBurn);baseRate=hauntPerBurn=0;}setChanged();}
  }
  if(!ecto())push(level);
 }
 private void start(){
  var input=items.get(0);
  if(ecto()){
   boolean pure=input.is(CatalogItems.PURE_ECTOPLASM);remaining=pure?2000:1000;baseRate=pure?40:20;hauntPerBurn=pure?3:1;
  }else{
   long total=0,rate=0;var enchants=EnchantmentHelper.getEnchantmentsForCrafting(input);
   for(var enchant:enchants.keySet()){
    long level=Math.max(1,enchants.getLevel(enchant)),value=Math.max(1,enchant.value().getMinCost(1));
    total=Math.min(Integer.MAX_VALUE,total+value*level*level*1000);rate=Math.min(4000,rate+value*level*2);
   }
   remaining=(int)Math.max(1,total);baseRate=(int)Math.min(4000,Math.max(20,rate));
   var result=input.is(Items.ENCHANTED_BOOK)?input.transmuteCopy(Items.BOOK,1):input.copyWithCount(1);
   result.remove(DataComponents.ENCHANTMENTS);result.remove(DataComponents.STORED_ENCHANTMENTS);items.set(1,result);
  }
  input.shrink(1);setChanged();
 }
 private void push(ServerLevel level){
  long budget=Math.min(maxOutput(),energy.amount);
  for(Direction d:Direction.values()){
   if(budget<=0)break;var at=worldPosition.relative(d);
   if(level.getChunkSource().getChunkNow(at.getX()>>4,at.getZ()>>4)==null)continue;
   var target=EnergyStorage.SIDED.find(level,at,d.getOpposite());
   if(target!=null)budget-=EnergyStorageUtil.move(energy,target,budget,null);
  }
 }
 public ContainerData menuData(){return new ContainerData(){
  public int get(int index){int value=switch(index/2){case 0->(int)energy.amount;case 1->capacity();case 2->remaining;case 3->rate();case 4->hauntTier();case 5->baseRate;default->0;};return(index%2==0?value:value>>>16)&65535;}
  public void set(int index,int value){}
  public int getCount(){return GeneratorMenu.DATA_COUNT;}
 };}
 @Override public int getContainerSize(){return 2;}
 @Override public boolean isEmpty(){return items.get(0).isEmpty()&&items.get(1).isEmpty();}
 @Override public ItemStack getItem(int slot){return slot==1&&remaining>0?ItemStack.EMPTY:items.get(slot);}
 @Override public ItemStack removeItem(int slot,int count){var result=getItem(slot).split(count);if(!result.isEmpty())setChanged();return result;}
 @Override public ItemStack removeItemNoUpdate(int slot){var result=getItem(slot);if(!result.isEmpty())items.set(slot,ItemStack.EMPTY);return result;}
 @Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);setChanged();}
 @Override public void clearContent(){items.set(0,ItemStack.EMPTY);items.set(1,ItemStack.EMPTY);setChanged();}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getCenter())<=64;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&accepts(stack);}
 @Override public int[] getSlotsForFace(Direction d){return ecto()?new int[]{0}:new int[]{0,1};}
 @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction d){return canPlaceItem(slot,stack);}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction d){return !ecto()&&slot==1&&remaining==0;}
 public void dropInventory(){if(level instanceof ServerLevel){for(var stack:items)Containers.dropItemStack(level,worldPosition.getX()+0.5,worldPosition.getY()+0.5,worldPosition.getZ()+0.5,stack);clearContent();}}
 @Override public Component getDisplayName(){return Component.translatable(getBlockState().getBlock().getDescriptionId());}
 @Override public BlockPos getScreenOpeningData(ServerPlayer player){return worldPosition;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new GeneratorMenu(id,inventory,this,menuData());}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putLong("Energy",energy.amount);tag.putInt("Remaining",remaining);tag.putInt("BaseRate",baseRate);tag.putInt("BurnHaunt",hauntPerBurn);if(!items.get(0).isEmpty())tag.put("Input",items.get(0).save(lookup));if(!items.get(1).isEmpty())tag.put("Output",items.get(1).save(lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);energy.amount=Math.max(0,Math.min(capacity(),tag.getLong("Energy")));remaining=Math.max(0,Math.min(ecto()?2000:Integer.MAX_VALUE,tag.getInt("Remaining")));baseRate=Math.max(0,Math.min(ecto()?40:4000,tag.getInt("BaseRate")));hauntPerBurn=Math.max(0,Math.min(3,tag.getInt("BurnHaunt")));items.set(0,ItemStack.parseOptional(lookup,tag.getCompound("Input")));items.set(1,ItemStack.parseOptional(lookup,tag.getCompound("Output")));}
}
