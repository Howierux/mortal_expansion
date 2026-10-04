package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.haunt.NaturalPurificationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FluidBucketGameTests {
    @GameTest(template = "deferred_items_test")
    public static void cauldron_bucket_click_is_consumed_once(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        var states = new net.minecraft.world.level.block.state.BlockState[]{ModBlocks.HOLY_WATER_CAULDRON.get().defaultBlockState(),
                ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get().defaultBlockState(), ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get().defaultBlockState()};
        var items = new net.minecraft.world.item.Item[]{ModItems.HOLY_WATER_BUCKET.get(), ModItems.LIQUID_ECTOPLASM_BUCKET.get(), ModItems.PURIFIED_ECTOPLASM_BUCKET.get()};
        for (int i = 0; i < states.length; i++) {
            var pos = helper.absolutePos(new BlockPos(3 + i * 3, 2, 3));
            helper.getLevel().setBlock(pos, states[i], 3);
            helper.getLevel().setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
            helper.assertTrue(NaturalPurificationHandler.handlesCauldronInteraction(helper.getLevel(), pos, states[i], player.getMainHandItem()), "Client prediction recognizes extraction");
            var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
            var event = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit);
            NaturalPurificationHandler.onRightClickBlock(event);
            helper.assertTrue(event.isCanceled() && event.getCancellationResult().consumesAction(), "Click consumed before bucket item fallback");
            helper.assertTrue(player.getMainHandItem().is(items[i]) && player.getMainHandItem().getCount() == 1, "Exactly one full bucket received");
            helper.assertTrue(helper.getLevel().getBlockState(pos).is(Blocks.CAULDRON) && helper.getLevel().getFluidState(pos.above()).isEmpty(), "Cauldron drained with no fluid placed above it");
            // A deliberate later click can refill all three cauldron types.
            event = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit);
            NaturalPurificationHandler.onRightClickBlock(event);
            helper.assertTrue(player.getMainHandItem().is(Items.BUCKET) && helper.getLevel().getBlockState(pos).is(states[i].getBlock()), "Fluid can be returned on a new click");
        }
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void holy_salt_smelting_returns_bucket_once(GameTestHelper helper) {
        var pos = helper.absolutePos(new BlockPos(5, 2, 5));
        helper.getLevel().setBlock(pos, Blocks.FURNACE.defaultBlockState(), 3);
        var furnace = (AbstractFurnaceBlockEntity)helper.getLevel().getBlockEntity(pos);
        furnace.setItem(0, new ItemStack(ModItems.HOLY_WATER_BUCKET.get()));
        furnace.setItem(1, new ItemStack(Items.COAL));
        for (int i = 0; i < 220; i++) AbstractFurnaceBlockEntity.serverTick(helper.getLevel(), pos, helper.getLevel().getBlockState(pos), furnace);
        helper.assertTrue(furnace.getItem(2).is(ModItems.HOLY_SALT.get()) && furnace.getItem(2).getCount() == 1, "Holy Salt produced once");
        helper.assertTrue(furnace.getItem(0).is(Items.BUCKET) && furnace.getItem(0).getCount() == 1, "Vessel returned in input slot");
        for (int i = 0; i < 220; i++) AbstractFurnaceBlockEntity.serverTick(helper.getLevel(), pos, helper.getLevel().getBlockState(pos), furnace);
        helper.assertTrue(furnace.getItem(0).getCount() == 1 && furnace.getItem(2).getCount() == 1, "Further ticks cannot duplicate returns");
        furnace.setItem(0, new ItemStack(ModItems.HOLY_WATER_BUCKET.get()));
        furnace.setItem(2, new ItemStack(Items.STONE, 64));
        for (int i = 0; i < 220; i++) AbstractFurnaceBlockEntity.serverTick(helper.getLevel(), pos, helper.getLevel().getBlockState(pos), furnace);
        helper.assertTrue(furnace.getItem(0).is(ModItems.HOLY_WATER_BUCKET.get()), "Blocked smelting neither consumes nor returns prematurely");
        helper.succeed();
    }
}
