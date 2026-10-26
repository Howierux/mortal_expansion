package com.howierux.mortalexpansion.machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import com.howierux.mortalexpansion.content.ContentRuntime;
public final class EctchantmentMenu extends AbstractContainerMenu {
 private final Container inventory;private final ContainerData data;
 public EctchantmentMenu(int id,Inventory inv,BlockPos pos){this(id,inv,new SimpleContainer(3),new SimpleContainerData(6));}
 public EctchantmentMenu(int id,Inventory inv,Container items,ContainerData data){super(ContentRuntime.ECTCHANT_MENU,id);inventory=items;this.data=data;addDataSlots(data);for(int i=0;i<3;i++){final int slot=i;addSlot(new Slot(items,i,44+i*36,58){public boolean mayPlace(ItemStack s){return slot==0||slot==1&&EctchantmentEntity.fuel(s)>0;}});}for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,9+y*9+x,8+x*18,125+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,183));}
 public int getEctoplasmicCharge(){return data.get(0);}public int getMaxEctoplasmicCharge(){return data.get(1);}public int getEnergyStored(){return data.get(2)*10;}public int getEnergyCapacity(){return data.get(3)*10;}public int getRecentFeInput(){return data.get(4)*10;}public int getStatus(){return data.get(5);}
 public boolean stillValid(Player p){return inventory.stillValid(p);}public ItemStack quickMoveStack(Player p,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);var stack=slot.getItem();if(stack.isEmpty())return ItemStack.EMPTY;var copy=stack.copy();if(index<3){if(!moveItemStackTo(stack,3,39,true))return ItemStack.EMPTY;}else if(EctchantmentEntity.fuel(stack)>0){if(!moveItemStackTo(stack,1,2,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(p,stack);return copy;}
}
