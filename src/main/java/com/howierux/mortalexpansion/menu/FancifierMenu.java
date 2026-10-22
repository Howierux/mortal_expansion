package com.howierux.mortalexpansion.menu;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Per-user temporary stonecutter-style inventory; closing returns both slots. */
public final class FancifierMenu extends AbstractContainerMenu {
    public static final int APPLY=0;
    private final SimpleContainer items = new SimpleContainer(2) { @Override public void setChanged() { super.setChanged(); FancifierMenu.this.broadcastChanges(); } };
    private final ContainerLevelAccess access;
    private final DataSlot opacity = DataSlot.standalone();
    public FancifierMenu(int id,Inventory inventory) { this(id,inventory,ContainerLevelAccess.NULL); }
    public FancifierMenu(int id,Inventory inventory,ContainerLevelAccess access) {
        super(ModMenus.FANCIFIER.get(),id); this.access=access; opacity.set(20); addDataSlot(opacity);
        addSlot(new Slot(items,0,25,46) { @Override public boolean mayPlace(ItemStack stack) { return FancifierRules.accepts(stack); } });
        addSlot(new Slot(items,1,187,46) { @Override public boolean mayPlace(ItemStack stack) { return false; } });
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,34+18*col,146+18*row));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,34+18*col,204));
    }
    public int opacity() { return opacity.get(); }
    @Override public boolean stillValid(Player player) { return stillValid(access,player,ModBlocks.FANCIFIER.get()); }
    public boolean canApply(Player player) { return canApply(player,opacity()); }
    public boolean canApply(Player player,int selectedOpacity) {
        var input=items.getItem(0); var result=FancifierRules.convert(input,selectedOpacity,player.registryAccess());
        if (result.isEmpty() || ItemStack.isSameItemSameComponents(input,result)) return false;
        var output=items.getItem(1);
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output,result) && output.getCount()<output.getMaxStackSize();
    }
    @Override public boolean clickMenuButton(Player player,int button) {
        if (player.level().isClientSide || !stillValid(player)) return false;
        if (FancifierRules.validOpacity(button)) { opacity.set(button); broadcastChanges(); return true; }
        if (button!=APPLY || !canApply(player)) return false;
        var result=FancifierRules.convert(items.getItem(0),opacity(),player.registryAccess()); var output=items.getItem(1);
        int amount=Math.min(result.getCount(),result.getMaxStackSize()-(output.isEmpty()?0:output.getCount()));
        if(amount<=0)return false;
        items.removeItem(0,amount); result.setCount(amount);
        if(output.isEmpty())items.setItem(1,result);else { output.grow(amount);items.setChanged(); }
        broadcastChanges(); return true;
    }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0 || index>=slots.size())return ItemStack.EMPTY;
        Slot slot=slots.get(index); if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem(); var copy=stack.copy();
        if(index<2) { if(!moveItemStackTo(stack,2,38,true))return ItemStack.EMPTY; }
        else if(FancifierRules.accepts(stack)) { if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY; }
        else if(index<29) { if(!moveItemStackTo(stack,29,38,false))return ItemStack.EMPTY; }
        else if(!moveItemStackTo(stack,2,29,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged(); slot.onTake(player,stack); return copy;
    }
    @Override public void removed(Player player) { super.removed(player); access.execute((level,pos)->clearContainer(player,items)); }
}
