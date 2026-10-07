package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.MortalityResearchStationBlockEntity;
import com.howierux.mortalexpansion.menu.MortalityResearchStationMenu;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class MortalityResearchStationBlock extends BaseEntityBlock {
    public static final MapCodec<MortalityResearchStationBlock> CODEC =
            simpleCodec(MortalityResearchStationBlock::new);

    public MortalityResearchStationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext context){return net.minecraft.world.level.block.Block.box(0,0,0,16,12,16);}

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
        return new MortalityResearchStationBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof MortalityResearchStationBlockEntity generator)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, menuPlayer) ->
                                    new MortalityResearchStationMenu(
                                            containerId,
                                            inventory,
                                            generator
                                    ),
                            Component.translatable(
                                    "gui.mortal_expansion.mortality_research_station.title"
                            )
                    ),
                    pos
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock())
                && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof MortalityResearchStationBlockEntity generator) {
            generator.dropInventory(serverLevel);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
