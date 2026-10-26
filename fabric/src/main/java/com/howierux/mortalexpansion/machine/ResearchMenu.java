package com.howierux.mortalexpansion.machine;
import com.howierux.mortalexpansion.guide.MortalityResearch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class ResearchMenu extends AbstractContainerMenu {
 private final ResearchStationEntity station;private final ContainerData data;
 public ResearchMenu(int id,Inventory inv,BlockPos pos){this(id,inv,(ResearchStationEntity)inv.player.level().getBlockEntity(pos),new SimpleContainerData(2));}
 public ResearchMenu(int id,Inventory inv,ResearchStationEntity station,ContainerData data){
  super(Machines.RESEARCH_MENU,id);this.station=station;this.data=data;addDataSlots(data);Container items=inv.player.level().isClientSide?new SimpleContainer(1):station;
  addSlot(new Slot(items,0,30,45){@Override public boolean mayPlace(ItemStack stack){return MortalityResearch.amount(stack)>0;}});
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,9+row*9+col,8+col*18,125+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inv,col,8+col*18,183));
 }
 public int ectoType(){return data.get(1);}public int bonus(){return data.get(0);}
 @Override public boolean stillValid(Player p){return station.stillValid(p);}
 @Override public boolean clickMenuButton(Player player,int button){if(button!=0||!(player instanceof ServerPlayer p)||!stillValid(p))return false;var input=station.getItem(0);if(!MortalityResearch.contribute(p,input)){p.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.research.blocked"));return false;}station.setChanged();broadcastChanges();return true;}
 @Override public ItemStack quickMoveStack(Player p,int index){
  if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
  boolean moved=index==0?moveItemStackTo(stack,1,37,true):MortalityResearch.amount(stack)>0?moveItemStackTo(stack,0,1,false):index<28?moveItemStackTo(stack,28,37,false):moveItemStackTo(stack,1,28,false);
  if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(p,stack);return copy;
 }
}
