package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.resources.RegistryOps;
import com.mojang.serialization.JsonOps;
import net.neoforged.neoforge.gametest.*;
import io.netty.buffer.Unpooled;
import java.util.List;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EctceteraGameTests {
    @GameTest(template = "deferred_items_test")
    public static void crafting_book_and_sequential_upgrades_consume_inputs_and_keep_name(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var menu = player.inventoryMenu;
        menu.getSlot(1).set(new ItemStack(Items.ROTTEN_FLESH));
        menu.getSlot(4).set(new ItemStack(Items.BOOK));
        menu.slotsChanged(menu.getSlot(1).container);
        var book = menu.getSlot(0).remove(1); menu.getSlot(0).onTake(player, book);
        helper.assertTrue(book.is(ModItems.BOOK_OF_ECTCETERA.get()) && EctceteraProgression.tier(book) == 0, "Original book and flesh recipe now makes the starter tier");
        helper.assertTrue(menu.getSlot(1).getItem().isEmpty() && menu.getSlot(4).getItem().isEmpty(), "Initial recipe consumes both inputs");
        book.set(DataComponents.CUSTOM_NAME, Component.literal("My Field Journal"));
        var unbreaking = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        book.enchant(unbreaking, 1);
        for (int tier = 0; tier < 3; tier++) {
            menu.getSlot(2).set(book.copy());
            menu.getSlot(3).set(new ItemStack(EctceteraProgression.upgradeIngredient(tier), 3));
            menu.slotsChanged(menu.getSlot(2).container);
            helper.assertTrue(menu.getSlot(0).hasItem() && menu.getSlot(0).mayPickup(player), "Upgrade works in a nonadjacent arrangement in the inventory grid");
            var upgraded = menu.getSlot(0).remove(1); menu.getSlot(0).onTake(player, upgraded);
            helper.assertTrue(EctceteraProgression.tier(upgraded) == tier + 1 && upgraded.getCount() == 1, "One craft advances exactly one tier");
            helper.assertTrue(upgraded.getHoverName().getString().equals("My Field Journal") && upgraded.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY).getLevel(unbreaking) == 1, "Book name and custom components survive each upgrade");
            helper.assertTrue(menu.getSlot(2).getItem().isEmpty() && menu.getSlot(3).getItem().getCount() == 2, "Book consumed into output, exactly one material consumed");
            menu.getSlot(3).set(ItemStack.EMPTY); book = upgraded;
        }
        player.discard(); helper.succeed();
    }

    @GameTest(template = "deferred_items_test")
    public static void upgrade_recipes_reject_skipping_repeating_and_extra_inputs(GameTestHelper helper) {
        var level = helper.getLevel();
        for (int tier = 0; tier <= 3; tier++) for (int ingredientTier = 0; ingredientTier < 3; ingredientTier++) {
            var input = CraftingInput.of(2, 1, List.of(EctceteraProgression.book(tier), new ItemStack(EctceteraProgression.upgradeIngredient(ingredientTier))));
            boolean matches = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).isPresent();
            helper.assertTrue(matches == (tier == ingredientTier), "Only the exact next ingredient matches; no skip, repeat, or downgrade");
        }
        for (Item wrong : new Item[]{ModItems.PURIFIED_ECTOPLASM_BUCKET.get(), ModItems.LIQUID_ECTOPLASM_BUCKET.get(), Items.BONE_MEAL}) {
            var input = CraftingInput.of(2, 1, List.of(EctceteraProgression.book(2), new ItemStack(wrong)));
            helper.assertTrue(level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).isEmpty(), "Fluid buckets and bone meal cannot substitute for solid upgrade ingredients");
        }
        var extra = CraftingInput.of(3, 1, List.of(EctceteraProgression.book(0), new ItemStack(Items.BONE), new ItemStack(Items.ROTTEN_FLESH)));
        helper.assertTrue(level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, extra, level).isEmpty(), "Extra ingredients rejected");
        var legacy = new ItemStack(ModItems.BOOK_OF_ECTCETERA.get());
        helper.assertTrue(EctceteraProgression.tier(legacy) == 3, "Unmarked pre-upgrade books retain all knowledge");
        var legacyInput = CraftingInput.of(2, 1, List.of(legacy, new ItemStack(Items.BONE)));
        helper.assertTrue(level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, legacyInput, level).isEmpty(), "Legacy full books cannot be downgraded");
        helper.succeed();
    }

    @GameTest(template = "deferred_items_test")
    public static void tiers_and_recipes_survive_saved_and_network_roundtrips(GameTestHelper helper) {
        var level = helper.getLevel();
        var ops = RegistryOps.create(JsonOps.INSTANCE, level.registryAccess());
        for (int tier = 0; tier <= 3; tier++) {
            var book = EctceteraProgression.book(tier);
            var encoded = ItemStack.CODEC.encodeStart(ops, book).getOrThrow();
            var restored = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
            helper.assertTrue(EctceteraProgression.tier(restored) == tier, "Saved book retains its per-stack tier");
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            try {
                ItemStack.STREAM_CODEC.encode(buffer, book);
                helper.assertTrue(EctceteraProgression.tier(ItemStack.STREAM_CODEC.decode(buffer)) == tier, "Client inventory receives the correct tier");
            } finally { buffer.release(); }
            if (tier == 3) continue;
            var input = CraftingInput.of(2, 1, List.of(book, new ItemStack(EctceteraProgression.upgradeIngredient(tier))));
            var recipe = (com.howierux.mortalexpansion.recipe.EctceteraUpgradeRecipe) level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElseThrow().value();
            helper.assertTrue(EctceteraProgression.tier(recipe.getIngredients().getFirst().getItems()[0]) == tier, "Recipe previews show the required input book tier");
            var codec = ModRecipeSerializers.ECTCETERA_UPGRADE.get().streamCodec();
            var recipeBuffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            try {
                codec.encode(recipeBuffer, recipe); var decoded = codec.decode(recipeBuffer);
                helper.assertTrue(decoded.matches(input, level) && EctceteraProgression.tier(decoded.assemble(input, level.registryAccess())) == tier + 1, "Recipe network roundtrip retains sequential crafting");
            } finally { recipeBuffer.release(); }
        }
        helper.succeed();
    }

    @GameTest(template = "deferred_items_test")
    public static void chapter_unlocks_are_cumulative_and_navigation_ends_with_next_step(GameTestHelper helper) {
        String[] endings = {"unlock_bone", "unlock_raw", "unlock_pure", "mastery"};
        List<EctceteraGuide.Chapter> previous = List.of();
        for (int tier = 0; tier <= 3; tier++) {
            var chapters = EctceteraGuide.chapters(tier);
            helper.assertTrue(chapters.containsAll(previous) && chapters.getLast().key().equals(endings[tier]), "Each upgrade retains known chapters and ends with appropriate guidance");
            int page = 1 + EctceteraGuide.contentsPages(chapters);
            for (int index = 0; index < chapters.size(); index++) {
                helper.assertTrue(chapters.get(index).tier() <= tier && EctceteraGuide.firstPage(chapters, index) == page, "Filtered contents links reach the correct chapter and expose no locked entries");
                page += chapters.get(index).pages();
            }
            previous = chapters;
        }
        helper.assertTrue(EctceteraGuide.chapters(0).stream().noneMatch(ch -> ch.key().equals("graves")), "Starter cannot read the Bone chapters");
        helper.assertTrue(EctceteraGuide.chapters(1).stream().noneMatch(ch -> ch.key().equals("cauldrons")), "Bone book cannot read Raw Ectoplasm chapters");
        helper.assertTrue(EctceteraGuide.chapters(2).stream().noneMatch(ch -> ch.key().equals("wards")), "Raw book cannot read purified chapters");
        helper.succeed();
    }
}
