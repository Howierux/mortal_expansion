package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;

/** Visual six-way pipe connections only; deliberately no fluid handler or ticker. */
public final class EctoPipeBlock extends PipeBlock {
    public static final MapCodec<EctoPipeBlock> CODEC=simpleCodec(EctoPipeBlock::new);
    public EctoPipeBlock(Properties properties){super(0.1875F,properties);
        var state=stateDefinition.any();for(var property:PROPERTY_BY_DIRECTION.values())state=state.setValue(property,false);registerDefaultState(state);
    }
    @Override protected MapCodec<? extends PipeBlock> codec(){return CODEC;}
    private static boolean connects(BlockState state){return state.getBlock() instanceof EctoPipeBlock
            || state.is(ModBlocks.ECTANK.get()) || state.is(ModBlocks.PURIFIED_ECTANK.get());}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var state=defaultBlockState();for(var direction:Direction.values())state=state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                connects(context.getLevel().getBlockState(context.getClickedPos().relative(direction))));return state;
    }
    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos){
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction),connects(neighbor));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(NORTH,EAST,SOUTH,WEST,UP,DOWN);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){
        var result=state;for(var direction:Direction.values())result=result.setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(direction)),state.getValue(PROPERTY_BY_DIRECTION.get(direction)));return result;
    }
    @Override protected BlockState mirror(BlockState state,Mirror mirror){
        var result=state;for(var direction:Direction.values())result=result.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(direction)),state.getValue(PROPERTY_BY_DIRECTION.get(direction)));return result;
    }
}
