package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.*;
import net.minecraft.core.*;
import net.minecraft.world.level.block.*;
public final class CemeteryRegressionGameTests implements FabricGameTest {
 @GameTest(template=EMPTY_STRUCTURE,batch="cemetery") public void grave_breaking_rewards_real_and_automated_players_only_after_success(GameTestHelper h){
  var p=RemainingSlicesGameTests.player(h);var level=h.getLevel();var at=h.absolutePos(new BlockPos(4,3,4));
  try{PlayerHauntManager.setValue(p,20);HauntManager.setValue(level,at,20);
   for(var mode:java.util.List.of(net.minecraft.world.level.GameType.SURVIVAL,net.minecraft.world.level.GameType.CREATIVE)){
    p.setGameMode(mode);level.setBlockAndUpdate(at,CatalogBlocks.GRAVE.defaultBlockState().setValue(CatalogBlocks.GRAVE_NATURAL,true));int before=PlayerHauntManager.getHundredths(p);h.assertTrue(p.gameMode.destroyBlock(at),"Native player destruction succeeds");h.assertTrue(PlayerHauntManager.getHundredths(p)==before+100,"Natural grave gives exactly one personal Haunt in "+mode);
   }
   level.setBlockAndUpdate(at,CatalogBlocks.GRAVE.defaultBlockState());int before=PlayerHauntManager.getHundredths(p);p.gameMode.destroyBlock(at);h.assertTrue(PlayerHauntManager.getHundredths(p)==before,"Placed grave gives no breaking reward");
   level.setBlockAndUpdate(at,CatalogBlocks.GRAVE.defaultBlockState().setValue(CatalogBlocks.GRAVE_NATURAL,true));RemainingSlicesGameTests.DENIED.add(at);try{h.assertTrue(!p.gameMode.destroyBlock(at),"Claim cancels destruction");h.assertTrue(PlayerHauntManager.getHundredths(p)==before,"Cancelled destruction gives no Haunt");}finally{RemainingSlicesGameTests.DENIED.remove(at);}
   var fake=net.fabricmc.fabric.api.entity.FakePlayer.get(level);fake.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);int local=HauntManager.getHundredths(level,at),personal=PlayerHauntManager.getHundredths(fake);h.assertTrue(fake.gameMode.destroyBlock(at),"Native Fabric automation destroys grave");h.assertTrue(HauntManager.getHundredths(level,at)==local+100&&PlayerHauntManager.getHundredths(fake)==personal,"Automation raises chunk Haunt, not fake-player Haunt");h.succeed();
  }finally{RemainingSlicesGameTests.DENIED.remove(at);RemainingSlicesGameTests.remove(h,p);}
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="cemetery") public void stone_fence_connects_after_neighbor_updates(GameTestHelper h){
  var l=h.getLevel();var at=h.absolutePos(new BlockPos(4,3,4));var fence=CatalogBlocks.STONE_FENCE;
  h.assertTrue(fence.defaultBlockState().is(net.minecraft.tags.BlockTags.FENCES),"Stone fence has the native connection tag");l.setBlockAndUpdate(at,fence.defaultBlockState());l.setBlockAndUpdate(at.east(),fence.defaultBlockState());h.assertTrue(l.getBlockState(at).getValue(FenceBlock.EAST),"New neighboring stone fence connects existing post");l.removeBlock(at.east(),false);h.assertTrue(!l.getBlockState(at).getValue(FenceBlock.EAST),"Removed neighbor clears arm");h.succeed();
 }
}
