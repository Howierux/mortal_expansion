package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.MortalGolemBaseBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;

public final class MortalGolemBaseBlock extends BaseEntityBlock {
    public static final MapCodec<MortalGolemBaseBlock> CODEC=simpleCodec(MortalGolemBaseBlock::new);
    public static final IntegerProperty FLESH_STAGE=IntegerProperty.create("flesh_stage",0,4);
    public static final EnumProperty<Direction.Axis> AXIS=BlockStateProperties.AXIS;
    public MortalGolemBaseBlock(BlockBehaviour.Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(FLESH_STAGE,0).setValue(AXIS,Direction.Axis.Y));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FLESH_STAGE,AXIS);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(AXIS,context.getClickedFace().getAxis());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MortalGolemBaseBlockEntity(pos,state);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){
        if(rotation==Rotation.CLOCKWISE_90 || rotation==Rotation.COUNTERCLOCKWISE_90)
            return state.setValue(AXIS,state.getValue(AXIS)==Direction.Axis.X?Direction.Axis.Z:state.getValue(AXIS)==Direction.Axis.Z?Direction.Axis.X:Direction.Axis.Y);
        return state;
    }
}
