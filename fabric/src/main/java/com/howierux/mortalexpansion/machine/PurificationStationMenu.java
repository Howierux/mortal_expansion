package com.howierux.mortalexpansion.machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class PurificationStationMenu extends AbstractContainerMenu {
 public static final int DATA_COUNT=16,MACHINE_HEIGHT=136;
 private final PurificationStationEntity station;private final ContainerData data;
 public PurificationStationMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,(PurificationStationEntity)inventory.player.level().getBlockEntity(pos),new SimpleContainerData(DATA_COUNT));}
 public PurificationStationMenu(int id,Inventory inventory,PurificationStationEntity station,ContainerData data){
  super(Machines.STATION_MENU,id);this.station=station;this.data=data;addDataSlots(data);
  Container container=inventory.player.level().isClientSide?new SimpleContainer(4):station;
  int[][] coords={{44,50},{44,74},{116,62},{13,88}};
  for(int i=0;i<4;i++){final int slot=i;addSlot(new Slot(container,i,coords[i][0],coords[i][1]){@Override public boolean mayPlace(ItemStack stack){return station.canPlaceItem(slot,stack);}});}
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,MACHINE_HEIGHT+13+row*18));
  for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,MACHINE_HEIGHT+71));
 }
 private int value(int index){return(data.get(index*2)&65535)|((data.get(index*2+1)&65535)<<16);}
 public int getWater(){return value(0);}public int getWaterCapacity(){return value(1);}
 public int getEnergy(){return value(2);}public int getEnergyCapacity(){return value(3);}
 public int getProgress(){return value(4);}public int getMaxProgress(){return value(5);}
 public int getFeUsed(){return value(6);}
 @Override public boolean stillValid(Player player){return station.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){
  if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
  if(index<4){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;}
  else if(!moveItemStackTo(stack,0,4,false))return ItemStack.EMPTY;
  if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
 }
}
