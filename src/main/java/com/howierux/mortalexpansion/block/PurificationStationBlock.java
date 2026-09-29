package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.PurificationStationBlockEntity;
import com.howierux.mortalexpansion.menu.PurificationStationMenu;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModDataComponents;
import com.howierux.mortalexpansion.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PurificationStationBlock extends BaseEntityBlock {
    public static final MapCodec<PurificationStationBlock> CODEC =
            simpleCodec(PurificationStationBlock::new);

    public PurificationStationBlock(BlockBehaviour.Properties properties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PurificationStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide ? null : createTickerHelper(
                type,
                ModBlockEntities.PURIFICATION_STATION.get(),
                PurificationStationBlockEntity::serverTick
        );
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof PurificationStationBlockEntity station)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, menuPlayer) ->
                                    new PurificationStationMenu(
                                            containerId,
                                            inventory,
                                            station
                                    ),
                            Component.translatable(
                                    "gui.mortal_expansion.purification_station.title"
                            )
                    ),
                    pos
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide
                && level.getBlockEntity(pos) instanceof PurificationStationBlockEntity station) {
            var data = stack.get(ModDataComponents.PURIFICATION_STATION_DATA.get());
            if (data != null) {
                station.loadPortableData(data);
            }
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack stationStack = new ItemStack(ModItems.PURIFICATION_STATION.get());

        BlockEntity blockEntity = builder.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY
        );

        if (blockEntity instanceof PurificationStationBlockEntity station) {
            stationStack.set(
                    ModDataComponents.PURIFICATION_STATION_DATA.get(),
                    station.savePortableData(builder.getLevel().registryAccess())
            );
        }

        return List.of(stationStack);
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
                && level.getBlockEntity(pos) instanceof PurificationStationBlockEntity station) {
            station.dropUnprocessedInventory(serverLevel);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
