package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.EctoplasmicGeneratorBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class EctoplasmicGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<EctoplasmicGeneratorBlock> CODEC =
            simpleCodec(EctoplasmicGeneratorBlock::new);

    public EctoplasmicGeneratorBlock(BlockBehaviour.Properties properties) {
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
        return new EctoplasmicGeneratorBlockEntity(pos, state);
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
                        ModBlockEntities.ECTOPLASMIC_GENERATOR.get(),
                        EctoplasmicGeneratorBlockEntity::serverTick
                );
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof EctoplasmicGeneratorBlockEntity generator)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!stack.is(com.howierux.mortalexpansion.registry.ModItems.RAW_ECTOPLASM.get())
                && !stack.is(com.howierux.mortalexpansion.registry.ModItems.PURE_ECTOPLASM.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!generator.addFuel(stack)) {
            player.displayClientMessage(
                    Component.translatable("message.mortal_expansion.ectoplasmic_generator.busy"),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        showStatus(player, generator);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(pos) instanceof EctoplasmicGeneratorBlockEntity generator) {
            if (!level.isClientSide) {
                showStatus(player, generator);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private static void showStatus(Player player, EctoplasmicGeneratorBlockEntity generator) {
        player.displayClientMessage(
                Component.translatable(
                        "message.mortal_expansion.ectoplasmic_generator.status",
                        generator.getEnergyStored(),
                        EctoplasmicGeneratorBlockEntity.ENERGY_CAPACITY,
                        generator.getBurnTicksRemaining(),
                        generator.getLocalHauntTier().displayName(),
                        String.format("%.2f", generator.getHauntMultiplier()),
                        generator.getCurrentGenerationPerTick()
                ),
                false
        );
    }
}
