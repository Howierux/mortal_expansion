package com.howierux.mortalexpansion.machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class AltarMenu extends AbstractContainerMenu {
 private final AltarEntity altar;private final ContainerData data;
 public AltarMenu(int id,Inventory inv,BlockPos pos){this(id,inv,(AltarEntity)inv.player.level().getBlockEntity(pos),new SimpleContainerData(3));}
 public AltarMenu(int id,Inventory inv,AltarEntity altar,ContainerData data){
  super(altar.isImpure()?Machines.IMPURITY_MENU:Machines.PURITY_MENU,id);this.altar=altar;this.data=data;addDataSlots(data);
  Container items=inv.player.level().isClientSide?new SimpleContainer(2):altar;
  addSlot(new Slot(items,0,30,46){@Override public boolean mayPlace(ItemStack stack){return altar.canPlaceItem(0,stack);}});
  addSlot(new Slot(items,1,30,80){@Override public boolean mayPlace(ItemStack stack){return false;}});
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,9+row*9+col,8+col*18,125+row*18));
  for(int col=0;col<9;col++)addSlot(new Slot(inv,col,8+col*18,183));
 }
 public boolean isImpure(){return altar.isImpure();}
 public int getBurnTicks(){return Math.clamp((data.get(0)&65535)|((data.get(1)&65535)<<16),0,AltarEntity.MAX_BURN_TICKS);}
 @Override public boolean stillValid(Player p){return altar.stillValid(p);}
 @Override public ItemStack quickMoveStack(Player player,int index){
  if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
  boolean moved=index<2?moveItemStackTo(stack,2,38,true):altar.canPlaceItem(0,stack)?moveItemStackTo(stack,0,1,false):index<29?moveItemStackTo(stack,29,38,false):moveItemStackTo(stack,2,29,false);
  if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
 }
}
