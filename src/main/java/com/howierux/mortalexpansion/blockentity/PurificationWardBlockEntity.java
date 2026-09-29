package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.PurificationWardTracker;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PurificationWardBlockEntity extends BlockEntity {
    public PurificationWardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PURIFICATION_WARD.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level instanceof ServerLevel serverLevel) {
            PurificationWardTracker.add(serverLevel, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            PurificationWardTracker.remove(serverLevel, worldPosition);
        }

        super.setRemoved();
    }
}
