package com.howierux.mortalexpansion.blockentity;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.*;

public final class AltarOfPurityBlockEntity extends BlockEntity {
    public static final int MAX_BURN_TICKS=72000,OFFERING_INTERVAL=250,CLEANSE_INTERVAL=200,CLEANSE_HUNDREDTHS=10;
    private int burnTicks,offeringDelay,cleanseProgress;
    private boolean offeringBlessed;
    private final ItemStackHandler items=new ItemStackHandler(2){
        @Override public boolean isItemValid(int slot,ItemStack stack){return slot==0 && PurityOfferings.burnTicks(stack,isImpure())>0;}
        @Override protected void onContentsChanged(int slot){setChanged();}
    };
    private final IItemHandler automation=new IItemHandler(){
        public int getSlots(){return 2;}public ItemStack getStackInSlot(int slot){return items.getStackInSlot(slot);}
        public ItemStack insertItem(int slot,ItemStack stack,boolean simulate){return slot==0?items.insertItem(slot,stack,simulate):stack;}
        public ItemStack extractItem(int slot,int amount,boolean simulate){return slot==1?items.extractItem(slot,amount,simulate):ItemStack.EMPTY;}
        public int getSlotLimit(int slot){return items.getSlotLimit(slot);}public boolean isItemValid(int slot,ItemStack stack){return items.isItemValid(slot,stack);}
    };
    public AltarOfPurityBlockEntity(BlockPos pos,BlockState state){super(ModBlockEntities.ALTAR_OF_PURITY.get(),pos,state);}
    private AltarOfPurityBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    public static AltarOfPurityBlockEntity impurity(BlockPos pos,BlockState state){return new AltarOfPurityBlockEntity(ModBlockEntities.ALTAR_OF_IMPURITY.get(),pos,state);}
    public boolean isImpure(){return getType()==ModBlockEntities.ALTAR_OF_IMPURITY.get();}
    public ItemStackHandler getItems(){return items;}public IItemHandler getAutomationItems(){return automation;}
    public int getBurnTicks(){return burnTicks;}
    public ContainerData createMenuData(){return new ContainerData(){
        public int get(int index){return switch(index){case 0->burnTicks&65535;case 1->burnTicks>>>16;default->offeringDelay;};}
        public void set(int index,int value){}public int getCount(){return 3;}
    };}
    public boolean blessNextOffering(){
        int duration=PurityOfferings.burnTicks(items.getStackInSlot(0),isImpure());
        if(isImpure() || offeringBlessed || duration<=0 || duration+duration/2>MAX_BURN_TICKS-burnTicks)return false;
        offeringBlessed=true;setChanged();return true;
    }
    public boolean hasBlessedOffering(){return offeringBlessed;}
    public boolean addOfferingGift(){
        if(isImpure())return false;
        var gift=new ItemStack(ModItems.HOLY_SALT.get());
        if(!items.insertItem(0,gift,true).isEmpty())return false;
        if(!items.insertItem(0,gift,false).isEmpty())return false;
        setChanged();return true;
    }
    private boolean consumeOffering(){
        var input=items.getStackInSlot(0);int duration=PurityOfferings.burnTicks(input,isImpure());if(offeringBlessed)duration+=duration/2;
        if(duration<=0 || duration>MAX_BURN_TICKS-burnTicks)return false;
        var remainder=input.copyWithCount(1).getCraftingRemainingItem();var output=items.getStackInSlot(1);
        if(!remainder.isEmpty() && !output.isEmpty() && (!ItemStack.isSameItemSameComponents(output,remainder)
            || output.getCount()+remainder.getCount()>Math.min(output.getMaxStackSize(),items.getSlotLimit(1))))return false;
        items.extractItem(0,1,false);
        if(!remainder.isEmpty()){if(output.isEmpty())items.setStackInSlot(1,remainder);else{output.grow(remainder.getCount());items.setStackInSlot(1,output);}}
        burnTicks+=duration;offeringBlessed=false;offeringDelay=OFFERING_INTERVAL;setChanged();return true;
    }
    public static int cleanse(ServerLevel level,BlockPos pos){return pulse(level,pos,-CLEANSE_HUNDREDTHS);}
    public static int corrupt(ServerLevel level,BlockPos pos){return pulse(level,pos,CLEANSE_HUNDREDTHS);}
    private static int pulse(ServerLevel level,BlockPos pos,int change){
        var center=new net.minecraft.world.level.ChunkPos(pos);int changed=0;
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){
            var chunk=level.getChunkSource().getChunkNow(center.x+dx,center.z+dz);if(chunk==null)continue;
            long before=HauntManager.getHundredths(level,chunk.getPos());
            if(change<0?before>0:before<HauntTier.MAX_VALUE*FractionalHaunt.UNITS_PER_POINT){HauntManager.addHundredths(level,chunk.getPos(),change);changed++;}
        }return changed;
    }
    public static void serverTick(Level level,BlockPos pos,BlockState state,AltarOfPurityBlockEntity altar){
        if(!(level instanceof ServerLevel server))return;
        if(altar.offeringDelay>0){altar.offeringDelay--;altar.setChanged();}
        if(altar.offeringDelay==0)altar.consumeOffering();
        if(altar.burnTicks>0){
            altar.burnTicks--;altar.cleanseProgress++;
            if(altar.cleanseProgress>=CLEANSE_INTERVAL){altar.cleanseProgress=0;if(altar.isImpure())corrupt(server,pos);else cleanse(server,pos);}
            altar.setChanged();
        }
        boolean lit=altar.burnTicks>0;
        if(state.getValue(BlockStateProperties.LIT)!=lit)level.setBlock(pos,state.setValue(BlockStateProperties.LIT,lit),3);
    }
    public void dropInventory(ServerLevel level){for(int slot=0;slot<2;slot++){Containers.dropItemStack(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,items.getStackInSlot(slot).copy());items.setStackInSlot(slot,ItemStack.EMPTY);}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.put("Items",items.serializeNBT(registries));tag.putInt("BurnTicks",burnTicks);tag.putInt("OfferingDelay",offeringDelay);tag.putInt("CleanseProgress",cleanseProgress);tag.putBoolean("OfferingBlessed",offeringBlessed);}
    @Override public void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);if(tag.contains("Items"))items.deserializeNBT(registries,tag.getCompound("Items"));burnTicks=net.minecraft.util.Mth.clamp(tag.getInt("BurnTicks"),0,MAX_BURN_TICKS);offeringDelay=net.minecraft.util.Mth.clamp(tag.getInt("OfferingDelay"),0,OFFERING_INTERVAL);cleanseProgress=net.minecraft.util.Mth.clamp(tag.getInt("CleanseProgress"),0,CLEANSE_INTERVAL-1);offeringBlessed=!isImpure() && tag.getBoolean("OfferingBlessed");}
}
