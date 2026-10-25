package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.machine.*;
import com.howierux.mortalexpansion.haunt.HauntManager;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import java.util.*;

public final class MachineGameTests implements FabricGameTest {
 private static final Map<BlockPos,SimpleEnergyStorage> SINKS=new HashMap<>();
 static { EnergyStorage.SIDED.registerForBlocks((world,pos,state,be,side)->SINKS.get(pos),Blocks.PINK_CONCRETE); }
 private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h){
  var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(UUID.randomUUID(),"machine-test"),false);
  var p=new net.minecraft.server.level.ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());
  var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(connection);
  h.getLevel().getServer().getPlayerList().placeNewPlayer(connection,p,cookie);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);return p;
 }
 private static BlockPos at(GameTestHelper h,int x){return h.absolutePos(new BlockPos(x,2,2));}
 private static FluidMachineEntity fluid(GameTestHelper h,int x,net.minecraft.world.level.block.Block block){var p=at(h,x);h.getLevel().setBlockAndUpdate(p,block.defaultBlockState());return(FluidMachineEntity)h.getLevel().getBlockEntity(p);}
 private static GeneratorEntity generator(GameTestHelper h,int x,net.minecraft.world.level.block.Block block){var p=at(h,x);h.getLevel().setBlockAndUpdate(p,block.defaultBlockState());return(GeneratorEntity)h.getLevel().getBlockEntity(p);}
 private static final FluidVariant WATER=FluidVariant.of(Fluids.WATER);
 private static final long MB=FluidMachineEntity.MB;
 private static void fill(FluidMachineEntity be,long mb){try(var tx=Transaction.openOuter()){be.tank.insert(WATER,mb*MB,tx);tx.commit();}}
 @GameTest(template=EMPTY_STRUCTURE,batch="machines")
 public void fluid_transactions_side_limits_and_portable_contents(GameTestHelper h){
  var normal=fluid(h,1,CatalogBlocks.ECTANK);var pure=fluid(h,2,CatalogBlocks.PURIFIED_ECTANK);
  var pipe=fluid(h,3,CatalogBlocks.ECTO_PIPE);var purifiedPipe=fluid(h,4,CatalogBlocks.PURIFIED_ECTO_PIPE);
  h.assertTrue(normal.capacityMb()==16000&&pure.capacityMb()==64000&&pipe.capacityMb()==2000&&purifiedPipe.capacityMb()==8000,"Original four capacities retained");
  var sided=FluidStorage.SIDED.find(h.getLevel(),pipe.getBlockPos(),Direction.WEST);
  try(var tx=Transaction.openOuter()){h.assertTrue(sided.insert(WATER,1000*MB,tx)==500*MB,"Normal pipe caps insertion at 500 mB");}
  h.assertTrue(pipe.tank.amount==0,"Aborted transaction restores actual contents");
  try(var tx=Transaction.openOuter()){h.assertTrue(sided.insert(WATER,1000*MB,tx)==500*MB,"Rollback restores rate budget too");tx.commit();}
  var westArm=new net.minecraft.world.phys.BlockHitResult(pipe.getBlockPos().getCenter().add(-0.45,0.1,0),Direction.UP,pipe.getBlockPos(),false);
  h.assertTrue(FluidMachineBlock.selectedSide(pipe.getBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST,true),pipe.getBlockPos(),westArm)==Direction.WEST,"Extended arm selects its connection side rather than its top face");
  pipe.cycle(Direction.WEST); // Input only.
  try(var tx=Transaction.openOuter()){h.assertTrue(StorageUtil.move(sided,normal.tank,v->true,1000*MB,tx)==0,"Storage views cannot bypass input-only extraction");}
  pipe.cycle(Direction.WEST);pipe.cycle(Direction.WEST); // Disabled.
  try(var tx=Transaction.openOuter()){h.assertTrue(sided.insert(WATER,MB,tx)==0&&sided.extract(WATER,MB,tx)==0,"Disabled side blocks both directions");}
  fill(pure,63000);
  var tag=pure.saveWithFullMetadata(h.getLevel().registryAccess());
  var restored=(FluidMachineEntity)net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pure.getBlockPos(),pure.getBlockState(),tag,h.getLevel().registryAccess());
  h.assertTrue(restored!=null&&restored.tank.amount==63000*MB&&restored.tank.variant.equals(WATER),"Native block entity NBT restores precise fluid contents");
  var modeTag=pipe.saveWithFullMetadata(h.getLevel().registryAccess());
  var restoredPipe=(FluidMachineEntity)net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pipe.getBlockPos(),pipe.getBlockState(),modeTag,h.getLevel().registryAccess());
  h.assertTrue(restoredPipe.mode(Direction.WEST)==3&&restoredPipe.tank.amount==500*MB,"Side mode and buffer survive native reload");
  var drops=net.minecraft.world.level.block.Block.getDrops(pure.getBlockState(),h.getLevel(),pure.getBlockPos(),pure);
  h.assertTrue(drops.size()==1&&drops.getFirst().has(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA),"Breaking a tank preserves fluid in its dropped item");
  var p=player(h);
  try {
   fill(normal,16000);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(Items.WATER_BUCKET));
   var hit=new net.minecraft.world.phys.BlockHitResult(normal.getBlockPos().getCenter(),Direction.UP,normal.getBlockPos(),false);
   var result=normal.getBlockState().useItemOn(p.getMainHandItem(),h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND,hit);
   h.assertTrue(result==net.minecraft.world.ItemInteractionResult.SUCCESS&&p.getMainHandItem().is(Items.WATER_BUCKET)&&h.getLevel().getBlockState(normal.getBlockPos().above()).isAir(),"Rejected bucket is handled without spilling above a full tank");
   p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(Items.BUCKET));
   normal.getBlockState().useItemOn(p.getMainHandItem(),h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND,hit);
   h.assertTrue(p.getMainHandItem().is(Items.WATER_BUCKET)&&normal.tank.amount==15000*MB,"Empty bucket withdraws exactly 1000 mB");
   var portable=drops.getFirst();p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,portable);
   var target=at(h,6);var placeHit=new net.minecraft.world.phys.BlockHitResult(target.getCenter(),Direction.UP,target,false);
   var place=new net.minecraft.world.item.context.BlockPlaceContext(p,net.minecraft.world.InteractionHand.MAIN_HAND,portable,placeHit);
   ((BlockItem)portable.getItem()).place(place);
   var placed=(FluidMachineEntity)h.getLevel().getBlockEntity(target);
   h.assertTrue(placed!=null&&placed.tank.amount==63000*MB,"Native BlockItem placement restores the portable tank's exact contents");
  } finally {p.getServer().getPlayerList().remove(p);p.discard();}
  h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="machines")
 public void cached_network_conserves_fluid_and_respects_mixed_tier_bottleneck(GameTestHelper h){
  var source=fluid(h,0,CatalogBlocks.PURIFIED_ECTANK);var input=fluid(h,1,CatalogBlocks.PURIFIED_ECTO_PIPE);
  var middle=fluid(h,2,CatalogBlocks.ECTO_PIPE);var output=fluid(h,3,CatalogBlocks.PURIFIED_ECTO_PIPE);var target=fluid(h,4,CatalogBlocks.PURIFIED_ECTANK);
  for(var be:List.of(source,input,middle,output,target))FluidNetworks.add(be);
  input.cycle(Direction.WEST);fill(source,8000);FluidNetworks.tick(h.getLevel());
  h.assertTrue(target.tank.amount==500*MB,"Normal pipe limits the entire purified path to 500 mB/tick");
  FluidNetworks.tick(h.getLevel());
  h.assertTrue(target.tank.amount==500*MB,"Multiple branches/passes share the same tick budget");
  h.assertTrue(source.tank.amount+input.tank.amount+middle.tank.amount+output.tank.amount+target.tank.amount==8000*MB,"No fluid lost in buffered routing");
  // Removing the middle pipe must invalidate the graph immediately.
  h.getLevel().removeBlock(middle.getBlockPos(),false);long before=target.tank.amount;FluidNetworks.tick(h.getLevel());
  h.assertTrue(target.tank.amount==before,"Removed pipes stop routing");h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="machines")
 public void ectoplasm_burn_pauses_when_full_finishes_haunt_and_exports_energy(GameTestHelper h){
  var be=generator(h,1,CatalogBlocks.ECTOPLASMIC_GENERATOR);var level=h.getLevel();
  int before=HauntManager.getHundredths(level,be.getBlockPos());HauntManager.setValue(level,be.getBlockPos(),15);
  try{
   var input=ItemStorage.SIDED.find(level,be.getBlockPos(),Direction.UP);
   try(var tx=Transaction.openOuter()){h.assertTrue(input.insert(ItemVariant.of(CatalogItems.RAW_ECTOPLASM),1,tx)==1,"Fabric automation inserts valid fuel");tx.commit();}
   be.tick(level);h.assertTrue(be.energy.amount==25&&be.remaining()==999&&be.getItem(0).isEmpty(),"Natural tier multiplier generates 25 energy/tick and consumes one raw fuel");
   be.energy.amount=be.capacity();int remaining=be.remaining();be.tick(level);h.assertTrue(be.remaining()==remaining,"Full buffer pauses burn");be.energy.amount=0;
   for(int i=0;i<remaining;i++)be.tick(level);
   h.assertTrue(HauntManager.getHundredths(level,be.getBlockPos())==1600,"Raw burn emits exactly one Haunt point on completion");
   be.energy.amount=0;be.setItem(0,new ItemStack(CatalogItems.PURE_ECTOPLASM));for(int i=0;i<2000;i++)be.tick(level);
   h.assertTrue(HauntManager.getHundredths(level,be.getBlockPos())==1900,"Purified burn emits three Haunt points");
   var sink=new SimpleEnergyStorage(100000,100000,0);var sinkAt=be.getBlockPos().east();SINKS.put(sinkAt,sink);level.setBlockAndUpdate(sinkAt,Blocks.PINK_CONCRETE.defaultBlockState());long buffered=be.energy.amount;be.tick(level);
   h.assertTrue(sink.amount==2000&&be.energy.amount==buffered-2000,"Generator pushes a shared 2000/tick budget through Fabric energy API");SINKS.remove(sinkAt);
   var api=EnergyStorage.SIDED.find(level,be.getBlockPos(),Direction.EAST);
   try(var tx=Transaction.openOuter()){h.assertTrue(api.insert(100,tx)==0,"Generator rejects incoming energy");long n=api.extract(9000,tx);h.assertTrue(n==2000,"Native energy API limits generator extraction");}
  }finally{HauntManager.setValue(level,be.getBlockPos(),0);HauntManager.addHundredths(level,be.getBlockPos(),before);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="machines")
 public void enchantment_generation_returns_item_and_saves_processing_without_truncating_menu_data(GameTestHelper h){
  var be=generator(h,1,CatalogBlocks.ENCHANTMENT_GENERATOR);var level=h.getLevel();
  var stack=new ItemStack(Items.ENCHANTED_BOOK);var enchant=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
  EnchantmentHelper.updateEnchantments(stack,mutable->mutable.set(enchant,2));be.setItem(0,stack);be.tick(level);
  h.assertTrue(be.getItem(1).isEmpty()&&be.remaining()>0,"Result stays inaccessible until generation completes");
  int remaining=be.remaining();var saved=be.saveWithFullMetadata(level.registryAccess());
  var restored=(GeneratorEntity)net.minecraft.world.level.block.entity.BlockEntity.loadStatic(be.getBlockPos(),be.getBlockState(),saved,level.registryAccess());
  h.assertTrue(restored!=null&&restored.remaining()==remaining&&restored.energy.amount==be.energy.amount,"Active enchantment conversion survives save/reload");
  restored.setLevel(level);for(int i=0;i<500;i++)restored.tick(level);
  h.assertTrue(restored.remaining()==0&&restored.getItem(1).is(Items.BOOK)&&EnchantmentHelper.getEnchantmentsForCrafting(restored.getItem(1)).isEmpty(),"Enchanted book returns ordinary book");
  be.energy.amount=399999;var data=be.menuData();int reconstructed=((short)data.get(0)&65535)|(((short)data.get(1)&65535)<<16);
  h.assertTrue(reconstructed==399999,"Menu's signed-short transport retains exact six-digit energy");
  var auto=ItemStorage.SIDED.find(level,be.getBlockPos(),Direction.DOWN);
  try(var tx=Transaction.openOuter()){h.assertTrue(auto.extract(ItemVariant.of(Items.ENCHANTED_BOOK),1,tx)==0,"Automation cannot extract active input or hidden result");}
  h.succeed();
 }
}
