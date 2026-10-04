package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.respawn.SpiritJourneyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Headless regressions, disabled by NeoForge in production. */
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeferredDeathItemsGameTests {
    private static ServerPlayer player(GameTestHelper helper, boolean selfGrave) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        Vec3 death = Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(3, 2, 3)));
        player.moveTo(death.x, death.y, death.z, 0, 0);
        if (selfGrave) {
            BlockPos pos = helper.absolutePos(new BlockPos(10, 1, 10));
            helper.getLevel().setBlockAndUpdate(pos, ModBlocks.GRAVE.get().defaultBlockState());
            GraveBlockEntity grave = (GraveBlockEntity) helper.getLevel().getBlockEntity(pos);
            grave.beginNaming(player);
            helper.assertTrue(grave.trySetInscription(player, player.getGameProfile().getName()), "Self-Grave setup");
        }
        ItemStack diamonds = new ItemStack(Items.DIAMOND, 64);
        diamonds.set(DataComponents.CUSTOM_NAME, Component.literal("Deferred item components"));
        player.getInventory().setItem(4, diamonds);
        player.getInventory().setItem(38, new ItemStack(Items.DIAMOND_CHESTPLATE));
        player.getInventory().setItem(40, new ItemStack(Items.SHIELD));
        return player;
    }
    private static void die(ServerPlayer player) {
        player.setHealth(0);
        player.die(player.damageSources().genericKill());
    }
    private static void withKeepInventory(GameTestHelper helper, boolean enabled, Runnable test) {
        var rule = helper.getLevel().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY);
        boolean previous = rule.get();
        rule.set(enabled, helper.getLevel().getServer());
        try { test.run(); }
        finally { rule.set(previous, helper.getLevel().getServer()); }
    }
    @GameTest(template = "deferred_items_test", timeoutTicks = 100)
    public static void choice_holds_items_and_survives_player_reload(GameTestHelper helper) {
        withKeepInventory(helper, false, () -> {
            ServerPlayer dead = player(helper, true);
            ItemStack expected = dead.getInventory().getItem(4).copy();
            ItemStack cursed = new ItemStack(Items.DIAMOND_SWORD);
            cursed.enchant(helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(Enchantments.VANISHING_CURSE), 1);
            dead.getInventory().setItem(5, cursed);
            die(dead);
            helper.assertItemEntityNotPresent(Items.DIAMOND);
            helper.assertItemEntityNotPresent(Items.DIAMOND_CHESTPLATE);
            CompoundTag storage = dead.getData(ModAttachments.DEFERRED_DEATH_ITEMS).copy();
            helper.assertTrue(storage.getList("Pending", 10).size() == 1, "Actual death items await a choice");
            CompoundTag savedPlayer = dead.saveWithoutId(new CompoundTag());
            dead.setData(ModAttachments.DEFERRED_DEATH_ITEMS, new CompoundTag());
            dead.load(savedPlayer);
            helper.assertTrue(dead.getData(ModAttachments.DEFERRED_DEATH_ITEMS).equals(storage), "Pending items survive player save/load");
            SpiritJourneyHandler.choose(dead);
            ServerPlayer spirit = dead.connection.player;
            helper.assertTrue(spirit.isSpectator(), "Spirit Journey enters spectator mode");
            helper.assertTrue(spirit.getInventory().isEmpty(), "Inventory was not duplicated into spectator slots");
            CompoundTag held = spirit.getData(ModAttachments.DEFERRED_DEATH_ITEMS);
            helper.assertTrue(held.getList("Pending", 10).isEmpty(), "Pending choice is resolved");
            ListTag drops = held.getList("Held", 10).getCompound(0).getList("Drops", 10);
            int diamondCount = 0;
            for (int i = 0; i < drops.size(); i++) {
                ItemStack item = ItemStack.parseOptional(helper.getLevel().registryAccess(), drops.getCompound(i).getCompound("Item"));
                helper.assertFalse(item.is(Items.DIAMOND_SWORD), "Vanishing item remains vanished");
                if (item.is(Items.DIAMOND)) {
                    helper.assertTrue(ItemStack.matches(item, expected), "Counts and custom components retained");
                    diamondCount += item.getCount();
                }
            }
            helper.assertTrue(diamondCount == 64, "Exactly one original stack held");
            helper.assertItemEntityNotPresent(Items.DIAMOND);
            spirit.discard();
            helper.succeed();
        });
    }
    @GameTest(template = "deferred_items_test", timeoutTicks = 100)
    public static void ordinary_respawn_drops_at_original_death_site(GameTestHelper helper) {
        withKeepInventory(helper, false, () -> {
            ServerPlayer dead = player(helper, true);
            die(dead);
            helper.assertItemEntityNotPresent(Items.DIAMOND);
            ServerPlayer alive = dead.server.getPlayerList().respawn(dead, false, Entity.RemovalReason.KILLED);
            alive.connection.player = alive;
            helper.assertItemEntityCountIs(Items.DIAMOND, new BlockPos(3, 2, 3), 3, 64);
            helper.assertItemEntityCountIs(Items.DIAMOND, new BlockPos(10, 2, 10), 2, 0);
            helper.assertTrue(alive.getData(ModAttachments.DEFERRED_DEATH_ITEMS).isEmpty(), "Ordinary choice clears emitted escrow");
            helper.assertTrue(alive.getInventory().isEmpty(), "Normal respawn does not also keep items");
            alive.discard();
            helper.succeed();
        });
    }
    @GameTest(template = "deferred_items_test", timeoutTicks = 100)
    public static void journey_corpse_persists_and_recovers_once(GameTestHelper helper) {
        withKeepInventory(helper, false, () -> {
            ServerPlayer dead = player(helper, true);
            Vec3 death = dead.position();
            die(dead);
            SpiritJourneyHandler.choose(dead);
            ServerPlayer spirit = dead.connection.player;
            var id = java.util.UUID.fromString(spirit.getData(ModAttachments.SPIRIT_JOURNEY_CORPSE));
            var corpse = (com.howierux.mortalexpansion.entity.PlayerCorpseEntity) helper.getLevel().getEntity(id);
            helper.assertTrue(corpse != null && corpse.position().distanceToSqr(death) < 0.01,
                    "Journey leaves a body at the death site");
            var saved = corpse.saveWithoutId(new CompoundTag());
            corpse.load(saved);
            helper.assertTrue(corpse.owner().orElseThrow().equals(spirit.getUUID()), "Corpse owner survives save/load");
            spirit.moveTo(death.x, death.y, death.z, 0, 0);
            spirit.setData(ModAttachments.SPIRIT_JOURNEY_CORPSE, "");
            corpse.tick();
            helper.assertTrue(spirit.isSpectator() && !corpse.isRemoved(), "Unlinked spectators cannot recover the body");
            spirit.setData(ModAttachments.SPIRIT_JOURNEY_CORPSE, id.toString());
            corpse.tick();
            helper.assertTrue(!spirit.isSpectator() && corpse.isRemoved(), "Returning revives the owner and consumes the body");
            helper.assertTrue(spirit.getData(ModAttachments.DEFERRED_DEATH_ITEMS).getList("Held", 10).isEmpty(),
                    "Recovered escrow is cleared");
            int count = 0;
            for (ItemStack stack : spirit.getInventory().items) if (stack.is(Items.DIAMOND)) count += stack.getCount();
            helper.assertTrue(count == 64, "Exactly one original stack restored");
            com.howierux.mortalexpansion.respawn.DeferredDeathItems.recoverHeld(spirit);
            int after = 0;
            for (ItemStack stack : spirit.getInventory().items) if (stack.is(Items.DIAMOND)) after += stack.getCount();
            helper.assertTrue(after == 64, "Repeated recovery cannot duplicate items");
            spirit.discard();
            helper.succeed();
        });
    }
    @GameTest(template = "deferred_items_test", timeoutTicks = 100)
    public static void no_self_grave_uses_normal_death_drops(GameTestHelper helper) {
        withKeepInventory(helper, false, () -> {
            ServerPlayer dead = player(helper, false);
            die(dead);
            helper.assertItemEntityCountIs(Items.DIAMOND, new BlockPos(3, 2, 3), 3, 64);
            helper.assertTrue(dead.getData(ModAttachments.DEFERRED_DEATH_ITEMS).isEmpty(), "No eligible choice means no escrow");
            dead.discard();
            helper.succeed();
        });
    }
    @GameTest(template = "deferred_items_test", timeoutTicks = 100)
    public static void keep_inventory_stays_vanilla(GameTestHelper helper) {
        withKeepInventory(helper, true, () -> {
            ServerPlayer dead = player(helper, true);
            die(dead);
            helper.assertTrue(dead.getData(ModAttachments.DEFERRED_DEATH_ITEMS).isEmpty(), "Keep-inventory creates no duplicate escrow");
            SpiritJourneyHandler.choose(dead);
            ServerPlayer spirit = dead.connection.player;
            helper.assertTrue(spirit.getInventory().getItem(4).getCount() == 64, "Keep-inventory is preserved");
            helper.assertItemEntityNotPresent(Items.DIAMOND);
            spirit.discard();
            helper.succeed();
        });
    }
}
