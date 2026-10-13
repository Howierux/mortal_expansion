package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.PurificationStationBlockEntity;
import com.howierux.mortalexpansion.menu.PurificationStationMenu;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PurificationStationGameTests {
    private static PurificationStationBlockEntity station(GameTestHelper helper, int x) {
        BlockPos pos = helper.absolutePos(new BlockPos(x, 2, 3));
        helper.getLevel().setBlock(pos, ModBlocks.PURIFICATION_STATION.get().defaultBlockState(), 3);
        return (PurificationStationBlockEntity) helper.getLevel().getBlockEntity(pos);
    }

    private static void tick(GameTestHelper helper, PurificationStationBlockEntity station, int count) {
        for (int i = 0; i < count; i++)
            PurificationStationBlockEntity.serverTick(helper.getLevel(), station.getBlockPos(),
                    station.getBlockState(), station);
    }

    @GameTest(template = "deferred_items_test")
    public static void salt_and_moss_purify_with_identical_consumption_and_output(GameTestHelper helper) {
        var reagents = new net.minecraft.world.item.Item[]{ModItems.HOLY_SALT.get(), ModItems.GRAVE_MOSS.get()};
        for (int i = 0; i < reagents.length; i++) {
            var station = station(helper, 3 + i * 2);
            var handler = station.getAutomationItems();
            helper.assertTrue(handler.insertItem(1, new ItemStack(reagents[i], 2), true).isEmpty()
                    && handler.getStackInSlot(1).isEmpty(), "Automation simulation accepts without consuming");
            helper.assertTrue(handler.insertItem(1, new ItemStack(reagents[i], 2), false).isEmpty(),
                    "Automation accepts either purification reagent");
            station.getItems().setStackInSlot(0, new ItemStack(ModItems.RAW_ECTOPLASM.get(), 2));
            station.getWaterTank().fill(new FluidStack(Fluids.WATER, 2000), IFluidHandler.FluidAction.EXECUTE);
            for (int fill = 0; fill < 10; fill++) station.getEnergyStorage().receiveEnergy(10000, false);
            tick(helper, station, PurificationStationBlockEntity.PROCESS_TICKS);
            helper.assertTrue(station.getItems().getStackInSlot(0).getCount() == 1
                    && station.getItems().getStackInSlot(1).getCount() == 1,
                    "One raw ectoplasm and one reagent are consumed");
            helper.assertTrue(station.getItems().getStackInSlot(2).is(ModItems.PURE_ECTOPLASM.get())
                    && station.getItems().getStackInSlot(2).getCount() == 1,
                    "Either reagent produces exactly one purified ectoplasm");
            helper.assertTrue(station.getWaterTank().getFluidAmount() == 1000
                    && station.getEnergyStorage().getEnergyStored() == 0 && station.getProgress() == 0,
                    "Water, energy and processing duration retain their existing costs");
        }
        helper.succeed();
    }

    @GameTest(template = "deferred_items_test")
    public static void salt_shift_clicks_into_reagent_slot_and_blocked_output_preserves_it(GameTestHelper helper) {
        var station = station(helper, 3);
        var player = helper.makeMockServerPlayerInLevel();
        var menu = new PurificationStationMenu(1, player.getInventory(), station);
        var salt = new ItemStack(ModItems.HOLY_SALT.get(), 2);
        helper.assertTrue(menu.getSlot(1).mayPlace(salt)
                && !menu.getSlot(0).mayPlace(salt) && !menu.getSlot(2).mayPlace(salt),
                "Manual insertion accepts salt only in the reagent slot");
        menu.getSlot(4).set(salt);
        helper.assertTrue(!menu.quickMoveStack(player, 4).isEmpty()
                && station.getItems().getStackInSlot(1).getCount() == 2 && menu.getSlot(4).getItem().isEmpty(),
                "Shift-click routes Holy Salt into the Grave Moss slot");
        helper.assertTrue(!station.getAutomationItems().isItemValid(1, new ItemStack(Items.DIRT)),
                "Unrelated items are still rejected");
        station.getItems().setStackInSlot(0, new ItemStack(ModItems.RAW_ECTOPLASM.get()));
        station.getItems().setStackInSlot(2, new ItemStack(ModItems.PURE_ECTOPLASM.get(), 64));
        station.getWaterTank().fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        tick(helper, station, 1);
        helper.assertTrue(station.getItems().getStackInSlot(1).getCount() == 2
                && station.getItems().getStackInSlot(0).getCount() == 1
                && station.getWaterTank().getFluidAmount() == 1000 && station.getProgress() == 0,
                "A full output consumes no salt, raw ectoplasm or water");
        player.discard();
        helper.succeed();
    }
}
