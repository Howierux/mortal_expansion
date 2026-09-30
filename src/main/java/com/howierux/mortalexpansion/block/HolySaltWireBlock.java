package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.entity.mob.HolySaltBarrierTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class HolySaltWireBlock extends RedStoneWireBlock {
    public HolySaltWireBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        HolySaltBarrierTracker.markPresent(level, pos);
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock())) {
            HolySaltBarrierTracker.invalidate(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
