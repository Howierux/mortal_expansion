package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SpiritTorchBlock extends RedstoneTorchBlock {
    public SpiritTorchBlock(Properties properties) { super(properties); }
    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return 0; }
    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return 0; }
    @Override
    protected boolean isSignalSource(BlockState state) { return false; }
    @Override
    protected boolean hasNeighborSignal(Level level, BlockPos pos, BlockState state) {
        return super.hasNeighborSignal(level, pos, state) || SpiritSignals.saltPowersSupport(level, pos.below());
    }
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos,
            BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (!oldState.is(state.getBlock())) SpiritTorchBarrierTracker.addTorch(level, pos);
    }
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos,
            BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) SpiritTorchBarrierTracker.removeTorch(level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        SpiritSignals.particles(state, level, pos, random);
    }
}
