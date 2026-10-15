package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EctoInfrastructureGameTests {
    @GameTest(template="deferred_items_test")
    public static void placed_pipe_connects_to_tanks_and_disconnects_after_removal(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));var level=h.getLevel();var player=h.makeMockServerPlayerInLevel();player.setPos(pos.getCenter().add(8,0,0));
        for(var direction:Direction.values())level.setBlock(pos.relative(direction),ModBlocks.ECTANK.get().defaultBlockState(),3);
        var stack=new ItemStack(ModItems.ECTO_PIPE.get());player.setItemInHand(InteractionHand.MAIN_HAND,stack);
        var context=new BlockPlaceContext(level,player,InteractionHand.MAIN_HAND,stack,
                new BlockHitResult(pos.below().getCenter().add(0,0.5,0),Direction.UP,pos.below(),false));
        h.assertTrue(ModItems.ECTO_PIPE.get().place(context).consumesAction(),"Pipe places through the native item path");
        for(var direction:Direction.values())h.assertTrue(level.getBlockState(pos).getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction)),"Placed pipe connects to every adjacent tank");
        level.removeBlock(pos.north(),false);h.assertFalse(level.getBlockState(pos).getValue(PipeBlock.NORTH),"Removing a neighbor removes the visible connection");
        level.setBlock(pos.north(),ModBlocks.PURIFIED_ECTANK.get().defaultBlockState(),3);h.assertTrue(level.getBlockState(pos).getValue(PipeBlock.NORTH),"Purified tank is also a visual endpoint");
        level.setBlock(pos.east(),ModBlocks.ECTO_PIPE.get().defaultBlockState(),3);
        h.assertTrue(level.getBlockState(pos).getValue(PipeBlock.EAST),"Pipes connect to other pipes");
        h.assertTrue(level.getBlockEntity(pos)==null && level.getBlockEntity(pos.north())==null,"Decorative blocks have no processing block entities");
        player.discard();h.succeed();
    }
}
