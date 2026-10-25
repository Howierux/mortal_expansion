package com.howierux.mortalexpansion.machine;

import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

/** Fabric uses 81 transfer units per mB. All public capacities/rates remain in the original mB. */
public final class FluidMachineEntity extends BlockEntity {
 public static final long MB=FluidConstants.BUCKET/1000;
 public static final int NORMAL=0,INPUT=1,OUTPUT=2,DISABLED=3;
 private final byte[] modes=new byte[6];
 private final List<Storage<FluidVariant>> sides=new ArrayList<>(Collections.nCopies(7,null));
 private long budgetTick=Long.MIN_VALUE,filled,drained;
 private final SnapshotParticipant<long[]> budget=new SnapshotParticipant<>() {
  @Override protected long[] createSnapshot(){return new long[]{filled,drained};}
  @Override protected void readSnapshot(long[] s){filled=s[0];drained=s[1];}
 };
 public final SingleVariantStorage<FluidVariant> tank=new SingleVariantStorage<>() {
  @Override protected FluidVariant getBlankVariant(){return FluidVariant.blank();}
  @Override protected long getCapacity(FluidVariant v){return capacityMb()*MB;}
  @Override protected void onFinalCommit(){changed();}
 };
 public FluidMachineEntity(BlockPos pos,BlockState state){super(Machines.FLUID,pos,state);}
 public boolean pipe(){return getBlockState().getBlock() instanceof FluidMachineBlock.Pipe;}
 public boolean purified(){return getBlockState().is(CatalogBlocks.PURIFIED_ECTANK)||getBlockState().is(CatalogBlocks.PURIFIED_ECTO_PIPE);}
 public int capacityMb(){return pipe()?(purified()?8000:2000):(purified()?64000:16000);}
 public long rate(){return (purified()?2000:500)*MB;}
 public int mode(Direction side){return modes[side.ordinal()];}
 public void cycle(Direction side){modes[side.ordinal()]=(byte)((mode(side)+1)%4);changed();FluidNetworks.invalidate(this);}
 private void resetBudget(){long now=level==null?0:level.getGameTime();if(budgetTick!=now){budgetTick=now;filled=drained=0;}}
 public long remaining(){resetBudget();return pipe()?Math.max(0,rate()-drained):Long.MAX_VALUE;}
 public void reserve(long amount,TransactionContext tx){resetBudget();budget.updateSnapshots(tx);drained+=amount;}
 public Storage<FluidVariant> storage(Direction side){
  int index=side==null?6:side.ordinal();if(sides.get(index)!=null)return sides.get(index);
  Storage<FluidVariant> storage=new Storage<>() {
   @Override public long insert(FluidVariant v,long max,TransactionContext tx){
    if(max<0)throw new IllegalArgumentException("Negative fluid insertion");
    if(side!=null&&pipe()&&(mode(side)==OUTPUT||mode(side)==DISABLED))return 0;
    resetBudget();long limit=pipe()?Math.max(0,rate()-filled):Long.MAX_VALUE;
    long n=tank.insert(v,Math.min(limit,max),tx);if(n>0){budget.updateSnapshots(tx);filled+=n;}return n;
   }
   @Override public long extract(FluidVariant v,long max,TransactionContext tx){
    if(max<0)throw new IllegalArgumentException("Negative fluid extraction");
    if(side!=null&&pipe()&&(mode(side)==INPUT||mode(side)==DISABLED))return 0;
    long n=tank.extract(v,Math.min(max,remaining()),tx);if(n>0){budget.updateSnapshots(tx);drained+=n;}return n;
   }
   @Override public Iterator<StorageView<FluidVariant>> iterator(){
    var owner=this;
    return Collections.<StorageView<FluidVariant>>singleton(new StorageView<>() {
     public boolean isResourceBlank(){return tank.isResourceBlank();}
     public FluidVariant getResource(){return tank.variant;}
     public long getAmount(){return tank.amount;}
     public long getCapacity(){return tank.getCapacity();}
     public long extract(FluidVariant v,long max,TransactionContext tx){return owner.extract(v,max,tx);}
    }).iterator();
   }
  };
  sides.set(index,storage);return storage;
 }
 private void changed(){setChanged();FluidNetworks.changed(this);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);SingleVariantStorage.writeNbt(tank,FluidVariant.CODEC,tag,lookup);tag.putByteArray("Modes",modes);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
  super.loadAdditional(tag,lookup);SingleVariantStorage.readNbt(tank,FluidVariant.CODEC,FluidVariant::blank,tag,lookup);
  tank.amount=tank.variant.isBlank()?0:Math.max(0,Math.min(tank.amount,capacityMb()*MB));if(tank.amount==0)tank.variant=FluidVariant.blank();
  byte[] saved=tag.getByteArray("Modes");for(int i=0;i<6;i++)modes[i]=(byte)(i<saved.length&&saved[i]>=0&&saved[i]<=3?saved[i]:0);
  budgetTick=Long.MIN_VALUE;FluidNetworks.invalidate(this);FluidNetworks.changed(this);
 }
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
