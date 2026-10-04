package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Natural Pure Ectoplasm process performed in a heated cauldron.
 */
public final class NaturalPurificationHandler {
    private NaturalPurificationHandler() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack held = player.getItemInHand(hand);
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (level.isClientSide) {
            if (handlesCauldronInteraction(level, pos, state, held)) succeed(event);
            return;
        }

        if (state.is(Blocks.CAULDRON)) {
            if (held.is(ModItems.PURIFIED_ECTOPLASM_BUCKET.get())) {
                emptyFluidBucketIntoCauldron(player, hand, ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get().defaultBlockState(), level, pos);
                succeed(event);
                return;
            }
            if (held.is(ModItems.LIQUID_ECTOPLASM_BUCKET.get())) {
                emptyFluidBucketIntoCauldron(
                        player,
                        hand,
                        ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get().defaultBlockState(),
                        level,
                        pos
                );
                succeed(event);
                return;
            }

            if (held.is(ModItems.HOLY_WATER_BUCKET.get())) {
                emptyFluidBucketIntoCauldron(
                        player,
                        hand,
                        ModBlocks.HOLY_WATER_CAULDRON.get().defaultBlockState(),
                        level,
                        pos
                );
                succeed(event);
                return;
            }
        }

        if (state.is(Blocks.WATER_CAULDRON)
                && state.getValue(LayeredCauldronBlock.LEVEL) == 3
                && hasHeatSource(level, pos)) {
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
            if (held.is(ModItems.GRAVE_MOSS.get()) && hasHeatSource(level, pos)) {
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
            if (held.is(ModItems.RAW_ECTOPLASM.get()) && hasHeatSource(level, pos)) {
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


    /** Client prediction must consume exactly the interactions handled by the server. */
    public static boolean handlesCauldronInteraction(Level level, BlockPos pos, BlockState state, ItemStack held) {
        if (state.is(Blocks.CAULDRON)) return held.is(ModItems.LIQUID_ECTOPLASM_BUCKET.get())
                || held.is(ModItems.HOLY_WATER_BUCKET.get()) || held.is(ModItems.PURIFIED_ECTOPLASM_BUCKET.get());
        if (held.is(Items.BUCKET)) return state.is(ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get())
                || state.is(ModBlocks.HOLY_WATER_CAULDRON.get()) || state.is(ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get());
        if (!hasHeatSource(level, pos)) return false;
        if (state.is(Blocks.WATER_CAULDRON) && state.getValue(LayeredCauldronBlock.LEVEL) == 3)
            return held.is(ModItems.RAW_ECTOPLASM.get()) || held.is(ModItems.GRAVE_MOSS.get());
        return (state.is(ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get()) && held.is(ModItems.GRAVE_MOSS.get()))
                || (state.is(ModBlocks.HOLY_WATER_CAULDRON.get()) && held.is(ModItems.RAW_ECTOPLASM.get()));
    }

    @SubscribeEvent
    public static void onItemTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)
                || itemEntity.level().isClientSide
                || itemEntity.getItem().isEmpty()) {
            return;
        }

        Level level = itemEntity.level();
        BlockPos cauldronPos = findCauldronAtItem(level, itemEntity);
        if (cauldronPos == null || !hasHeatSource(level, cauldronPos)) {
            return;
        }

        BlockState state = level.getBlockState(cauldronPos);
        ItemStack stack = itemEntity.getItem();
        BlockState resultState = null;

        if (state.is(Blocks.WATER_CAULDRON)
                && state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
            if (stack.is(ModItems.RAW_ECTOPLASM.get())) {
                resultState = ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get()
                        .defaultBlockState();
            } else if (stack.is(ModItems.GRAVE_MOSS.get())) {
                resultState = ModBlocks.HOLY_WATER_CAULDRON.get()
                        .defaultBlockState();
            }
        } else if (state.is(ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get())
                && stack.is(ModItems.GRAVE_MOSS.get())) {
            resultState = ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get()
                    .defaultBlockState();
        } else if (state.is(ModBlocks.HOLY_WATER_CAULDRON.get())
                && stack.is(ModItems.RAW_ECTOPLASM.get())) {
            resultState = ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get()
                    .defaultBlockState();
        }

        if (resultState == null) {
            return;
        }

        level.setBlock(cauldronPos, resultState, 3);

        if (stack.getCount() <= 1) {
            itemEntity.discard();
            return;
        }

        stack.shrink(1);
        itemEntity.setItem(stack);
        itemEntity.setPos(
                cauldronPos.getX() + 0.5D,
                cauldronPos.getY() + 1.05D,
                cauldronPos.getZ() + 0.5D
        );
        itemEntity.setDeltaMovement(0.0D, 0.08D, 0.0D);
    }

    private static BlockPos findCauldronAtItem(Level level, ItemEntity itemEntity) {
        BlockPos current = itemEntity.blockPosition();
        if (isPurificationCauldron(level.getBlockState(current))) {
            return current;
        }

        BlockPos below = current.below();
        if (isPurificationCauldron(level.getBlockState(below))) {
            return below;
        }

        return null;
    }

    private static boolean isPurificationCauldron(BlockState state) {
        return state.is(Blocks.WATER_CAULDRON)
                || state.is(ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get())
                || state.is(ModBlocks.HOLY_WATER_CAULDRON.get());
    }

    private static boolean hasHeatSource(Level level, BlockPos cauldronPos) {
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


    private static void emptyFluidBucketIntoCauldron(
            Player player,
            InteractionHand hand,
            BlockState cauldronState,
            Level level,
            BlockPos pos
    ) {
        level.setBlock(pos, cauldronState, 3);

        if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        }
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
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }
}
