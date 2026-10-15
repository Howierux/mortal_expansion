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
        h.assertTrue(level.getBlockEntity(pos) instanceof com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity
                && level.getBlockEntity(pos.north()) instanceof com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity,"Pipes and tanks expose their fluid storage block entities");
        player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void clicking_connected_arm_changes_its_mode_not_its_surface_face(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));var level=h.getLevel();var player=h.makeMockServerPlayerInLevel();
        for(var block:new Block[]{ModBlocks.ECTO_PIPE.get(),ModBlocks.PURIFIED_ECTO_PIPE.get()}){
            for(var arm:Direction.values()){
                level.removeBlock(pos,false);
                for(var direction:Direction.values())level.setBlock(pos.relative(direction),ModBlocks.ECTANK.get().defaultBlockState(),3);
                var state=block.defaultBlockState();for(var direction:Direction.values())state=state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction),true);
                level.setBlock(pos,state,3);
                var pipe=(com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity)level.getBlockEntity(pos);
                var surface=arm.getAxis()==Direction.Axis.Y?Direction.EAST:Direction.UP;
                var location=pos.getCenter().add(arm.getStepX()*0.4+surface.getStepX()*0.1875,
                        arm.getStepY()*0.4+surface.getStepY()*0.1875,arm.getStepZ()*0.4+surface.getStepZ()*0.1875);
                var hit=new BlockHitResult(location,surface,pos,false);
                for(int mode=1;mode<=3;mode++){
                    state.useWithoutItem(level,player,hit);
                    h.assertTrue(pipe.mode(arm)==mode,"Clicking the arm selects its connected direction for each mode");
                    h.assertTrue(pipe.mode(surface)==0,"The arm's visible surface face remains unchanged");
                }
                var detached=com.howierux.mortalexpansion.block.EctoPipeBlock.connections(level,pos,state);
                level.setBlock(pos,detached,2);
                h.assertFalse(detached.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(arm)),"Disabled arm retracts");
                var centerHit=new BlockHitResult(pos.getCenter().add(arm.getStepX()*0.1875,arm.getStepY()*0.1875,arm.getStepZ()*0.1875),arm,pos,false);
                detached.useWithoutItem(level,player,centerHit);
                h.assertTrue(pipe.mode(arm)==0,"Core face can re-enable a retracted arm");
            }
        }
        player.discard();h.succeed();
    }
}
