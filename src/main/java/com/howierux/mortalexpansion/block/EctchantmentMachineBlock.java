package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.EctchantmentMachineBlockEntity;
import com.howierux.mortalexpansion.menu.EctchantmentMachineMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class EctchantmentMachineBlock extends BaseEntityBlock {
    public static final MapCodec<EctchantmentMachineBlock> CODEC = simpleCodec(EctchantmentMachineBlock::new);

    public EctchantmentMachineBlock(BlockBehaviour.Properties properties) {
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
        return new EctchantmentMachineBlockEntity(pos, state);
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
        if (!(level.getBlockEntity(pos) instanceof EctchantmentMachineBlockEntity machine)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int fuelValue = EctchantmentMachineBlockEntity.getFuelValue(stack);
        if (fuelValue > 0) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }

            if (!machine.canAcceptFuel(stack)) {
                player.displayClientMessage(
                        Component.translatable(
                                "message.mortal_expansion.ectchantment_machine.full",
                                machine.getEctoplasmicCharge(),
                                EctchantmentMachineBlockEntity.MAX_CHARGE,
                                machine.getEnergyStored(),
                                EctchantmentMachineBlockEntity.FE_CAPACITY
                        ),
                        true
                );
                return ItemInteractionResult.SUCCESS;
            }

            machine.addFuel(stack);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            player.displayClientMessage(
                    Component.translatable(
                            "message.mortal_expansion.ectchantment_machine.charge",
                            machine.getEctoplasmicCharge(),
                            EctchantmentMachineBlockEntity.MAX_CHARGE,
                            machine.getEnergyStored(),
                            EctchantmentMachineBlockEntity.FE_CAPACITY
                    ),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        EctchantmentMachineBlockEntity.EnchantResult result =
                machine.tryApplyDemiTangible(stack, serverLevel);

        switch (result) {
            case SUCCESS -> player.displayClientMessage(
                    Component.translatable("message.mortal_expansion.ectchantment_machine.success"),
                    true
            );
            case ALREADY_PRESENT -> player.displayClientMessage(
                    Component.translatable("message.mortal_expansion.ectchantment_machine.already_present"),
                    true
            );
            case NEEDS_FUEL -> player.displayClientMessage(
                    Component.translatable(
                            "message.mortal_expansion.ectchantment_machine.needs_fuel",
                            machine.getEctoplasmicCharge(),
                            EctchantmentMachineBlockEntity.ENCHANTMENT_COST,
                            machine.getEnergyStored(),
                            EctchantmentMachineBlockEntity.FE_ENCHANTMENT_COST
                    ),
                    true
            );
            case NOT_SUPPORTED -> player.displayClientMessage(
                    Component.translatable("message.mortal_expansion.ectchantment_machine.not_supported"),
                    true
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
        if (level.getBlockEntity(pos) instanceof EctchantmentMachineBlockEntity machine) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) ->
                                new EctchantmentMachineMenu(
                                        containerId,
                                        inventory,
                                        machine.createMenuData()
                                ),
                        Component.translatable("gui.mortal_expansion.ectchantment_machine.title")
                ));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
