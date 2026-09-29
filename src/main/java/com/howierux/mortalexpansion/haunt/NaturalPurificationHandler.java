package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Natural Pure Ectoplasm process performed in a heated cauldron.
 */
public final class NaturalPurificationHandler {
    private NaturalPurificationHandler() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack held = player.getItemInHand(hand);
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.WATER_CAULDRON)
                && state.getValue(LayeredCauldronBlock.LEVEL) == 3
                && isBoiling(level, pos)) {
            if (held.is(ModItems.RAW_ECTOPLASM.get())) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get().defaultBlockState(),
                        3
                );
                succeed(event);
                return;
            }

            if (held.is(ModItems.GRAVE_MOSS.get())) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        ModBlocks.HOLY_WATER_CAULDRON.get().defaultBlockState(),
                        3
                );
                succeed(event);
                return;
            }
        }

        if (state.is(ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get())) {
            if (held.is(ModItems.GRAVE_MOSS.get()) && isBoiling(level, pos)) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get().defaultBlockState(),
                        3
                );
                succeed(event);
                return;
            }

            if (held.is(Items.BUCKET)) {
                fillBucket(
                        player,
                        hand,
                        held,
                        ModItems.LIQUID_ECTOPLASM_BUCKET.get()
                );
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                succeed(event);
                return;
            }
        }

        if (state.is(ModBlocks.HOLY_WATER_CAULDRON.get())) {
            if (held.is(ModItems.RAW_ECTOPLASM.get()) && isBoiling(level, pos)) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get().defaultBlockState(),
                        3
                );
                succeed(event);
                return;
            }

            if (held.is(Items.BUCKET)) {
                fillBucket(
                        player,
                        hand,
                        held,
                        ModItems.HOLY_WATER_BUCKET.get()
                );
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                succeed(event);
                return;
            }
        }

        if (state.is(ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get())
                && held.is(Items.BUCKET)) {
            fillBucket(
                    player,
                    hand,
                    held,
                    ModItems.PURIFIED_ECTOPLASM_BUCKET.get()
            );
            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            succeed(event);
        }
    }

    private static boolean isBoiling(Level level, BlockPos cauldronPos) {
        BlockState heat = level.getBlockState(cauldronPos.below());

        if (heat.getBlock() instanceof CampfireBlock
                && heat.hasProperty(CampfireBlock.LIT)
                && heat.getValue(CampfireBlock.LIT)) {
            return true;
        }

        return heat.is(Blocks.FIRE)
                || heat.is(Blocks.SOUL_FIRE)
                || heat.is(Blocks.LAVA)
                || level.getFluidState(cauldronPos.below()).is(Fluids.LAVA);
    }

    private static void consumeOne(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static void fillBucket(
            Player player,
            InteractionHand hand,
            ItemStack emptyBuckets,
            Item filledBucket
    ) {
        ItemStack filled = new ItemStack(filledBucket);

        if (player.getAbilities().instabuild) {
            if (!player.getInventory().contains(filled)) {
                player.getInventory().add(filled);
            }
            return;
        }

        emptyBuckets.shrink(1);
        if (emptyBuckets.isEmpty()) {
            player.setItemInHand(hand, filled);
        } else if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
    }

    private static void succeed(PlayerInteractEvent.RightClickBlock event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
