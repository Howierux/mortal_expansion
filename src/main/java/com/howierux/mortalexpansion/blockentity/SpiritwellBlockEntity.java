package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Dedicated anchor type for future Haunt effects and Spirit Journey interactions. */
public final class SpiritwellBlockEntity extends BlockEntity {
    public SpiritwellBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPIRITWELL.get(), pos, state);
    }
}
