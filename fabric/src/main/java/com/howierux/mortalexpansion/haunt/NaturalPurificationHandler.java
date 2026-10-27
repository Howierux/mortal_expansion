package com.howierux.mortalexpansion.haunt;

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

/**
 * Natural Pure Ectoplasm process performed in a heated cauldron.
 */
public final class NaturalPurificationHandler {
    private NaturalPurificationHandler() {}

    public static InteractionResult interact(Player player,Level level,InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit){
        var held=player.getItemInHand(hand);var pos=hit.getBlockPos();var state=level.getBlockState(pos);
        if(!handlesCauldronInteraction(level,pos,state,held))return InteractionResult.PASS;
        if(player.isSpectator()||!player.mayBuild()||!player.mayUseItemAt(pos,hit.getDirection(),held))return InteractionResult.FAIL;
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(level instanceof net.minecraft.server.level.ServerLevel server&&!server.mayInteract(player,pos))return InteractionResult.FAIL;
        if (state.is(Blocks.CAULDRON)) {
            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.PURIFIED_ECTOPLASM_BUCKET)) {
                emptyFluidBucketIntoCauldron(player, hand, com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON.defaultBlockState(), level, pos);
                return InteractionResult.CONSUME;
            }
            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.LIQUID_ECTOPLASM_BUCKET)) {
                emptyFluidBucketIntoCauldron(
                        player,
                        hand,
                        com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON.defaultBlockState(),
                        level,
                        pos
                );
                return InteractionResult.CONSUME;
            }

            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET)) {
                emptyFluidBucketIntoCauldron(
                        player,
                        hand,
                        com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON.defaultBlockState(),
                        level,
                        pos
                );
                return InteractionResult.CONSUME;
            }
        }

        if (state.is(Blocks.WATER_CAULDRON)
                && state.getValue(LayeredCauldronBlock.LEVEL) == 3
                && hasHeatSource(level, pos)) {
            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM)) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON.defaultBlockState(),
                        3
                );
                return InteractionResult.CONSUME;
            }

            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS)) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON.defaultBlockState(),
                        3
                );
                return InteractionResult.CONSUME;
            }
        }

        if (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON)) {
            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS) && hasHeatSource(level, pos)) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON.defaultBlockState(),
                        3
                );
                return InteractionResult.CONSUME;
            }

            if (held.is(Items.BUCKET)) {
                fillBucket(
                        player,
                        hand,
                        held,
                        com.howierux.mortalexpansion.catalog.CatalogItems.LIQUID_ECTOPLASM_BUCKET
                );
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                return InteractionResult.CONSUME;
            }
        }

        if (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON)) {
            if (held.is(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM) && hasHeatSource(level, pos)) {
                consumeOne(player, held);
                level.setBlock(
                        pos,
                        com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON.defaultBlockState(),
                        3
                );
                return InteractionResult.CONSUME;
            }

            if (held.is(Items.BUCKET)) {
                fillBucket(
                        player,
                        hand,
                        held,
                        com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET
                );
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                return InteractionResult.CONSUME;
            }
        }

        if (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON)
                && held.is(Items.BUCKET)) {
            fillBucket(
                    player,
                    hand,
                    held,
                    com.howierux.mortalexpansion.catalog.CatalogItems.PURIFIED_ECTOPLASM_BUCKET
            );
            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }


    /** Client prediction must consume exactly the interactions handled by the server. */
    public static boolean handlesCauldronInteraction(Level level, BlockPos pos, BlockState state, ItemStack held) {
        if (state.is(Blocks.CAULDRON)) return held.is(com.howierux.mortalexpansion.catalog.CatalogItems.LIQUID_ECTOPLASM_BUCKET)
                || held.is(com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET) || held.is(com.howierux.mortalexpansion.catalog.CatalogItems.PURIFIED_ECTOPLASM_BUCKET);
        if (held.is(Items.BUCKET)) return state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON)
                || state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON) || state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON);
        if (!hasHeatSource(level, pos)) return false;
        if (state.is(Blocks.WATER_CAULDRON) && state.getValue(LayeredCauldronBlock.LEVEL) == 3)
            return held.is(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM) || held.is(com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS);
        return (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON) && held.is(com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS))
                || (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON) && held.is(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM));
    }

    public static void itemTick(ItemEntity itemEntity) {
        if (itemEntity.level().isClientSide
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
            if (stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM)) {
                resultState = com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON
                        .defaultBlockState();
            } else if (stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS)) {
                resultState = com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON
                        .defaultBlockState();
            }
        } else if (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON)
                && stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS)) {
            resultState = com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON
                    .defaultBlockState();
        } else if (state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON)
                && stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM)) {
            resultState = com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON
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
                || state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON)
                || state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.HOLY_WATER_CAULDRON);
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

}
