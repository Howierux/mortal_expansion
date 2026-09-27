package com.howierux.mortalexpansion.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A simple upright grave marker. It occupies half of a block's horizontal
 * depth, like a vertical slab, and rotates to face the player when placed.
 */
public final class GraveBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<GraveBlock> CODEC = simpleCodec(GraveBlock::new);

    private static final VoxelShape NORTH_SOUTH_SHAPE =
            box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private static final VoxelShape EAST_WEST_SHAPE =
            box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);

    public GraveBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z
                ? NORTH_SOUTH_SHAPE
                : EAST_WEST_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder
    ) {
        builder.add(FACING);
    }
}
