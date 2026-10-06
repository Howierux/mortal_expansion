package com.howierux.mortalexpansion.menu;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import com.howierux.mortalexpansion.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class AltarOfPurityMenu extends AbstractContainerMenu {
    private final AltarOfPurityBlockEntity altar;private final ContainerData data;
    public AltarOfPurityMenu(int id,Inventory inventory,FriendlyByteBuf extra){this(id,inventory,(AltarOfPurityBlockEntity)inventory.player.level().getBlockEntity(extra.readBlockPos()),new SimpleContainerData(3));}
    public AltarOfPurityMenu(int id,Inventory inventory,AltarOfPurityBlockEntity altar){this(id,inventory,altar,altar.createMenuData());}
    private AltarOfPurityMenu(int id,Inventory inventory,AltarOfPurityBlockEntity altar,ContainerData data){
        super(ModMenus.ALTAR_OF_PURITY.get(),id);this.altar=altar;this.data=data;checkContainerDataCount(data,3);addDataSlots(data);
        addSlot(new SlotItemHandler(altar.getItems(),0,30,46));addSlot(new SlotItemHandler(altar.getItems(),1,30,80){@Override public boolean mayPlace(ItemStack stack){return false;}});
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,125+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,183));
    }
    public int getBurnTicks(){return Math.clamp((data.get(0)&65535)|((data.get(1)&65535)<<16),0,AltarOfPurityBlockEntity.MAX_BURN_TICKS);}
    @Override public boolean stillValid(Player player){return !altar.isRemoved() && player.level()==altar.getLevel() && player.distanceToSqr(altar.getBlockPos().getCenter())<=64;}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0 || index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var original=stack.copy();
        boolean moved=index<2?moveItemStackTo(stack,2,38,true):altar.getItems().isItemValid(0,stack)?moveItemStackTo(stack,0,1,false):index<29?moveItemStackTo(stack,29,38,false):moveItemStackTo(stack,2,29,false);
        if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;
    }
}
