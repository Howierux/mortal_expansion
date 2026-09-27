package com.howierux.mortalexpansion.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Upright half-block grave marker.
 *
 * DEPTH controls where the vertical slab sits along its thickness axis:
 * 0 = low/north/west edge, 1 = centered, 2 = high/south/east edge.
 */
public final class GraveBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<GraveBlock> CODEC = simpleCodec(GraveBlock::new);
    public static final IntegerProperty DEPTH = IntegerProperty.create("depth", 0, 2);

    private static final VoxelShape[] NORTH_SOUTH_SHAPES = {
            box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D),
            box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D),
            box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D)
    };

    private static final VoxelShape[] EAST_WEST_SHAPES = {
            box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D),
            box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public GraveBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(DEPTH, 1)
        );
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        double local = facing.getAxis() == Direction.Axis.Z
                ? context.getClickLocation().z - pos.getZ()
                : context.getClickLocation().x - pos.getX();

        int depth = local < (1.0D / 3.0D)
                ? 0
                : local < (2.0D / 3.0D) ? 1 : 2;

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(DEPTH, depth);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        int depth = state.getValue(DEPTH);
        return state.getValue(FACING).getAxis() == Direction.Axis.Z
                ? NORTH_SOUTH_SHAPES[depth]
                : EAST_WEST_SHAPES[depth];
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder
    ) {
        builder.add(FACING, DEPTH);
    }
}
