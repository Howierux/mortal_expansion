package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.machine.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.*;

public final class AltarGameTests implements FabricGameTest {
 private static BlockPos at(GameTestHelper h,int x){return h.absolutePos(new BlockPos(x,2,2));}
 private static AltarEntity altar(GameTestHelper h,int x,Block block){h.getLevel().setBlockAndUpdate(at(h,x),block.defaultBlockState());return(AltarEntity)h.getLevel().getBlockEntity(at(h,x));}
 private record Snapshot(LevelChunk chunk,int units,int revision){}
 private static List<Snapshot> snapshot(GameTestHelper h){var result=new ArrayList<Snapshot>();for(var chunk:WorldHauntRituals.loadedChunks(h.getLevel())){HauntManager.initialize(h.getLevel(),chunk);var target=(AttachmentTarget)chunk;result.add(new Snapshot(chunk,target.getAttachedOrThrow(FabricHauntData.CHUNK),target.getAttachedOrCreate(FabricHauntData.RITUAL_REVISION)));}return result;}
 private static void restore(List<Snapshot> saved){for(var state:saved){var target=(AttachmentTarget)state.chunk();target.setAttached(FabricHauntData.CHUNK,state.units());target.setAttached(FabricHauntData.RITUAL_REVISION,state.revision());HauntFurnaceEntity.onChunkTierChanged(state.chunk(),HauntTier.fromValue(state.units()/100));}}
 @GameTest(template=EMPTY_STRUCTURE,batch="altars")
 public void offering_cadence_fractional_pulses_and_loaded_only_coverage(GameTestHelper h){
  var level=h.getLevel();var pure=altar(h,1,CatalogBlocks.ALTAR_OF_PURITY);var impure=altar(h,2,CatalogBlocks.ALTAR_OF_IMPURITY);var saved=snapshot(h);
  try{
   pure.setItem(0,new ItemStack(CatalogItems.RAW_ECTOPLASM,3));pure.tick(level);
   h.assertTrue(pure.getBurnTicks()==1199&&pure.getItem(0).getCount()==2,"First offering grants one minute and immediately begins burning");
   var center=new ChunkPos(pure.getBlockPos());
   for(int i=1;i<200;i++)pure.tick(level);
   for(var state:saved){var pos=state.chunk().getPos();boolean covered=Math.abs(pos.x-center.x)<=1&&Math.abs(pos.z-center.z)<=1;h.assertTrue(HauntManager.getHundredths(level,pos)==(covered?FractionalHaunt.add(state.units(),-10):state.units()),"A pulse changes exactly the loaded three-by-three area by 0.10");}
   for(int i=200;i<250;i++)pure.tick(level);h.assertTrue(pure.getItem(0).getCount()==2,"Fuel consumption waits the full 250-tick interval");pure.tick(level);h.assertTrue(pure.getItem(0).getCount()==1,"Next offering is consumed at 12.5 seconds");
   int before=HauntManager.getHundredths(level,impure.getBlockPos());impure.setItem(0,new ItemStack(CatalogItems.PURE_ECTOPLASM));for(int i=0;i<200;i++)impure.tick(level);
   h.assertTrue(HauntManager.getHundredths(level,impure.getBlockPos())==FractionalHaunt.add(before,10)&&impure.getBurnTicks()==2200,"Impurity reverses the pulse and purified fuel lasts two minutes");
   var absent=new ChunkPos(center.x+100000,center.z+100000);h.assertTrue(level.getChunkSource().getChunkNow(absent.x,absent.z)==null,"Altar does not load distant chunks");
   h.assertTrue(pure.getBlockState().getLightEmission()==10,"Burning altar emits the original light level");
  }finally{level.removeBlock(pure.getBlockPos(),false);level.removeBlock(impure.getBlockPos(),false);restore(saved);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="altars")
 public void native_automation_remainders_saved_burn_cap_and_offering_hooks(GameTestHelper h){
  var level=h.getLevel();var pure=altar(h,1,CatalogBlocks.ALTAR_OF_PURITY);var impure=altar(h,2,CatalogBlocks.ALTAR_OF_IMPURITY);
  try{
   pure.setItem(0,new ItemStack(Items.MILK_BUCKET));pure.setItem(1,new ItemStack(Items.BUCKET,16));pure.tick(level);
   h.assertTrue(pure.getBurnTicks()==0&&pure.getItem(0).is(Items.MILK_BUCKET),"Full returned-item slot blocks offering without losing its vessel");
   pure.setItem(1,ItemStack.EMPTY);pure.tick(level);h.assertTrue(pure.getItem(1).is(Items.BUCKET)&&pure.getItem(0).isEmpty(),"Data-pack offering returns native crafting remainder");
   var auto=ItemStorage.SIDED.find(level,pure.getBlockPos(),Direction.DOWN);
   try(var tx=Transaction.openOuter()){h.assertTrue(auto.extract(ItemVariant.of(Items.BUCKET),1,tx)==1,"Automation can remove returned vessels");tx.commit();}
   pure.setItem(0,new ItemStack(CatalogItems.RAW_ECTOPLASM,2));
   try(var tx=Transaction.openOuter()){h.assertTrue(auto.extract(ItemVariant.of(CatalogItems.RAW_ECTOPLASM),1,tx)==0&&auto.insert(ItemVariant.of(Items.DIAMOND),1,tx)==0,"Automation cannot steal fuel or insert unrelated items");}
   var tag=pure.saveWithoutMetadata(level.registryAccess());tag.putInt("BurnTicks",72000);tag.putInt("OfferingDelay",0);pure.loadWithComponents(tag,level.registryAccess());pure.tick(level);
   h.assertTrue(pure.getBurnTicks()==71999&&pure.getItem(0).getCount()==2,"Full burn reserve rejects extra fuel without consumption");
   var data=pure.menuData();h.assertTrue((((short)data.get(0)&65535)|(((short)data.get(1)&65535)<<16))==71999,"Signed-short menu packets retain full reserve");
   var loaded=(AltarEntity)BlockEntity.loadStatic(pure.getBlockPos(),pure.getBlockState(),pure.saveWithFullMetadata(level.registryAccess()),level.registryAccess());h.assertTrue(loaded.getBurnTicks()==71999&&loaded.getItem(0).getCount()==2,"Native save restores burn reserve and inventory");
   tag.putInt("BurnTicks",0);pure.loadWithComponents(tag,level.registryAccess());h.assertTrue(pure.blessNextOffering()&&!pure.blessNextOffering(),"Blessing hook affects one pending offering");pure.tick(level);
   h.assertTrue(pure.getBurnTicks()==1799&&!pure.hasBlessedOffering(),"Blessing adds fifty percent once");pure.setItem(0,ItemStack.EMPTY);h.assertTrue(pure.addOfferingGift()&&pure.getItem(0).is(CatalogItems.HOLY_SALT)&&!impure.addOfferingGift()&&!impure.blessNextOffering(),"Positive hooks target purity only");
  }finally{level.removeBlock(pure.getBlockPos(),false);level.removeBlock(impure.getBlockPos(),false);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="altars")
 public void ritual_history_and_chunk_revision_roundtrip_preserve_order_and_fractions(GameTestHelper h){
  var level=h.getLevel();var data=new WorldHauntRitualData();data.record(true);data.record(false);
  var chunk=new LevelChunk(level,new ChunkPos(100000,100000));var target=(AttachmentTarget)chunk;target.setAttached(FabricHauntData.CHUNK,6900);data.apply(chunk);
  h.assertTrue(target.getAttachedOrThrow(FabricHauntData.CHUNK)==5900,"Raise then cleanse at cap differs from a net-zero offset");data.apply(chunk);h.assertTrue(target.getAttachedOrThrow(FabricHauntData.CHUNK)==5900,"A revision is applied only once");
  var journal=WorldHauntRitualData.load(data.save(new CompoundTag(),level.registryAccess()),level.registryAccess());
  var second=new LevelChunk(level,new ChunkPos(100001,100000));var secondTarget=(AttachmentTarget)second;secondTarget.setAttached(FabricHauntData.CHUNK,2537);secondTarget.setAttached(FabricHauntData.RITUAL_REVISION,1);journal.apply(second);
  h.assertTrue(secondTarget.getAttachedOrThrow(FabricHauntData.CHUNK)==1537,"Only pending shifts apply, retaining hundredths");
  var nbt=net.minecraft.world.level.chunk.storage.ChunkSerializer.write(level,second);
  var loaded=net.minecraft.world.level.chunk.storage.ChunkSerializer.read(level,level.getPoiManager(),new net.minecraft.world.level.chunk.storage.RegionStorageInfo("ritual-test",level.dimension(),"chunk"),second.getPos(),nbt);
  var full=((net.minecraft.world.level.chunk.ImposterProtoChunk)loaded).getWrapped();journal.apply(full);h.assertTrue(((AttachmentTarget)full).getAttachedOrThrow(FabricHauntData.CHUNK)==1537&&((AttachmentTarget)full).getAttachedOrCreate(FabricHauntData.RITUAL_REVISION)==2,"Native chunk NBT preserves exactly-once revision");h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="altars")
 public void ritual_star_crafting_real_altars_overworld_only_and_future_chunks(GameTestHelper h){
  var level=h.getLevel();var pure=altar(h,1,CatalogBlocks.ALTAR_OF_PURITY);var impure=altar(h,2,CatalogBlocks.ALTAR_OF_IMPURITY);
  var prior=WorldHauntRitualData.get(level).save(new CompoundTag(),level.registryAccess());var saved=snapshot(h);
  try{
   level.getDataStorage().set(WorldHauntRitualData.KEY,new WorldHauntRitualData());for(var state:saved)((AttachmentTarget)state.chunk()).setAttached(FabricHauntData.RITUAL_REVISION,0);
   var grid=new ArrayList<ItemStack>();for(int i=0;i<9;i++)grid.add(new ItemStack(i==4?Items.NETHER_STAR:CatalogItems.BLOODSTONE));
   var recipe=level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,CraftingInput.of(3,3,grid),level);
   h.assertTrue(recipe.isPresent()&&recipe.get().value().getResultItem(level.registryAccess()).is(CatalogItems.RITUAL_STAR),"Native generated recipe crafts Nether Star surrounded by eight Bloodstone");
   HauntManager.setValue(level,pure.getBlockPos(),69);HauntManager.setValue(level,impure.getBlockPos(),69);impure.setItem(0,new ItemStack(CatalogItems.RITUAL_STAR));impure.tick(level);
   h.assertTrue(impure.getItem(0).isEmpty()&&impure.getBurnTicks()==0&&WorldHauntRitualData.get(level).revision()==1&&HauntManager.getValue(level,impure.getBlockPos())==69,"Sacrificed Star records one capped raise without becoming fuel");
   var far=new ChunkPos(200000,200000);h.assertTrue(level.getChunkSource().getChunkNow(far.x,far.z)==null,"Global ritual never force-loads absent chunks");
   var future=new LevelChunk(level,far);HauntManager.initialize(level,future);h.assertTrue(((AttachmentTarget)future).getAttachedOrThrow(FabricHauntData.CHUNK)==2500&&NaturalHauntGenerator.determineInitialTier(level,future)==HauntTier.NATURAL,"Future chunks receive rise while natural baseline stays Natural");
   var nether=level.getServer().getLevel(Level.NETHER);var end=level.getServer().getLevel(Level.END);var netherAltar=new AltarEntity(pure.getBlockPos(),CatalogBlocks.ALTAR_OF_IMPURITY.defaultBlockState());netherAltar.setLevel(nether);netherAltar.setItem(0,new ItemStack(CatalogItems.RITUAL_STAR));netherAltar.tick(nether);
   h.assertTrue(!netherAltar.canPlaceItem(0,new ItemStack(CatalogItems.RITUAL_STAR))&&netherAltar.getItem(0).is(CatalogItems.RITUAL_STAR)&&!WorldHauntRituals.activate(end,pure.getBlockPos(),true),"Other dimensions reject ritual and retain Star");
   pure.setItem(0,new ItemStack(CatalogItems.RITUAL_STAR));pure.tick(level);h.assertTrue(pure.getItem(0).isEmpty()&&WorldHauntRitualData.get(level).revision()==2&&HauntManager.getValue(level,pure.getBlockPos())==59,"Purity Star lowers the already capped value by ten");
   HauntManager.initialize(level,future);h.assertTrue(((AttachmentTarget)future).getAttachedOrThrow(FabricHauntData.CHUNK)==1500,"Previously unloaded chunk reconciles next revision exactly once");
  }finally{var previous=WorldHauntRitualData.load(prior,level.registryAccess());previous.setDirty();level.getDataStorage().set(WorldHauntRitualData.KEY,previous);level.removeBlock(pure.getBlockPos(),false);level.removeBlock(impure.getBlockPos(),false);restore(saved);}h.succeed();
 }
}
