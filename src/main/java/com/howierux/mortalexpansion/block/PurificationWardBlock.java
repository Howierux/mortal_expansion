package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.PurificationWardBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import com.howierux.mortalexpansion.haunt.PurificationWardChunkLoader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Wood-and-moss Haunt cleansing ward. It deliberately has no Beacon block
 * entity, so it never creates a beacon beam.
 */
public final class PurificationWardBlock extends BaseEntityBlock {
    public static final MapCodec<PurificationWardBlock> CODEC =
            simpleCodec(PurificationWardBlock::new);

    public PurificationWardBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos,
            BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            // Release on physical removal, never on chunk unload or server shutdown.
            PurificationWardChunkLoader.release(serverLevel, pos);
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PurificationWardBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return null;
    }
}
