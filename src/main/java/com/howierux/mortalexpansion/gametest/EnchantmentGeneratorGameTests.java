package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.EnchantmentGeneratorBlockEntity;
import com.howierux.mortalexpansion.menu.EnchantmentGeneratorMenu;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EnchantmentGeneratorGameTests {
    @GameTest(template="deferred_items_test")
    public static void gui_slots_process_preserve_and_return_exactly_one_item(GameTestHelper h) {
        var level = h.getLevel(); var pos = h.absolutePos(new BlockPos(4,2,4));
        level.setBlock(pos, ModBlocks.ENCHANTMENT_GENERATOR.get().defaultBlockState(), 3);
        var generator = (EnchantmentGeneratorBlockEntity)level.getBlockEntity(pos);
        var player = h.makeMockServerPlayerInLevel(); player.setPos(pos.getCenter());
        var menu = new EnchantmentGeneratorMenu(1, player.getInventory(), generator);
        var item = new ItemStack(Items.DIAMOND_PICKAXE); item.setDamageValue(37);
        item.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), 1);
        h.assertTrue(menu.slots.size() == 38 && !menu.getSlot(0).mayPlace(new ItemStack(Items.DIRT))
                && !menu.getSlot(1).mayPlace(item), "GUI has inventory and validated input/output slots");
        player.getInventory().setItem(9, item);
        menu.quickMoveStack(player, 2);
        h.assertTrue(player.getInventory().getItem(9).isEmpty() && menu.getSlot(0).getItem().getCount() == 1, "Shift click transfers one input without duplication");
        var restored = new EnchantmentGeneratorBlockEntity(pos, generator.getBlockState());
        restored.loadAdditional(generator.saveWithoutMetadata(level.registryAccess()), level.registryAccess());
        h.assertTrue(restored.getMenuItems().getItem(0).is(Items.DIAMOND_PICKAXE), "Queued input survives save/load");
        EnchantmentGeneratorBlockEntity.serverTick(level, pos, generator.getBlockState(), generator);
        h.assertTrue(menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty(), "Processing consumes input and hides unfinished output");
        for (int i=0; i<3000 && generator.isProcessing(); i++)
            EnchantmentGeneratorBlockEntity.serverTick(level, pos, generator.getBlockState(), generator);
        h.assertTrue(generator.hasFinishedItem() && generator.getEnergyStored()>0, "Enchantment produces FE before output unlocks");
        menu.quickMoveStack(player, 1);
        int outputs = 0;
        for (int i=0; i<player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (stack.is(Items.DIAMOND_PICKAXE)) {
                outputs += stack.getCount();
                h.assertTrue(stack.getDamageValue()==37 && EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty(), "Output preserves damage and removes enchantments");
            }
        }
        h.assertTrue(outputs==1 && !generator.hasFinishedItem(), "Shift click returns exactly one result");
        player.setPos(pos.getCenter().add(20,0,0)); h.assertFalse(menu.stillValid(player), "GUI closes out of range");
        player.discard(); h.succeed();
    }
}
