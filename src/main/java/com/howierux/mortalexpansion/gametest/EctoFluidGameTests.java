package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity;
import com.howierux.mortalexpansion.fluid.EctoFluidNetworks;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EctoFluidGameTests {
    private static EctoFluidBlockEntity place(GameTestHelper h,int x,int z,Block block){
        var pos=h.absolutePos(new BlockPos(x,3,z));h.getLevel().setBlock(pos,block.defaultBlockState(),3);
        var be=(EctoFluidBlockEntity)h.getLevel().getBlockEntity(pos);be.onLoad();return be;
    }
    @GameTest(template="deferred_items_test")
    public static void capacities_simulation_and_saved_fluid(GameTestHelper h){
        var blocks=new Block[]{ModBlocks.ECTANK.get(),ModBlocks.PURIFIED_ECTANK.get(),ModBlocks.ECTO_PIPE.get(),ModBlocks.PURIFIED_ECTO_PIPE.get()};
        int[] capacities={16000,64000,2000,8000};
        for(int i=0;i<blocks.length;i++){
            var be=place(h,2+i,2,blocks[i]);h.assertTrue(be.tank.getCapacity()==capacities[i],"Correct storage capacity");
            var handler=h.getLevel().getCapability(Capabilities.FluidHandler.BLOCK,be.getBlockPos(),Direction.UP);
            h.assertTrue(handler!=null,"Native fluid capability is registered");
            int expected=i<2?capacities[i]:(i==2?500:2000);
            h.assertTrue(handler.fill(new FluidStack(Fluids.WATER,100000),FluidAction.SIMULATE)==expected && be.tank.isEmpty(),"Simulation respects capacity and rate without consuming either");
            h.assertTrue(handler.fill(new FluidStack(Fluids.WATER,100000),FluidAction.EXECUTE)==expected,"Execution accepts the simulated amount");
            h.assertTrue(handler.fill(new FluidStack(Fluids.LAVA,100),FluidAction.EXECUTE)==0,"Different fluids cannot mix");
            if(be.isPipe())be.cycle(Direction.NORTH);
            var saved=be.saveWithoutMetadata(h.getLevel().registryAccess());var restored=new EctoFluidBlockEntity(be.getBlockPos(),be.getBlockState());
            restored.loadAdditional(saved,h.getLevel().registryAccess());
            h.assertTrue(restored.tank.getFluidAmount()==expected && restored.mode(Direction.NORTH)==be.mode(Direction.NORTH),"Fluid and face settings survive saving");
        }h.succeed();
    }
    private static void rate(GameTestHelper h,boolean purified){
        var source=place(h,2,5,ModBlocks.PURIFIED_ECTANK.get());var pipe=place(h,3,5,purified?ModBlocks.PURIFIED_ECTO_PIPE.get():ModBlocks.ECTO_PIPE.get());
        var sink=place(h,4,5,ModBlocks.PURIFIED_ECTANK.get());var branch=place(h,3,6,ModBlocks.PURIFIED_ECTANK.get());
        source.tank.fill(new FluidStack(Fluids.WATER,16000),FluidAction.EXECUTE);pipe.cycle(Direction.WEST);
        EctoFluidNetworks.tick(h.getLevel());int expected=purified?2000:500;
        h.assertTrue(sink.tank.getFluidAmount()+branch.tank.getFluidAmount()==expected,"Branches share the exact pipe transfer rate");
        EctoFluidNetworks.tick(h.getLevel());
        h.assertTrue(sink.tank.getFluidAmount()+branch.tank.getFluidAmount()==expected,"Repeated work in one tick cannot multiply throughput");
        h.assertTrue(source.tank.getFluidAmount()+pipe.tank.getFluidAmount()+sink.tank.getFluidAmount()+branch.tank.getFluidAmount()==16000,"Transfer conserves fluid");
        h.succeed();
    }
    @GameTest(template="deferred_items_test") public static void basic_pipe_shared_rate_is_500(GameTestHelper h){rate(h,false);}
    @GameTest(template="deferred_items_test") public static void purified_pipe_shared_rate_is_2000(GameTestHelper h){rate(h,true);}
    @GameTest(template="deferred_items_test")
    public static void mixed_tiers_respect_bottleneck(GameTestHelper h){
        var source=place(h,2,5,ModBlocks.PURIFIED_ECTANK.get());var fast=place(h,3,5,ModBlocks.PURIFIED_ECTO_PIPE.get());
        var slow=place(h,4,5,ModBlocks.ECTO_PIPE.get());var sink=place(h,5,5,ModBlocks.PURIFIED_ECTANK.get());
        source.tank.fill(new FluidStack(Fluids.WATER,16000),FluidAction.EXECUTE);fast.cycle(Direction.WEST);
        EctoFluidNetworks.tick(h.getLevel());h.assertTrue(sink.tank.getFluidAmount()==500,"The basic pipe limits a mixed route to 500 mB per tick");
        h.assertTrue(source.tank.getFluidAmount()+fast.tank.getFluidAmount()+slow.tank.getFluidAmount()+sink.tank.getFluidAmount()==16000,"Buffer balancing conserves fluid");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void blocked_output_and_disabled_faces_keep_fluid(GameTestHelper h){
        var pipe=place(h,3,5,ModBlocks.ECTO_PIPE.get());var sink=place(h,4,5,ModBlocks.ECTANK.get());
        pipe.tank.fill(new FluidStack(Fluids.WATER,2000),FluidAction.EXECUTE);sink.tank.fill(new FluidStack(Fluids.LAVA,16000),FluidAction.EXECUTE);
        EctoFluidNetworks.tick(h.getLevel());h.assertTrue(pipe.tank.getFluidAmount()==2000 && sink.tank.getFluidAmount()==16000,"Incompatible full output loses no fluid");
        pipe.cycle(Direction.EAST);pipe.cycle(Direction.EAST);pipe.cycle(Direction.EAST);sink.tank.drain(16000,FluidAction.EXECUTE);
        EctoFluidNetworks.tick(h.getLevel());h.assertTrue(pipe.tank.getFluidAmount()==2000 && sink.tank.isEmpty(),"Disabled face prevents transfer");
        h.assertTrue(pipe.handler(Direction.EAST).drain(500,FluidAction.EXECUTE).isEmpty() && pipe.handler(Direction.EAST).fill(new FluidStack(Fluids.WATER,500),FluidAction.EXECUTE)==0,"Disabled capability blocks automation in both directions");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void bucket_and_mined_block_retain_fluid(GameTestHelper h){
        var be=place(h,3,5,ModBlocks.ECTANK.get());var player=h.makeMockServerPlayerInLevel();player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WATER_BUCKET));
        var hit=new net.minecraft.world.phys.BlockHitResult(be.getBlockPos().getCenter(),Direction.UP,be.getBlockPos(),false);
        be.getBlockState().useItemOn(player.getMainHandItem(),h.getLevel(),player,net.minecraft.world.InteractionHand.MAIN_HAND,hit);
        h.assertTrue(be.tank.getFluidAmount()==1000 && player.getMainHandItem().is(net.minecraft.world.item.Items.BUCKET),"Bucket fills the tank and returns an empty bucket");
        var drops=Block.getDrops(be.getBlockState(),h.getLevel(),be.getBlockPos(),be,player,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
        h.assertTrue(drops.size()==1,"Mining returns one portable tank");
        var stack=drops.getFirst();var target=be.getBlockPos();h.getLevel().removeBlock(target,false);h.getLevel().setBlock(target.below(),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),3);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);
        var context=new net.minecraft.world.item.context.BlockPlaceContext(h.getLevel(),player,net.minecraft.world.InteractionHand.MAIN_HAND,stack,
            new net.minecraft.world.phys.BlockHitResult(target.below().getCenter().add(0,0.5,0),Direction.UP,target.below(),false));
        h.assertTrue(((net.minecraft.world.item.BlockItem)stack.getItem()).place(context).consumesAction(),"Filled tank can be placed again");
        var restored=(EctoFluidBlockEntity)h.getLevel().getBlockEntity(target);
        h.assertTrue(restored.tank.getFluidAmount()==1000 && restored.tank.getFluid().getFluid()==Fluids.WATER,"Native block item restores its saved fluid");
        player.discard();h.succeed();
    }
}
