package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.FenceBlock;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID) @PrefixGameTestTemplate(false)
public final class CemeteryFenceGameTests {
 @GameTest(template="deferred_items_test",batch="cemetery_fence") public static void stone_fence_connections_survive_neighbor_updates(GameTestHelper h){
  var level=h.getLevel();var pos=h.absolutePos(new BlockPos(4,3,4));var fence=ModBlocks.STONE_FENCE.get();h.assertTrue(fence.defaultBlockState().is(net.minecraft.tags.BlockTags.FENCES),"Stone fence participates in native fence connectivity");level.setBlockAndUpdate(pos,fence.defaultBlockState());level.setBlockAndUpdate(pos.east(),fence.defaultBlockState());h.assertTrue(level.getBlockState(pos).getValue(FenceBlock.EAST),"Neighboring stone fence creates an arm");level.removeBlock(pos.east(),false);h.assertFalse(level.getBlockState(pos).getValue(FenceBlock.EAST),"Removing neighbor clears the arm");h.succeed();
 }
}
