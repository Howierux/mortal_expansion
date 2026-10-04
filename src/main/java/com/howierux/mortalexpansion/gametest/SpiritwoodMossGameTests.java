package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritwoodMossGameTests {
    @GameTest(template = "deferred_items_test")
    public static void sapling_tree_grows_moss_on_its_new_leaves(GameTestHelper helper) {
        var level = helper.getLevel();
        var origin = helper.absolutePos(new BlockPos(7, 2, 7));
        for (var pos : BlockPos.betweenClosed(origin.offset(-5, 0, -5), origin.offset(5, 11, 5))) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(origin.below(), Blocks.DIRT.defaultBlockState(), 3);
        var sapling = ModBlocks.SPIRITWOOD_SAPLING.get();
        var state = sapling.defaultBlockState();
        level.setBlock(origin, state, 3);
        sapling.advanceTree(level, origin, state, RandomSource.create(7));
        helper.assertTrue(level.getBlockState(origin).getValue(SaplingBlock.STAGE) == 1, "First growth only advances stage");
        for (int attempt = 0; attempt < 8 && level.getBlockState(origin).is(sapling); attempt++)
            sapling.performBonemeal(level, RandomSource.create(attempt + 7), origin, level.getBlockState(origin));
        helper.assertTrue(level.getBlockState(origin).is(ModBlocks.SPIRITWOOD_LOG.get()), "Actual sapling grows a tree");
        int mossCount = 0;
        for (var at : BlockPos.betweenClosed(origin.offset(-5, 0, -5), origin.offset(5, 11, 5))) {
            var moss = level.getBlockState(at);
            if (!moss.is(ModBlocks.GRAVE_MOSS.get())) continue;
            mossCount++;
            helper.assertTrue(moss.canSurvive(level, at), "Moss attached to leaves survives");
            boolean supported = false;
            for (var dir : Direction.values()) if (moss.getValue(MultifaceBlock.getFaceProperty(dir)))
                supported |= level.getBlockState(at.relative(dir)).is(ModBlocks.SPIRITWOOD_LEAVES.get());
            helper.assertTrue(supported, "Moss grows on Spiritwood leaves");
        }
        helper.assertTrue(mossCount > 0, "Sapling-grown canopy gets Grave Moss");
        helper.succeed();
    }
}
