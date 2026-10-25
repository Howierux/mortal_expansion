package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import java.util.Map;

public final class HauntFoundationGameTests implements FabricGameTest {
 private static ServerPlayer player(GameTestHelper h){
  var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"fabric-haunt"),false);
  var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());
  var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(connection);
  h.getLevel().getServer().getPlayerList().placeNewPlayer(connection,p,cookie);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
  p.setPos(h.absolutePos(new BlockPos(3,2,3)).getCenter());return p;
 }
 private static void remove(ServerPlayer p){p.getServer().getPlayerList().remove(p);p.discard();}
 @GameTest(template=EMPTY_STRUCTURE)
 public void personal_haunt_saves_fraction_and_copies_on_real_respawn(GameTestHelper h){
  var p=player(h);ServerPlayer next=null;
  try{
   h.assertTrue(PlayerHauntManager.getHundredths(p)==1500,"New players start at Natural 15.00");
   PlayerHauntManager.addHundredths(p,1);PlayerHauntManager.addPoints(p,1);
   var saved=p.saveWithoutId(new CompoundTag());PlayerHauntManager.setValue(p,0);p.load(saved);
   h.assertTrue(PlayerHauntManager.getHundredths(p)==1601,"Native player serialization preserves fractional Haunt");
   next=h.getLevel().getServer().getPlayerList().respawn(p,false,net.minecraft.world.entity.Entity.RemovalReason.KILLED);
   h.assertTrue(PlayerHauntManager.getHundredths(next)==1601,"Fabric attachment copyOnDeath survives native player replacement");
   PlayerHauntManager.addHundredths(next,Long.MAX_VALUE);h.assertTrue(PlayerHauntManager.getHundredths(next)==6900,"Large additions clamp before overflow");
   PlayerHauntManager.addHundredths(next,Long.MIN_VALUE);h.assertTrue(PlayerHauntManager.getHundredths(next)==0,"Large reductions clamp at zero");
  }finally{remove(next==null?p:next);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE)
 public void chunk_baselines_and_native_chunk_serialization(GameTestHelper h){
  var level=h.getLevel();var pos=new ChunkPos(h.absolutePos(new BlockPos(3,2,3)));var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);
  int before=HauntManager.getHundredths(level,pos);
  try{
   h.assertTrue(NaturalHauntGenerator.determineInitialTier(level,chunk)==HauntTier.NATURAL,"Overworld vanilla biomes retain Natural baseline");
   var nether=level.getServer().getLevel(net.minecraft.world.level.Level.NETHER);var end=level.getServer().getLevel(net.minecraft.world.level.Level.END);
   h.assertTrue(NaturalHauntGenerator.determineInitialTier(nether,chunk)==HauntTier.DEMONIC && NaturalHauntGenerator.determineInitialTier(end,chunk)==HauntTier.ELDRITCH,"Nether and End dimension baselines retained");
   chunk.setUnsaved(false);HauntManager.setValue(level,pos,33);HauntManager.addHundredths(level,pos,27);
   h.assertTrue(chunk.isUnsaved(),"Attachment updates mark clean chunks for normal disk saving");
   var tag=net.minecraft.world.level.chunk.storage.ChunkSerializer.write(level,chunk);
   var restored=net.minecraft.world.level.chunk.storage.ChunkSerializer.read(level,level.getPoiManager(),new net.minecraft.world.level.chunk.storage.RegionStorageInfo("gametest",level.dimension(),"chunk"),pos,tag);
   h.assertTrue(((AttachmentTarget)restored).getAttachedOrThrow(FabricHauntData.CHUNK)==3327,"Native chunk NBT saves and restores Fabric attachment data");
   var absent=new ChunkPos(pos.x+100000,pos.z+100000);boolean rejected=false;
   try{HauntManager.getValue(level,absent);}catch(IllegalArgumentException expected){rejected=true;}
   h.assertTrue(rejected && level.getChunkSource().getChunkNow(absent.x,absent.z)==null,"Read API never loads distant chunks");
  }finally{HauntManager.setValue(level,pos,0);HauntManager.addHundredths(level,pos,before);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE)
 public void kill_event_leak_and_commands_use_fabric_boundaries(GameTestHelper h) throws Exception {
  var p=player(h);var level=h.getLevel();var at=p.chunkPosition();int before=HauntManager.getHundredths(level,at);
  var cow=net.minecraft.world.entity.EntityType.COW.create(level);var zombie=net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
  try{
   cow.setPos(p.position());zombie.setPos(p.position());cow.setNoAi(true);zombie.setNoAi(true);level.addFreshEntity(cow);level.addFreshEntity(zombie);
   cow.hurt(level.damageSources().playerAttack(p),100);zombie.hurt(level.damageSources().playerAttack(p),100);
   h.assertTrue(PlayerHauntManager.getHundredths(p)==1501,"Eligible kill gains .01; Smite-sensitive undead do not");
   PlayerHauntManager.setValue(p,35);PlayerHauntManager.addHundredths(p,7);HauntManager.setValue(level,at,15);
   h.assertTrue(HauntSimulation.leak(level,p,0) && PlayerHauntManager.getHundredths(p)==3407 && HauntManager.getValue(level,at)==16,"Leak transfers exactly one point and retains personal fraction");
   HauntManager.setValue(level,at,40);h.assertFalse(HauntSimulation.leak(level,p,0),"Haunt never leaks into an equally or more haunted chunk");
   var source=p.createCommandSourceStack().withPermission(2);var dispatcher=level.getServer().getCommands().getDispatcher();
   dispatcher.execute("mortalexpansion haunt player add 2",source);h.assertTrue(PlayerHauntManager.getHundredths(p)==3607,"Registered command updates actual player data");
   dispatcher.execute("mortalexpansion haunt set void",source);h.assertTrue(HauntManager.getValue(level,at)==0,"Chunk command is separate from personal Haunt");
   boolean denied=false;try{dispatcher.execute("mortalexpansion haunt player add 2",source.withPermission(0));}catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected){denied=true;}
   h.assertTrue(denied,"Nonoperators cannot invoke modification commands");
  }finally{cow.discard();zombie.discard();HauntManager.setValue(level,at,0);HauntManager.addHundredths(level,at,before);remove(p);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE)
 public void snapshot_spread_retains_moon_weights_and_never_chains(GameTestHelper h){
  for(int value:new int[]{1,15,25,35,45,55,65})h.assertTrue(HauntSimulation.spreadChance(value,4)==1,"Every nonzero band spreads at 100 percent on new moon");
  h.assertTrue(HauntSimulation.spreadChance(0,4)==0 && HauntSimulation.spreadChance(65,0)<HauntSimulation.spreadChance(65,3),"Void zero never spreads; full moon is weakest");
  h.assertTrue(HauntSimulation.spreadCheckDue(7999,8000) && !HauntSimulation.spreadCheckDue(8000,8000) && !HauntSimulation.spreadCheckDue(8000,7999),"Forward crossings only, no duplicate/reverse checks");
  long source=ChunkPos.asLong(0,0);var start=new java.util.HashMap<Long,Integer>();start.put(source,20);
  for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)if(x!=0 || z!=0)start.put(ChunkPos.asLong(x,z),0);
  var plan=HauntSimulation.plan(start,Map.of(),true,false,4,net.minecraft.util.RandomSource.create(42));
  h.assertTrue(plan.values().stream().mapToInt(Integer::intValue).sum()==21 && plan.get(source)==20,"Only original source spreads; new points cannot propagate within the same pass");
  var full=HauntSimulation.plan(Map.of(source,69),Map.of(),true,false,4,net.minecraft.util.RandomSource.create(42));
  h.assertTrue(full.size()==1 && full.get(source)==69,"Absent neighboring chunks are neither inserted nor loaded");
  var decay=new net.minecraft.world.level.levelgen.LegacyRandomSource(0){@Override public float nextFloat(){return 0;}};
  h.assertTrue(HauntSimulation.plan(Map.of(source,30),Map.of(source,15),false,true,0,decay).get(source)==29
      && HauntSimulation.plan(Map.of(source,1),Map.of(source,15),false,true,0,decay).get(source)==2
      && HauntSimulation.plan(Map.of(source,15),Map.of(source,15),false,true,0,decay).get(source)==15,"Daily recovery moves one point toward baseline without overshooting");
  h.succeed();
 }
}
