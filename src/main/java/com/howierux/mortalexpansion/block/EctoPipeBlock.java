package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;

/** Six-way fluid pipe with sided controls; network work is coordinated once per component. */
public final class EctoPipeBlock extends PipeBlock implements net.minecraft.world.level.block.EntityBlock {
    public static final MapCodec<EctoPipeBlock> CODEC=simpleCodec(EctoPipeBlock::new);
    public EctoPipeBlock(Properties properties){super(0.1875F,properties);
        var state=stateDefinition.any();for(var property:PROPERTY_BY_DIRECTION.values())state=state.setValue(property,false);registerDefaultState(state);
    }
    @Override protected MapCodec<? extends PipeBlock> codec(){return CODEC;}
    private static boolean connects(BlockState state){return state.getBlock() instanceof EctoPipeBlock
            || state.is(ModBlocks.ECTANK.get()) || state.is(ModBlocks.PURIFIED_ECTANK.get());}
    public static BlockState connections(Level level,BlockPos pos,BlockState state){
        var own=level.getBlockEntity(pos);
        for(var side:Direction.values()){
            var at=pos.relative(side);boolean connected=false;
            if(level instanceof net.minecraft.server.level.ServerLevel server && server.getChunkSource().getChunkNow(at.getX()>>4,at.getZ()>>4)!=null){
                connected=level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,at,side.getOpposite())!=null;
                if(own instanceof com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity pipe && pipe.mode(side)==3)connected=false;
                if(level.getBlockEntity(at) instanceof com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity other && other.isPipe() && other.mode(side.getOpposite())==3)connected=false;
            }
            state=state.setValue(PROPERTY_BY_DIRECTION.get(side),connected);
        }
        return state;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var state=defaultBlockState();for(var direction:Direction.values())state=state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                connects(context.getLevel().getBlockState(context.getClickedPos().relative(direction))));return state;
    }
    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos){
        if(level.getBlockEntity(pos) instanceof com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity pipe)
            com.howierux.mortalexpansion.fluid.EctoFluidNetworks.invalidate(pipe);
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction),connects(neighbor));
    }
    @Override public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity(pos,state);}
    @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit){
        return EctankBlock.interact(stack,state,level,pos,player,hand,hit);
    }
    private static Direction selectedSide(BlockState state,BlockPos pos,net.minecraft.world.phys.BlockHitResult hit){
        // An arm's side surface faces the viewer, not the tank it connects to.
        // Resolve the arm from its position outside the central 6/16 cube instead.
        var offset=hit.getLocation().subtract(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
        for(var side:Direction.values())
            if(state.getValue(PROPERTY_BY_DIRECTION.get(side))
                    && offset.x*side.getStepX()+offset.y*side.getStepY()+offset.z*side.getStepZ()>0.1875+1.0E-6)
                return side;
        return hit.getDirection();
    }
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity pipe){
            var side=selectedSide(state,pos,hit);pipe.cycle(side);player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.mortal_expansion.ecto_pipe.mode",net.minecraft.network.chat.Component.translatable("message.mortal_expansion.ecto_pipe.mode."+pipe.mode(side))),true);
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected java.util.List<net.minecraft.world.item.ItemStack> getDrops(BlockState state,net.minecraft.world.level.storage.loot.LootParams.Builder builder){return EctankBlock.portable(super.getDrops(state,builder),builder);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(NORTH,EAST,SOUTH,WEST,UP,DOWN);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){
        var result=state;for(var direction:Direction.values())result=result.setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(direction)),state.getValue(PROPERTY_BY_DIRECTION.get(direction)));return result;
    }
    @Override protected BlockState mirror(BlockState state,Mirror mirror){
        var result=state;for(var direction:Direction.values())result=result.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(direction)),state.getValue(PROPERTY_BY_DIRECTION.get(direction)));return result;
    }
}
