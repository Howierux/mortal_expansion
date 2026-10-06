package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.combat.HaintGarbJump;
import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import com.howierux.mortalexpansion.combat.IntangibleCombatHandler;
import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.registry.ModRecipeSerializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import io.netty.buffer.Unpooled;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HaintGarbGameTests {
    private static Item[] ghosts() { return new Item[]{ModItems.GHOST_GARB_HELMET.get(), ModItems.GHOST_GARB_CHESTPLATE.get(), ModItems.GHOST_GARB_LEGGINGS.get(), ModItems.GHOST_GARB_BOOTS.get()}; }
    private static Item[] haints() { return new Item[]{ModItems.HAINT_GARB_HELMET.get(), ModItems.HAINT_GARB_CHESTPLATE.get(), ModItems.HAINT_GARB_LEGGINGS.get(), ModItems.HAINT_GARB_BOOTS.get()}; }
    private static Item[] spectralite() { return new Item[]{ModItems.SPECTRALITE_HELMET.get(), ModItems.SPECTRALITE_CHESTPLATE.get(), ModItems.SPECTRALITE_LEGGINGS.get(), ModItems.SPECTRALITE_BOOTS.get()}; }
    private static Item[] combined() { return new Item[]{ModItems.SPECTRALITE_HAINT_HELMET.get(), ModItems.SPECTRALITE_HAINT_CHESTPLATE.get(), ModItems.SPECTRALITE_HAINT_LEGGINGS.get(), ModItems.SPECTRALITE_HAINT_BOOTS.get()}; }
    private static void equip(net.minecraft.server.level.ServerPlayer player, Item[] items) {
        var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < 4; i++) player.setItemSlot(slots[i], new ItemStack(items[i]));
        player.doTick();
    }

    @GameTest(template = "deferred_items_test")
    public static void smithing_upgrades_without_templates_preserve_base_components(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        com.howierux.mortalexpansion.guide.SpectralInsight.learnPlayer(player,15);
        var level = helper.getLevel();
        var enchantment = level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        for (int i = 0; i < 4; i++) for (int step = 0; step < 2; step++) {
            var base = new ItemStack((step == 0 ? ghosts() : haints())[i]);
            base.setDamageValue(7); base.set(DataComponents.CUSTOM_NAME, Component.literal("Remembered Garb")); base.enchant(enchantment, 3);
            var addition = new ItemStack(step == 0 ? ModItems.PURE_ECTOPLASM.get() : spectralite()[i]);
            Item result = (step == 0 ? haints() : combined())[i];
            var input = new SmithingRecipeInput(ItemStack.EMPTY, base, addition);
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, input, level).orElseThrow().value();
            helper.assertFalse(recipe.isIncomplete(), "Intentionally missing template is a complete recipe");
            helper.assertFalse(recipe.matches(new SmithingRecipeInput(new ItemStack(net.minecraft.world.item.Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), base, addition), level), "Template slot must be empty");
            if (step == 1) helper.assertFalse(recipe.matches(new SmithingRecipeInput(ItemStack.EMPTY, base, new ItemStack(spectralite()[(i + 1) % 4])), level), "Spectralite must be the same armor part");
            // Exercise the real vanilla menu, including its slot predicates and result-taking consumption.
            var menu = new SmithingMenu(1, player.getInventory());
            helper.assertTrue(menu.getSlot(1).mayPlace(base) && menu.getSlot(2).mayPlace(addition), "Inputs accepted by the vanilla smithing menu");
            menu.getSlot(1).set(base.copy()); menu.getSlot(2).set(addition.copy()); menu.createResult();
            helper.assertTrue(menu.getSlot(3).mayPickup(player), "Template-free result can be taken");
            var crafted = menu.getSlot(3).remove(1); menu.getSlot(3).onTake(player, crafted);
            helper.assertTrue(crafted.is(result) && crafted.getDamageValue() == 7 && crafted.getHoverName().getString().equals("Remembered Garb"), "Correct output retains name and wear");
            helper.assertTrue(crafted.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY).getLevel(enchantment) == 3, "Upgrade preserves enchantments");
            helper.assertTrue(menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty() && menu.getSlot(2).getItem().isEmpty(), "Consumes exactly the two inputs without a template");
            var codec = ModRecipeSerializers.TEMPLATE_FREE_SMITHING.get().streamCodec();
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            try {
                codec.encode(buffer, (com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe) recipe);
                var decoded = codec.decode(buffer);
                helper.assertTrue(decoded.matches(input, level) && decoded.assemble(input, level.registryAccess()).is(result), "Client network recipe roundtrip retains upgrade behavior");
            } finally { buffer.release(); }
        }
        player.discard(); helper.succeed();
    }

    @GameTest(template = "deferred_items_test")
    public static void full_haint_jump_survives_mixed_upgrades_and_removes_with_a_piece(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel(); player.setGameMode(GameType.SURVIVAL);
        equip(player, haints()); player.jumpFromGround();
        helper.assertTrue(Math.abs(player.getDeltaMovement().y - 0.72D) < 0.0001, "Full Haint set jumps with Jump Boost III strength");
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(combined()[0])); player.doTick(); player.jumpFromGround();
        helper.assertTrue(Math.abs(player.getDeltaMovement().y - 0.72D) < 0.0001, "Mixing Haint and combined armor keeps jump strength");
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ghosts()[0])); player.doTick(); player.jumpFromGround();
        helper.assertTrue(Math.abs(player.getDeltaMovement().y - 0.42D) < 0.0001 && GhostGarbPhasing.active(player), "Ghost/Haint mix phases but does not grant Haint's full-set jump");
        equip(player, combined()); player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY); player.doTick();
        helper.assertTrue(Math.abs(player.getAttributeValue(Attributes.JUMP_STRENGTH) - 0.42D) < 0.0001 && !GhostGarbPhasing.active(player), "Removing a piece removes jump and intangibility");
        player.discard(); helper.succeed();
    }

    @GameTest(template = "deferred_items_test")
    public static void spectralite_haint_has_spectral_defense_and_garb_abilities(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel(); player.setGameMode(GameType.SURVIVAL); equip(player, combined());
        helper.assertTrue(player.getArmorValue() == 20 && player.getAttributeValue(Attributes.ARMOR_TOUGHNESS) == 12, "Combined set retains full Spectralite defense and toughness");
        helper.assertTrue(Math.abs(player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) - 0.4D) < 0.0001, "Combined set retains Spectralite knockback resistance");
        helper.assertTrue(HauntProtection.fullGarb(player) && GhostGarbPhasing.active(player) && HaintGarbJump.fullSet(player), "Combined armor qualifies for haunt protection, phasing, and jump");
        for (int i = 0; i < 4; i++) helper.assertTrue(new ItemStack(combined()[i]).getMaxDamage() == new ItemStack(spectralite()[i]).getMaxDamage(), "Combined durability matches the corresponding Spectralite piece");
        var ghost = ModEntities.GHOST.get().create(helper.getLevel());
        var event = new LivingIncomingDamageEvent(player, new DamageContainer(player.damageSources().mobAttack(ghost), 10));
        IntangibleCombatHandler.onIncomingDamage(event);
        helper.assertTrue(!event.isCanceled() && Math.abs(event.getAmount() - 8) < 0.0001, "Intangible attack still lands with Spectralite's 20 percent reduction");
        var pos = helper.absolutePos(new net.minecraft.core.BlockPos(5, 2, 5));
        player.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        helper.getLevel().setBlock(pos.above(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
        player.setAirSupply(100); player.baseTick();
        helper.assertTrue(player.getAirSupply() == 99, "Combined armor preserves the wall-breathing limit");
        player.discard(); ghost.discard(); helper.succeed();
    }
}
