package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.worldgen.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import java.util.*;
public final class TerrainReaderGameTests implements FabricGameTest {
 private static ServerPlayer player(GameTestHelper h){var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(UUID.randomUUID(),"reader-test"),false);var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());var c=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(c);h.getLevel().getServer().getPlayerList().placeNewPlayer(c,p,cookie);p.setPos(h.absolutePos(new BlockPos(2,2,2)).getCenter());return p;}
 @GameTest(template=EMPTY_STRUCTURE,batch="terrain_readers")
 public void held_readers_sample_fractional_haunt_and_reject_stale_owners_or_chunks(GameTestHelper h){
  var p=player(h);try{
   var level=h.getLevel();var stack=new ItemStack(CatalogItems.EMF_READER);p.setItemInHand(InteractionHand.OFF_HAND,stack);
   HauntManager.setValue(level,p.chunkPosition(),0);HauntManager.addHundredths(level,p.chunkPosition(),2345);PlayerHauntManager.setValue(p,0);PlayerHauntManager.addHundredths(p,4567);
   ((EmfReaderItem)stack.getItem()).inventoryTick(stack,level,p,40,false);
   h.assertTrue(EmfReaderItem.hundredths(stack,p)==2345,"Offhand reads exact loaded chunk Haunt");
   stack.getItem().use(level,p,InteractionHand.OFF_HAND);h.assertTrue(EmfReaderItem.personal(stack,p)&&EmfReaderItem.hundredths(stack,p)==4567,"Held use selects personal Haunt");p.stopUsingItem();
   p.setPos(p.getX()+16,p.getY(),p.getZ());h.assertTrue(EmfReaderItem.reading(stack,p)==null,"Crossing chunks invalidates stale readings immediately");
   stack.set(EmfReaderItem.EMF_READING,new EmfReading(level.dimension().location(),p.chunkPosition().x,p.chunkPosition().z,UUID.randomUUID(),2345,4567));h.assertTrue(EmfReaderItem.reading(stack,p)==null,"Transferred sample rejects another owner");
   h.assertTrue(!stack.save(level.registryAccess()).toString().contains("emf_reading"),"Transient readings never persist to equipment NBT");
   p.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);((EmfReaderItem)stack.getItem()).inventoryTick(stack,level,p,4,false);h.assertTrue(stack.get(EmfReaderItem.EMF_READING)==null,"Unequipped readers clear samples");
   h.assertTrue(EmfScale.decimal(2345).equals("23.45")&&EmfScale.modelLevel(70)==7&&EmfScale.modelLevel(130)==12,"Decimal and future overflow display bands remain supported");
  }finally{p.getServer().getPlayerList().remove(p);p.discard();}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="terrain_readers")
 public void native_sapling_growth_places_spiritwood_and_grave_moss(GameTestHelper h){
  var level=h.getLevel();var pos=h.absolutePos(new BlockPos(5,3,5));
  for(var at:BlockPos.betweenClosed(pos.offset(-5,-1,-5),pos.offset(5,11,5)))level.setBlockAndUpdate(at,at.getY()==pos.getY()-1?Blocks.DIRT.defaultBlockState():Blocks.AIR.defaultBlockState());
  var block=(SaplingBlock)CatalogBlocks.SPIRITWOOD_SAPLING;level.setBlockAndUpdate(pos,block.defaultBlockState());var random=net.minecraft.util.RandomSource.create(42);
  block.advanceTree(level,pos,level.getBlockState(pos),random);block.advanceTree(level,pos,level.getBlockState(pos),random);
  int logs=0,leaves=0,moss=0;for(var at:BlockPos.betweenClosed(pos.offset(-5,0,-5),pos.offset(5,11,5))){var state=level.getBlockState(at);if(state.is(CatalogBlocks.SPIRITWOOD_LOG))logs++;if(state.is(CatalogBlocks.SPIRITWOOD_LEAVES))leaves++;if(state.is(CatalogBlocks.GRAVE_MOSS))moss++;}
  h.assertTrue(logs>=4&&leaves>0&&moss>0,"Native sapling grows real Spiritwood and decorates new leaves with moss");h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="terrain_readers")
 public void terrain_registries_climate_membership_and_ore_rules_load(GameTestHelper h){
  var level=h.getLevel();var biomes=level.registryAccess().registryOrThrow(Registries.BIOME);
  var forest=biomes.getHolderOrThrow(ModBiomes.SPIRITWOOD_FOREST);var grave=biomes.getHolderOrThrow(ModBiomes.GRAVEYARD);
  h.assertTrue(forest.is(net.minecraft.tags.BiomeTags.IS_OVERWORLD)&&grave.is(net.minecraft.tags.BiomeTags.IS_OVERWORLD),"Native biome tags include both generated biomes");
  h.assertTrue(forest.value().getGenerationSettings().hasFeature(level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).get(TerrainRuntime.placed("spiritwood_trees"))),"Forest includes tree placement");
  h.assertTrue(level.registryAccess().registryOrThrow(Registries.STRUCTURE_SET).containsKey(Catalog.id("graveyard_cemetery")),"Cemetery structure set loads through generated registry data");
  h.assertTrue(BloodstoneFormation.due(11999,12000)&&BloodstoneFormation.due(17999,18000)&&BloodstoneFormation.due(23999,24000)&&!BloodstoneFormation.due(0,8000),"Formation checks occur at dawn sunset and midnight");
  h.assertTrue(BloodstoneFormation.chance(35)==.005F&&BloodstoneFormation.chance(45)==.01F&&BloodstoneFormation.chance(55)==.02F&&BloodstoneFormation.chance(65)==.04F,"Formation chance doubles by tier");
  var chunk=level.getChunkAt(h.absolutePos(new BlockPos(2,2,2)));var ore=new BlockPos(chunk.getPos().getMinBlockX()+1,level.getMinBuildHeight()+2,chunk.getPos().getMinBlockZ()+1);level.setBlockAndUpdate(ore,Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState());h.assertTrue(BloodstoneFormation.convert(level,chunk,net.minecraft.util.RandomSource.create(1))&&level.getBlockState(ore).is(CatalogBlocks.BLOODSTONE_ORE),"Loaded section diamond conversion forms Bloodstone");
  try(var biomeArea=BiomeTestArea.force(level,h.absolutePos(new BlockPos(2,3,2)),ModBiomes.GRAVEYARD)){
  var stone=h.absolutePos(new BlockPos(2,3,2));level.setBlockAndUpdate(stone,Blocks.STONE.defaultBlockState());HauntManager.setValue(level,chunk.getPos(),35);
  h.assertTrue(GraveyardStoneWeathering.convert(level,stone)&&level.getBlockState(stone).is(CatalogBlocks.GRAVESTONE)&&HauntManager.getValue(level,chunk.getPos())==35,"Actual Graveyard stone weathering preserves Haunt");}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="terrain_readers")
 public void cemetery_piece_places_native_natural_graves_with_registered_properties(GameTestHelper h){
  var level=h.getLevel();var origin=h.absolutePos(new BlockPos(128,2,128));var chunk=level.getChunkAt(origin);var cp=chunk.getPos();
  var piece=new com.howierux.mortalexpansion.worldgen.structure.GraveyardCemeteryPiece(cp.getMinBlockX(),cp.getMinBlockZ());
  var bounds=new net.minecraft.world.level.levelgen.structure.BoundingBox(cp.getMinBlockX(),level.getMinBuildHeight(),cp.getMinBlockZ(),cp.getMaxBlockX(),level.getMaxBuildHeight()-1,cp.getMaxBlockZ());
  piece.postProcess(level,level.structureManager(),level.getChunkSource().getGenerator(),net.minecraft.util.RandomSource.create(7),bounds,cp,origin);
  int graves=0;for(var pos:BlockPos.betweenClosed(new BlockPos(cp.getMinBlockX(),-63,cp.getMinBlockZ()),new BlockPos(cp.getMaxBlockX(),-40,cp.getMaxBlockZ()))){var state=level.getBlockState(pos);if(state.is(CatalogBlocks.GRAVE)){h.assertTrue(state.getValue(CatalogBlocks.GRAVE_NATURAL),"Generated grave persists natural state");graves++;}}
  h.assertTrue(graves>0,"Real cemetery piece places graves without invalid state properties");
  var context=net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext.fromLevel(level);var loaded=TerrainRuntime.CEMETERY_PIECE.load(context,piece.createTag(context));
  h.assertTrue(loaded instanceof com.howierux.mortalexpansion.worldgen.structure.GraveyardCemeteryPiece&&loaded.getBoundingBox().minX()==cp.getMinBlockX(),"Native cemetery piece serializer preserves bounds");h.succeed();
 }
}
