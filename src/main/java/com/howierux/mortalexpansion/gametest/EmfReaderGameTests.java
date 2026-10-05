package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.item.EmfReaderItem;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.gametest.*;
import java.util.List;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EmfReaderGameTests {
    @GameTest(template = "deferred_items_test")
    public static void both_hands_switch_between_chunk_and_personal_without_changing_haunt(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setPos(helper.absolutePos(net.minecraft.core.BlockPos.ZERO).getCenter());
        var level = helper.getLevel();
        HauntManager.setValue(level, player.blockPosition(), 37);
        PlayerHauntManager.setValue(player, 52);
        for (var item : new Item[]{ModItems.EMF_READER.get(), ModItems.PRECISE_EMF_READER.get()})
        for (var hand : InteractionHand.values()) {
            var stack = new ItemStack(item);
            player.setItemInHand(hand, stack);
            stack.getItem().inventoryTick(stack, level, player, 0, hand == InteractionHand.MAIN_HAND);
            helper.assertTrue(EmfReaderItem.value(stack, player) == 37, "Either held hand samples chunk Haunt");
            stack.getItem().use(level, player, hand);
            helper.assertTrue(EmfReaderItem.value(stack, player) == 52, "Held use reads personal Haunt");
            helper.assertTrue(stack.getUseAnimation() == UseAnim.NONE && !player.isBlocking(), "Sensor pose must never grant shield protection");
            player.stopUsingItem();
            helper.assertTrue(EmfReaderItem.value(stack, player) == 37, "Releasing use immediately restores chunk reading");
            helper.assertTrue(HauntManager.getValue(level, player.blockPosition()) == 37 && PlayerHauntManager.getValue(player) == 52, "Scanning never changes Haunt");
            player.setItemInHand(hand, ItemStack.EMPTY);
            stack.getItem().inventoryTick(stack, level, player, 1, false);
            helper.assertTrue(EmfReaderItem.reading(stack, player) == null, "Stored sensors discard live readings");
        }
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void scale_and_recipe_match_haunt_tiers_and_required_materials(GameTestHelper helper) {
        for (int value = 0; value <= 69; value++) helper.assertTrue(EmfScale.litBulbs(value) == value / 10, "Every tier boundary lights the correct bulb count");
        var iron = new ItemStack(Items.IRON_INGOT);
        var red = new ItemStack(Items.REDSTONE);
        var salt = new ItemStack(ModItems.HOLY_SALT.get());
        var input = CraftingInput.of(3, 3, List.of(iron, iron, iron, red, salt, red, iron, iron, iron));
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel()).orElseThrow();
        helper.assertTrue(recipe.value().assemble(input, helper.getLevel().registryAccess()).is(ModItems.EMF_READER.get()), "Iron, Holy Salt and redstone craft a reader");
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void readings_reject_other_owners_and_survive_network_only(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setPos(helper.absolutePos(net.minecraft.core.BlockPos.ZERO).getCenter());
        var stack = new ItemStack(ModItems.EMF_READER.get());
        EmfReaderItem.sample(stack, player);
        var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            ItemStack.STREAM_CODEC.encode(buffer, stack);
            var decoded = ItemStack.STREAM_CODEC.decode(buffer);
            helper.assertTrue(EmfReaderItem.reading(decoded, player) != null, "Inventory synchronization retains live sample");
            var other = helper.makeMockServerPlayerInLevel();
            other.setPos(player.position());
            helper.assertTrue(EmfReaderItem.reading(decoded, other) == null, "Another owner cannot see stale personal readings");
            other.discard();
            player.setPos(player.getX() + 16, player.getY(), player.getZ());
            helper.assertTrue(EmfReaderItem.reading(decoded, player) == null, "Crossing chunks rejects the previous chunk reading");
        } finally { buffer.release(); }
        var ops = net.minecraft.resources.RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE, helper.getLevel().registryAccess());
        var restored = ItemStack.CODEC.parse(ops, ItemStack.CODEC.encodeStart(ops, stack).getOrThrow()).getOrThrow();
        helper.assertTrue(!restored.has(ModDataComponents.EMF_READING.get()), "Live samples do not persist in saved items");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void upgrade_consumes_materials_and_synchronizes_exact_hundredths(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setPos(helper.absolutePos(net.minecraft.core.BlockPos.ZERO).getCenter());
        var menu = player.inventoryMenu;
        menu.getSlot(1).set(new ItemStack(ModItems.EMF_READER.get()));
        menu.getSlot(4).set(new ItemStack(ModItems.RAW_ECTOPLASM.get(), 2));
        menu.slotsChanged(menu.getSlot(1).container);
        var upgraded = menu.getSlot(0).remove(1); menu.getSlot(0).onTake(player, upgraded);
        helper.assertTrue(upgraded.is(ModItems.PRECISE_EMF_READER.get()) && upgraded.getCount() == 1,
                "Reader plus raw ectoplasm crafts one precise reader in an arbitrary arrangement");
        helper.assertTrue(menu.getSlot(1).getItem().isEmpty() && menu.getSlot(4).getItem().getCount() == 1,
                "Consumes one reader and exactly one ectoplasm");
        HauntManager.setValue(helper.getLevel(), player.blockPosition(), 37);
        PlayerHauntManager.setValue(player, 24); PlayerHauntManager.addHundredths(player, 37);
        player.setItemInHand(InteractionHand.OFF_HAND, upgraded);
        EmfReaderItem.sample(upgraded, player);
        helper.assertTrue(EmfScale.decimal(EmfReaderItem.hundredths(upgraded, player)).equals("37.00"), "Chunk has real whole-point precision");
        upgraded.getItem().use(helper.getLevel(), player, InteractionHand.OFF_HAND);
        helper.assertTrue(EmfScale.decimal(EmfReaderItem.hundredths(upgraded, player)).equals("24.37"), "Personal fractional state displayed without rounding to whole points");
        PlayerHauntManager.addHundredths(player, 1); EmfReaderItem.sample(upgraded, player);
        helper.assertTrue(EmfReaderItem.hundredths(upgraded, player) == 2438, "Changes smaller than one point update the live sample");
        var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            ItemStack.STREAM_CODEC.encode(buffer, upgraded);
            var decoded = ItemStack.STREAM_CODEC.decode(buffer);
            helper.assertTrue(EmfReaderItem.reading(decoded, player).personalHundredths() == 2438, "Network codec preserves hundredths");
        } finally { buffer.release(); }
        helper.assertTrue(EmfScale.decimal(1).equals("0.01") && EmfScale.decimal(6900).equals("69.00")
                && EmfScale.decimal(-1).equals("--.--"), "Decimal formatting preserves zero padding, cap and missing samples");
        player.stopUsingItem(); player.discard(); helper.succeed();
    }
}
