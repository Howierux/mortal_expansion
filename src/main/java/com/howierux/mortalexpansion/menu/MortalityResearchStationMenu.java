package com.howierux.mortalexpansion.menu;
import com.howierux.mortalexpansion.registry.ModMenus;
import com.howierux.mortalexpansion.blockentity.MortalityResearchStationBlockEntity;
import com.howierux.mortalexpansion.haunt.MortalityResearch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
public final class MortalityResearchStationMenu extends AbstractContainerMenu {
    private final MortalityResearchStationBlockEntity station;private final ContainerData data;
    public MortalityResearchStationMenu(int id,Inventory inventory,FriendlyByteBuf extra){this(id,inventory,(MortalityResearchStationBlockEntity)inventory.player.level().getBlockEntity(extra.readBlockPos()),new SimpleContainerData(2));}
    public MortalityResearchStationMenu(int id,Inventory inventory,MortalityResearchStationBlockEntity station){this(id,inventory,station,new ContainerData(){public int get(int i){return inventory.player instanceof ServerPlayer p?(i==0?MortalityResearch.bonus(p):p.getData(com.howierux.mortalexpansion.registry.ModAttachments.MORTALITY_RESEARCH_ECTO)):0;}public void set(int i,int value){}public int getCount(){return 2;}});}
    private MortalityResearchStationMenu(int id,Inventory inventory,MortalityResearchStationBlockEntity station,ContainerData data){
        super(ModMenus.MORTALITY_RESEARCH_STATION.get(),id);this.station=station;this.data=data;checkContainerDataCount(data,2);addDataSlots(data);addSlot(new SlotItemHandler(station.getItems(),0,30,45));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,125+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,183));
    }
    public int ectoType(){return data.get(1);}
    public int bonus(){return data.get(0);}
    @Override public boolean stillValid(Player player){return !station.isRemoved() && player.level()==station.getLevel() && player.distanceToSqr(station.getBlockPos().getCenter())<=64;}
    @Override public boolean clickMenuButton(Player player,int button){
        if(button!=0 || !(player instanceof ServerPlayer p) || !stillValid(player))return false;
        var input=station.getItems().getStackInSlot(0);if(!MortalityResearch.contribute(p,input)){p.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.research.blocked"));return false;}
        station.getItems().setStackInSlot(0,input);broadcastChanges();return true;
    }
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0 || index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();
        boolean moved=index==0?moveItemStackTo(stack,1,37,true):MortalityResearch.amount(stack)>0?moveItemStackTo(stack,0,1,false):index<28?moveItemStackTo(stack,28,37,false):moveItemStackTo(stack,1,28,false);
        if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;
    }
}
