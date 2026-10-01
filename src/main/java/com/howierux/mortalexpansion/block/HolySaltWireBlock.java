package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.entity.mob.HolySaltBarrierTracker;
import com.howierux.mortalexpansion.sanctification.SanctifiedAreaHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;

/** A separate spirit circuit; never emits vanilla redstone power. */
public final class HolySaltWireBlock extends RedStoneWireBlock {
    public HolySaltWireBlock(Properties properties) { super(properties); }

    private static EnumProperty<RedstoneSide> side(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            default -> WEST;
        };
    }

    private BlockPos wireNeighbor(LevelAccessor level, BlockPos pos, Direction direction) {
        BlockPos beside = pos.relative(direction);
        if (!level.hasChunk(beside.getX() >> 4, beside.getZ() >> 4)) return null;
        BlockState neighbor = level.getBlockState(beside);
        if (neighbor.is(this)) return beside;
        if (neighbor.isRedstoneConductor(level, beside)) {
            if (!level.getBlockState(pos.above()).isRedstoneConductor(level, pos.above())
                    && level.getBlockState(beside.above()).is(this)) return beside.above();
        } else if (level.getBlockState(beside.below()).is(this)) return beside.below();
        return null;
    }

    private BlockState connections(LevelAccessor level, BlockPos pos, BlockState state) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos wire = wireNeighbor(level, pos, direction);
            BlockPos beside = pos.relative(direction);
            boolean torch = level.hasChunk(beside.getX() >> 4, beside.getZ() >> 4)
                    && SpiritSignals.isTorch(level.getBlockState(beside));
            state = state.setValue(side(direction), wire != null && wire.getY() > pos.getY()
                    ? RedstoneSide.UP : wire != null || torch ? RedstoneSide.SIDE : RedstoneSide.NONE);
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connections(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        return connections(level, pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.is(this)) return;
        HolySaltBarrierTracker.markPresent(level, pos);
        SanctifiedAreaHandler.addSalt(level, pos);
        if (!level.isClientSide) { level.scheduleTick(pos, this, 1); notifyCircuit(level, pos); }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moved) {
        if (next.is(this)) return;
        HolySaltBarrierTracker.invalidate(level, pos);
        SanctifiedAreaHandler.removeSalt(level, pos);
        if (!level.isClientSide) notifyCircuit(level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block source,
            BlockPos sourcePos, boolean moved) {
        if (level.isClientSide) return;
        if (!canSurvive(state, level, pos)) {
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        } else level.scheduleTick(pos, this, 1);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int power = SpiritSignals.input(level, pos);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = wireNeighbor(level, pos, direction);
            if (neighbor != null) power = Math.max(power, level.getBlockState(neighbor).getValue(POWER) - 1);
        }
        BlockState next = connections(level, pos, state).setValue(POWER, Math.max(0, power));
        if (!next.equals(state)) { level.setBlock(pos, next, 2); notifyCircuit(level, pos); }
    }

    private void notifyCircuit(Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        for (Direction direction : Direction.values()) {
            BlockPos next = pos.relative(direction);
            if (level.hasChunkAt(next)) level.updateNeighborsAt(next, this);
        }
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return 0; }
    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return 0; }
    @Override
    protected boolean isSignalSource(BlockState state) { return false; }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) { return InteractionResult.PASS; }
    @Override
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos,
            int flags, int recursion) {}
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWER) > 0 && random.nextInt(5) == 0)
            level.addParticle(SpiritSignals.DUST, pos.getX() + random.nextDouble(), pos.getY() + 0.05,
                    pos.getZ() + random.nextDouble(), 0, 0, 0);
    }
}
