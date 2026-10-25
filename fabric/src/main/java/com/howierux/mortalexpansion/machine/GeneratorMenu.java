package com.howierux.mortalexpansion.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;

public final class GeneratorMenu extends AbstractContainerMenu {
 public static final int DATA_COUNT=12;
 public final boolean ecto;
 private final GeneratorEntity generator;
 private final ContainerData data;
 private final int machineSlots;
 public GeneratorMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,(GeneratorEntity)inventory.player.level().getBlockEntity(pos),new SimpleContainerData(DATA_COUNT));}
 public GeneratorMenu(int id,Inventory inventory,GeneratorEntity generator,ContainerData data){
  super(generator.ecto()?Machines.ECTO_MENU:Machines.ENCHANT_MENU,id);this.generator=generator;this.ecto=generator.ecto();this.data=data;machineSlots=ecto?1:2;
  Container items=inventory.player.level().isClientSide?new SimpleContainer(2):generator;
  addDataSlots(data);
  addSlot(new Slot(items,0,ecto?80:44,ecto?58:52){@Override public boolean mayPlace(ItemStack stack){return generator.accepts(stack);}});
  if(!ecto)addSlot(new Slot(items,1,116,52){@Override public boolean mayPlace(ItemStack stack){return false;}});
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,125+row*18));
  for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,183));
 }
 public int value(int index){return(data.get(index*2)&65535)|((data.get(index*2+1)&65535)<<16);}
 public int getEnergyStored(){return value(0);}
 public int getEnergyCapacity(){return value(1);}
 public int getBurnTicksRemaining(){return value(2);}
 public int getHauntTierId(){return value(4);}
 public int getHauntMultiplierPercent(){return 100+25*value(4);}
 public int getGenerationPerTick(){return value(3);}
 public int getRemainingEnergyToGenerate(){return value(2);}
 public boolean hasFinishedItem(){return !ecto&&value(2)==0&&slots.get(1).hasItem();}
 @Override public boolean stillValid(Player player){return generator.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){
  if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
  var stack=slot.getItem();var copy=stack.copy();
  if(index<machineSlots){if(!moveItemStackTo(stack,machineSlots,slots.size(),true))return ItemStack.EMPTY;}
  else if(generator.accepts(stack)){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}
  else if(index<machineSlots+27){if(!moveItemStackTo(stack,machineSlots+27,slots.size(),false))return ItemStack.EMPTY;}
  else if(!moveItemStackTo(stack,machineSlots,machineSlots+27,false))return ItemStack.EMPTY;
  if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
 }
}
