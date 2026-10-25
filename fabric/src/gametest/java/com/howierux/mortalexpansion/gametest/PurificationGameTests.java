package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.machine.*;
import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import java.util.*;

public final class PurificationGameTests implements FabricGameTest {
 private static BlockPos at(GameTestHelper h,int x){return h.absolutePos(new BlockPos(x,2,2));}
 private static short value(GameTestHelper h,BlockEntity be,String key){return be.saveWithoutMetadata(h.getLevel().registryAccess()).getShort(key);}
 private static HauntFurnaceEntity furnace(GameTestHelper h,int x,Block b){h.getLevel().setBlockAndUpdate(at(h,x),b.defaultBlockState());return(HauntFurnaceEntity)h.getLevel().getBlockEntity(at(h,x));}
 private static void tick(GameTestHelper h,HauntFurnaceEntity f){HauntFurnaceEntity.serverTick(h.getLevel(),f.getBlockPos(),f.getBlockState(),f);}
 @GameTest(template=EMPTY_STRUCTURE,batch="purification")
 public void furnace_speed_rescales_and_haint_preserves_fuel_and_eye_states(GameTestHelper h){
  var level=h.getLevel();var f=furnace(h,1,CatalogBlocks.HAUNT_FURNACE);var haint=furnace(h,2,CatalogBlocks.HAINT_FURNACE);
  int before=HauntManager.getHundredths(level,f.getBlockPos());
  try{
   HauntManager.setValue(level,f.getBlockPos(),15);f.setItem(0,new ItemStack(Items.IRON_ORE,4));f.setItem(1,new ItemStack(Items.COAL));
   for(int i=0;i<40;i++)tick(h,f);
   h.assertTrue(value(h,f,"CookTimeTotal")==80,"Natural smelting is 2.5x vanilla speed");
   HauntManager.setValue(level,f.getBlockPos(),69);tick(h,f);
   h.assertTrue(value(h,f,"CookTimeTotal")==20&&value(h,f,"CookTime")>0&&value(h,f,"CookTime")<20,"Tier notification rescales active recipe progress");
   HauntManager.setValue(level,haint.getBlockPos(),45);haint.setItem(0,new ItemStack(Items.IRON_ORE,4));haint.setItem(1,new ItemStack(Items.COAL));tick(h,haint);
   int burn=value(h,haint,"BurnTime");tick(h,haint);
   h.assertTrue(value(h,haint,"BurnTime")==burn&&haint.getBlockState().getValue(HauntFurnaceBlock.Haint.HAUNT_POWERED),"Demonic Haint cooking keeps burn time and opens ghast eyes");
   HauntManager.setValue(level,haint.getBlockPos(),15);tick(h,haint);
   h.assertTrue(value(h,haint,"BurnTime")==burn-1&&!haint.getBlockState().getValue(HauntFurnaceBlock.Haint.HAUNT_POWERED),"Below Demonic cooking consumes fuel and closes eyes");
   haint.setItem(2,new ItemStack(Items.DIAMOND,64));int idle=value(h,haint,"BurnTime");for(int i=0;i<5;i++)tick(h,haint);
   h.assertTrue(value(h,haint,"BurnTime")==idle,"Blocked Haint furnace does not waste fuel");
  }finally{HauntManager.setValue(level,f.getBlockPos(),0);HauntManager.addHundredths(level,f.getBlockPos(),before);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="purification")
 public void vanilla_and_haunt_fluid_cooking_return_buckets_only_on_completion(GameTestHelper h){
  var level=h.getLevel();level.setBlockAndUpdate(at(h,1),Blocks.FURNACE.defaultBlockState());var vanilla=(AbstractFurnaceBlockEntity)level.getBlockEntity(at(h,1));
  Item[] inputs={CatalogItems.HOLY_WATER_BUCKET,CatalogItems.LIQUID_ECTOPLASM_BUCKET,CatalogItems.PURIFIED_ECTOPLASM_BUCKET};
  Item[] outputs={CatalogItems.HOLY_SALT,CatalogItems.RAW_ECTOPLASM,CatalogItems.PURE_ECTOPLASM};
  for(int j=0;j<3;j++){
   vanilla.setItem(0,new ItemStack(inputs[j]));vanilla.setItem(1,new ItemStack(Items.COAL));vanilla.setItem(2,ItemStack.EMPTY);
   for(int i=0;i<205;i++)AbstractFurnaceBlockEntity.serverTick(level,vanilla.getBlockPos(),level.getBlockState(vanilla.getBlockPos()),vanilla);
   h.assertTrue(vanilla.getItem(2).is(outputs[j])&&vanilla.getItem(2).getCount()==1&&vanilla.getItem(0).is(Items.BUCKET),"Native vanilla recipe returns one vessel for fluid "+j);
  }
  vanilla.setItem(0,new ItemStack(CatalogItems.HOLY_WATER_BUCKET));vanilla.setItem(2,new ItemStack(Items.DIAMOND,64));
  for(int i=0;i<205;i++)AbstractFurnaceBlockEntity.serverTick(level,vanilla.getBlockPos(),level.getBlockState(vanilla.getBlockPos()),vanilla);
  h.assertTrue(vanilla.getItem(0).is(CatalogItems.HOLY_WATER_BUCKET),"Blocked output neither consumes fluid nor returns a premature bucket");
  var custom=furnace(h,2,CatalogBlocks.HAUNT_FURNACE);custom.setItem(0,new ItemStack(CatalogItems.HOLY_WATER_BUCKET));custom.setItem(1,new ItemStack(Items.COAL));for(int i=0;i<205;i++)tick(h,custom);
  h.assertTrue(custom.getItem(0).is(Items.BUCKET)&&custom.getItem(2).is(CatalogItems.HOLY_SALT),"Same successful-smelt callback covers custom furnace");h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="purification")
 public void station_purifies_with_saved_partial_energy_and_accepts_generator_and_pipe_outputs(GameTestHelper h){
  var level=h.getLevel();var pos=at(h,1);level.setBlockAndUpdate(pos,CatalogBlocks.PURIFICATION_STATION.defaultBlockState());var station=(PurificationStationEntity)level.getBlockEntity(pos);
  int before=HauntManager.getHundredths(level,pos);HauntManager.setValue(level,pos,15);
  try{
   var items=ItemStorage.SIDED.find(level,pos,Direction.UP);
   try(var tx=Transaction.openOuter()){h.assertTrue(items.insert(ItemVariant.of(CatalogItems.RAW_ECTOPLASM),2,tx)==2&&items.insert(ItemVariant.of(CatalogItems.HOLY_SALT),2,tx)==2,"Tagged Holy Salt is accepted by native item automation");tx.commit();}
   h.assertTrue(station.canPlaceItem(1,new ItemStack(CatalogItems.GRAVE_MOSS)),"Grave Moss remains a purification reagent");
   station.setItem(3,new ItemStack(Items.WATER_BUCKET));try(var tx=Transaction.openOuter()){station.energy.insert(10000,tx);tx.commit();}
   for(int i=0;i<20;i++)station.tick(level);
   h.assertTrue(station.feUsed()==10000&&station.progress()>0&&station.getItem(3).is(Items.BUCKET),"Underpowered station pauses after consuming only available energy and returns bucket");
   var restored=(PurificationStationEntity)BlockEntity.loadStatic(pos,station.getBlockState(),station.saveWithFullMetadata(level.registryAccess()),level.registryAccess());restored.setLevel(level);
   h.assertTrue(restored.feUsed()==station.feUsed()&&restored.progress()==station.progress(),"In-flight reservation and partial energy survive native NBT");
   for(int i=0;i<9;i++)try(var tx=Transaction.openOuter()){restored.energy.insert(10000,tx);tx.commit();}
   for(int i=0;i<60;i++)restored.tick(level);
   h.assertTrue(restored.energy.amount==0&&restored.getItem(2).is(CatalogItems.PURE_ECTOPLASM)&&restored.getItem(2).getCount()==1&&restored.getItem(0).getCount()==1&&restored.getItem(1).getCount()==1,"Exactly one reserved recipe consumes 100000 energy and produces purified ectoplasm");
   h.assertTrue(HauntManager.getHundredths(level,pos)==1600,"Completed purification adds one chunk Haunt");
   level.setBlockAndUpdate(pos.east(),CatalogBlocks.ECTOPLASMIC_GENERATOR.defaultBlockState());var gen=(GeneratorEntity)level.getBlockEntity(pos.east());gen.energy.amount=10000;gen.tick(level);
   h.assertTrue(station.energy.amount==2000,"Existing generator pushes energy into station through native API");
   level.setBlockAndUpdate(pos.above(),CatalogBlocks.ECTO_PIPE.defaultBlockState());level.setBlockAndUpdate(pos.above(2),CatalogBlocks.ECTANK.defaultBlockState());
   var pipe=(FluidMachineEntity)level.getBlockEntity(pos.above());var tank=(FluidMachineEntity)level.getBlockEntity(pos.above(2));
   FluidNetworks.add(pipe);FluidNetworks.add(tank);pipe.cycle(Direction.UP);
   try(var tx=Transaction.openOuter()){tank.tank.insert(FluidVariant.of(Fluids.WATER),FluidConstants.BUCKET,tx);tx.commit();}FluidNetworks.tick(level);
   h.assertTrue(station.water.amount==500*FluidMachineEntity.MB,"Ecto Pipe inserts water into the station at its configured rate");
   try(var tx=Transaction.openOuter()){h.assertTrue(station.water.extract(FluidVariant.of(Fluids.WATER),FluidConstants.BUCKET,tx)==0&&station.energy.extract(1000,tx)==0,"Station exposes input-only fluid and energy");}
   var drops=Block.getDrops(station.getBlockState(),level,pos,station);var portable=drops.getFirst().get(Machines.STATION_DATA);var placed=new PurificationStationEntity(pos,station.getBlockState());placed.restorePortable(portable);
   h.assertTrue(placed.water.amount==station.water.amount&&placed.energy.amount==station.energy.amount&&placed.isEmpty()&&placed.progress()==0,"Portable station retains water/energy without duplicating inventory or active craft");
  }finally{HauntManager.setValue(level,pos,0);HauntManager.addHundredths(level,pos,before);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="purification",timeoutTicks=300)
 public void ward_protection_tickets_and_persisted_owners_release_without_removing_forceload(GameTestHelper h){
  var level=h.getLevel();var pos=at(h,1);var center=new ChunkPos(pos);boolean forced=level.getForcedChunks().contains(center.toLong());
  level.setChunkForced(center.x,center.z,true);level.setBlockAndUpdate(pos,CatalogBlocks.PURIFICATION_WARD.defaultBlockState());PurificationWards.add(level,pos);PurificationWards.tick(level);
  h.runAfterDelay(25,()->{
   try{
    for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)h.assertTrue(level.getChunkSource().getChunkNow(center.x+dx,center.z+dz)!=null,"Ward tickets keep entire 3x3 area loaded");
    h.assertTrue(PurificationWardTracker.isChunkProtected(level,new ChunkPos(center.x+1,center.z+1))&&!PurificationWardTracker.isChunkProtected(level,new ChunkPos(center.x+2,center.z)),"Coverage is exactly three by three chunks");
    var saved=PurificationWards.owners(level).save(new CompoundTag(),level.registryAccess());h.assertTrue(PurificationWards.Owners.load(saved,level.registryAccess()).positions.contains(pos),"Owner positions survive SavedData serialization for restart restoration");
    long key=center.toLong();var start=Map.of(key,30);var result=HauntSimulation.plan(start,Map.of(key,15),true,true,4,net.minecraft.util.RandomSource.create(1),Set.of(key));
    h.assertTrue(result.get(key)==29,"Protected chunks cleanse once daily and skip recovery/spread");
    var overlap=pos.offset(1,0,0);PurificationWardTracker.add(level,overlap);level.removeBlock(pos,false);
    h.assertTrue(!PurificationWards.owners(level).positions.contains(pos)&&PurificationWardTracker.isChunkProtected(level,center),"Breaking Ward releases persisted owner while overlapping Ward retains protection");PurificationWardTracker.remove(level,overlap);
    h.assertFalse(PurificationWardTracker.isChunkProtected(level,center),"Last Ward removal clears protection immediately");
    h.assertTrue(level.getForcedChunks().contains(key),"Ward release does not remove vanilla forceload ownership");
   }finally{level.removeBlock(pos,false);PurificationWards.release(level,pos);if(!forced)level.setChunkForced(center.x,center.z,false);}h.succeed();
  });
 }
}
