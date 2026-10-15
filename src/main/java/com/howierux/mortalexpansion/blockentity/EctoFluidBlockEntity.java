package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.fluid.EctoFluidNetworks;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public final class EctoFluidBlockEntity extends BlockEntity {
    public static final int NORMAL=0,INPUT=1,OUTPUT=2,DISABLED=3;
    private final byte[] modes=new byte[6];
    private final IFluidHandler[] handlers=new IFluidHandler[7];
    private long budgetTick=Long.MIN_VALUE;
    private int filled,drained;
    public final FluidTank tank;
    public EctoFluidBlockEntity(BlockPos pos,BlockState state){
        super(ModBlockEntities.ECTO_FLUID.get(),pos,state);
        tank=new FluidTank(capacity(state)){
            @Override protected void onContentsChanged(){setChanged();EctoFluidNetworks.changed(EctoFluidBlockEntity.this);}
        };
    }
    public static boolean pipe(BlockState state){return state.getBlock() instanceof com.howierux.mortalexpansion.block.EctoPipeBlock;}
    public static boolean purified(BlockState state){return state.is(ModBlocks.PURIFIED_ECTANK.get()) || state.is(ModBlocks.PURIFIED_ECTO_PIPE.get());}
    public static int capacity(BlockState state){return pipe(state)?(purified(state)?8000:2000):(purified(state)?64000:16000);}
    public boolean isPipe(){return pipe(getBlockState());}
    public int rate(){return purified(getBlockState())?2000:500;}
    public int mode(Direction side){return modes[side.ordinal()];}
    public void cycle(Direction side){modes[side.ordinal()]=(byte)((mode(side)+1)%4);setChanged();EctoFluidNetworks.invalidate(this);EctoFluidNetworks.changed(this);}
    private void budget(){long now=level==null?0:level.getGameTime();if(budgetTick!=now){budgetTick=now;filled=0;drained=0;}}
    public int remaining(){budget();return isPipe()?Math.max(0,rate()-drained):Integer.MAX_VALUE;}
    public void reserve(int amount){budget();drained+=amount;}
    public IFluidHandler handler(Direction side){
        int index=side==null?6:side.ordinal();if(handlers[index]!=null)return handlers[index];
        return handlers[index]=new IFluidHandler(){
            public int getTanks(){return 1;}
            public FluidStack getFluidInTank(int index){return tank.getFluidInTank(index);}
            public int getTankCapacity(int index){return tank.getTankCapacity(index);}
            public boolean isFluidValid(int index,FluidStack fluid){return tank.isFluidValid(index,fluid);}
            private boolean fillAllowed(){return side==null || !isPipe() || (mode(side)!=OUTPUT && mode(side)!=DISABLED);}
            private boolean drainAllowed(){return side==null || !isPipe() || (mode(side)!=INPUT && mode(side)!=DISABLED);}
            public int fill(FluidStack fluid,FluidAction action){
                if(!fillAllowed() || fluid.isEmpty())return 0;budget();
                int limit=isPipe()?Math.max(0,rate()-filled):Integer.MAX_VALUE;
                int accepted=tank.fill(fluid.copyWithAmount(Math.min(limit,fluid.getAmount())),action);
                if(action.execute())filled+=accepted;return accepted;
            }
            public FluidStack drain(FluidStack fluid,FluidAction action){
                if(!drainAllowed() || fluid.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid,tank.getFluid()))return FluidStack.EMPTY;
                return drain(fluid.getAmount(),action);
            }
            public FluidStack drain(int amount,FluidAction action){
                if(!drainAllowed())return FluidStack.EMPTY;budget();
                var result=tank.drain(Math.min(amount,remaining()),action);if(action.execute())drained+=result.getAmount();return result;
            }
        };
    }
    @Override public void onLoad(){super.onLoad();if(level instanceof net.minecraft.server.level.ServerLevel)EctoFluidNetworks.add(this);}
    @Override public void setRemoved(){EctoFluidNetworks.remove(this);super.setRemoved();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){
        super.saveAdditional(tag,registries);tag.put("Tank",tank.writeToNBT(registries,new CompoundTag()));tag.putByteArray("Modes",modes);
    }
    @Override public void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){
        super.loadAdditional(tag,registries);tank.readFromNBT(registries,tag.getCompound("Tank"));
        if(tank.getFluidAmount()>tank.getCapacity())tank.setFluid(tank.getFluid().copyWithAmount(tank.getCapacity()));
        var saved=tag.getByteArray("Modes");for(int i=0;i<6;i++)modes[i]=(byte)(i<saved.length && saved[i]>=0 && saved[i]<=3?saved[i]:0);
        EctoFluidNetworks.changed(this);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
