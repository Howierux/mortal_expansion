package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Upright half-block grave marker.
 *
 * DEPTH controls where the vertical slab sits along its thickness axis:
 * 0 = low/north/west edge, 1 = centered, 2 = high/south/east edge.
 *
 * Each Grave also stores persistent provenance in a block entity:
 * naturally generated Graves can raise Haunt when broken, while Graves placed
 * by real players or fake players do not.
 */
public final class GraveBlock extends BaseEntityBlock {
    public static final MapCodec<GraveBlock> CODEC = simpleCodec(GraveBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty DEPTH = IntegerProperty.create("depth", 0, 2);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

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
                        .setValue(NATURAL, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide
                ? null
                : createTickerHelper(
                        type,
                        com.howierux.mortalexpansion.registry.ModBlockEntities.GRAVE.get(),
                        GraveBlockEntity::serverTick
                );
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
    public BlockState playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        if (!level.isClientSide
                && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof GraveBlockEntity grave
                && grave.isNaturallyGenerated()) {
            if (player instanceof FakePlayer) {
                HauntManager.addPoints(serverLevel, pos, 1);
            } else {
                PlayerHauntManager.addPoints(player, 1);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
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
        builder.add(FACING, DEPTH, NATURAL);
    }
}
