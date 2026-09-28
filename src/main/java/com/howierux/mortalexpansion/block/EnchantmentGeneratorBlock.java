package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.EnchantmentGeneratorBlockEntity;
import com.howierux.mortalexpansion.menu.EnchantmentGeneratorMenu;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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

public final class EnchantmentGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<EnchantmentGeneratorBlock> CODEC =
            simpleCodec(EnchantmentGeneratorBlock::new);

    public EnchantmentGeneratorBlock(BlockBehaviour.Properties properties) {
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
        return new EnchantmentGeneratorBlockEntity(pos, state);
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
                ModBlockEntities.ENCHANTMENT_GENERATOR.get(),
                EnchantmentGeneratorBlockEntity::serverTick
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
        if (!(level.getBlockEntity(pos) instanceof EnchantmentGeneratorBlockEntity generator)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!generator.canAccept(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (generator.insertEnchantedItem(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.displayClientMessage(
                    Component.translatable(
                            "message.mortal_expansion.enchantment_generator.started",
                            generator.getRemainingEnergyToGenerate(),
                            generator.getGenerationPerTick()
                    ),
                    false
            );
        }

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
        if (!(level.getBlockEntity(pos) instanceof EnchantmentGeneratorBlockEntity generator)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && generator.hasFinishedItem()) {
            ItemStack result = generator.takeFinishedItem();
            if (!player.addItem(result)) {
                player.drop(result, false);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) ->
                            new EnchantmentGeneratorMenu(
                                    containerId,
                                    inventory,
                                    generator.createMenuData()
                            ),
                    Component.translatable("gui.mortal_expansion.enchantment_generator.title")
            ));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
