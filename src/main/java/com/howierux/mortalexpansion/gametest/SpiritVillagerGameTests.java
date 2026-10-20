package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.entity.mob.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritVillagerGameTests {
    private static GraveBlockEntity grave(GameTestHelper helper, String name) {
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 4));
        helper.getLevel().setBlock(pos, ModBlocks.GRAVE.get().defaultBlockState(), 3);
        var grave = (GraveBlockEntity)helper.getLevel().getBlockEntity(pos);
        var player = helper.makeMockServerPlayerInLevel();
        grave.beginNaming(player);
        helper.assertTrue(grave.trySetInscription(player, name), "Inscription accepted");
        player.discard(); return grave;
    }
    private static Villager villager(GameTestHelper helper, BlockPos pos) {
        var villager = EntityType.VILLAGER.create(helper.getLevel());
        villager.moveTo(pos.getX() + 1.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        villager.setNoAi(true);
        villager.setCustomName(Component.literal("Test Villager"));
        villager.setVillagerData(villager.getVillagerData().setType(VillagerType.DESERT));
        villager.getInventory().setItem(0, new ItemStack(Items.BREAD, 3));
        helper.getLevel().addFreshEntity(villager); return villager;
    }
    @GameTest(template = "deferred_items_test")
    public static void expertise_unlocks_tiers_and_survives_reload(GameTestHelper helper) {
        var spirit = ModEntities.SPIRIT.get().create(helper.getLevel());
        helper.assertTrue(spirit.getOffers().size() == 4 && spirit.getExpertise() == 1 && spirit.showProgressBar(), "Novice only has starter trades and expertise bar");
        int[] trades = {5, 12, 8, 4, 3};
        int[] offerIndices = {0, 4, 8, 12, 13};
        for (int stage = 0; stage < trades.length; stage++) {
            var offer = spirit.getOffers().get(offerIndices[stage]);
            for (int n = 0; n < trades[stage]; n++) {
                helper.assertFalse(offer.isOutOfStock(), "Progression uses available stock");
                var a = offer.getCostA().copy(); var b = offer.getCostB().copy();
                helper.assertTrue(offer.take(a, b) && a.isEmpty() && b.isEmpty(), "Full payment consumed before XP is awarded");
                spirit.notifyTrade(offer);
            }
            int expected = new int[]{2, 3, 4, 4, 5}[stage];
            helper.assertTrue(spirit.getExpertise() == expected && spirit.getOffers().size() == expected * 4, "Only earned tiers unlock; previous offers are not duplicated");
        }
        helper.assertTrue(spirit.getVillagerXp() == 255 && spirit.getExpertise() == 5, "Vanilla Master XP threshold");
        var saved = spirit.saveWithoutId(new CompoundTag());
        var loaded = ModEntities.SPIRIT.get().create(helper.getLevel()); loaded.load(saved);
        helper.assertTrue(loaded.getExpertise() == 5 && loaded.getVillagerXp() == 255 && loaded.getOffers().size() == 20
                && loaded.getOffers().get(12).isOutOfStock(), "Tier, XP, unlocks, and limited stock persist");
        var legacy = saved.copy(); legacy.remove("SpiritTradeCatalog"); legacy.remove("SpiritExpertise"); legacy.remove("SpiritTradeXp");
        loaded.load(legacy);
        helper.assertTrue(loaded.getExpertise() == 1 && loaded.getVillagerXp() == 0 && loaded.getOffers().size() == 4,
                "Legacy flat catalog becomes balanced novice trades");
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void assembly_trades_retain_recipe_material_costs(GameTestHelper helper) {
        var offers = new net.minecraft.world.item.trading.MerchantOffers(); SpiritTrades.populate(offers, 5);
        for (var offer : offers) {
            var result = offer.getResult();
            if (result.getItem() instanceof net.minecraft.world.item.ArmorItem) {
                var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem());
                var recipe = helper.getLevel().getRecipeManager().byKey(id).orElseThrow().value();
                long materialCost = recipe.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty()).count();
                helper.assertTrue(offer.getCostB().getCount() >= materialCost && offer.getCostA().is(Items.EMERALD),
                        "Armor trade consumes at least the real crafting ingredient count plus assembly fee");
                helper.assertTrue(recipe.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty())
                        .allMatch(ingredient -> ingredient.test(offer.getCostB())), "Trade material matches recipe thread");
            }
            if (result.is(ModItems.ECTOTHREAD.get()) || result.is(ModItems.PURIFIED_ECTOTHREAD.get())) {
                var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem());
                var recipe = helper.getLevel().getRecipeManager().byKey(id).orElseThrow().value();
                helper.assertTrue(result.getCount() == recipe.getResultItem(helper.getLevel().registryAccess()).getCount()
                        && offer.getCostB().getCount() == 1 && recipe.getIngredients().stream().anyMatch(ingredient -> ingredient.test(offer.getCostB())),
                        "Thread trade retains recipe output and matching ectoplasm core");
                helper.assertTrue(offer.getCostA().is(Items.EMERALD) && offer.getCostA().getCount() >= 2, "Fee replaces the eight string input");
            }
        }
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void engraved_grave_claims_one_eligible_villager(GameTestHelper helper) {
        var grave = grave(helper, "Villager");
        var first = villager(helper, grave.getBlockPos());
        var second = villager(helper, grave.getBlockPos());
        SpiritWorkstations.tick(helper.getLevel(), grave);
        helper.assertTrue(grave.getSpiritWorker() != null, "Workstation claims a Spirit");
        var spirit = (SpiritEntity)helper.getLevel().getEntity(grave.getSpiritWorker());
        helper.assertTrue(spirit.graveCreated() && spirit.getCustomName().getString().equals("Test Villager"), "Converted villager name and provenance retained");
        helper.assertTrue(spirit.getInventory().getItem(0).is(Items.BREAD) && spirit.getInventory().getItem(0).getCount() == 3, "Inventory preserved");
        SpiritWorkstations.tick(helper.getLevel(), grave);
        helper.assertTrue(first.isRemoved() != second.isRemoved(), "Exactly one villager per workstation");
        helper.getLevel().setBlock(grave.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(spirit.isRemoved(), "Breaking grave immediately reverts its worker");
        var villagers = helper.getLevel().getEntitiesOfClass(Villager.class, new net.minecraft.world.phys.AABB(grave.getBlockPos()).inflate(4));
        helper.assertTrue(villagers.size() == 2, "Both villagers present after reversion");
        for (var v : villagers) helper.assertTrue(v.getVillagerData().getProfession() == VillagerProfession.NONE
                && v.getVillagerData().getType() == VillagerType.DESERT && v.getInventory().getItem(0).getCount() == 3, "Reversion preserves type and inventory, returns jobless");
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void spirit_trading_uses_vanilla_menu_and_payment(GameTestHelper helper) {
        var spirit = ModEntities.SPIRIT.get().create(helper.getLevel());
        var pos = helper.absolutePos(new BlockPos(5, 2, 5));
        spirit.moveTo(pos.getX(), pos.getY(), pos.getZ()); helper.getLevel().addFreshEntity(spirit);
        var player = helper.makeMockServerPlayerInLevel();
        player.setPos(spirit.position());
        helper.assertTrue(spirit.mobInteract(player, InteractionHand.MAIN_HAND).consumesAction(), "Options interaction accepted");
        helper.assertTrue(!(player.containerMenu instanceof MerchantMenu), "Right click does not skip the options");
        helper.assertTrue(com.howierux.mortalexpansion.quest.SpiritInteraction.choose(player,com.howierux.mortalexpansion.quest.SpiritInteraction.token(player),0), "Trade button is accepted");
        helper.assertTrue(player.containerMenu instanceof MerchantMenu, "Vanilla trading screen opens");
        var menu = (MerchantMenu)player.containerMenu;
        for (int i = 0; i < 5; i++) spirit.notifyTrade(spirit.getOffers().getFirst());
        helper.assertTrue(spirit.getExpertise() == 2 && spirit.getVillagerXp() == 10, "Trades unlock Apprentice expertise");
        menu.setSelectionHint(4);
        menu.getSlot(0).set(new ItemStack(Items.EMERALD, 2));
        menu.getSlot(1).set(new ItemStack(ModItems.RAW_ECTOPLASM.get()));
        helper.assertTrue(menu.getSlot(2).getItem().is(ModItems.ECTOTHREAD.get()) && menu.getSlot(2).getItem().getCount() == 8, "Trade preview matches price and output");
        menu.quickMoveStack(player, 2);
        helper.assertTrue(menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty() && spirit.getOffers().get(4).getUses() == 1, "Trade consumes emeralds and uses stock exactly once");
        int threads = 0;
        for (var stack : player.getInventory().items) if (stack.is(ModItems.ECTOTHREAD.get())) threads += stack.getCount();
        helper.assertTrue(threads == 8 && spirit.getTarget() == null, "Player receives output and merchant stays passive");
        var saved = spirit.saveWithoutId(new CompoundTag());
        var restored = ModEntities.SPIRIT.get().create(helper.getLevel()); restored.load(saved);
        helper.assertTrue(restored.getOffers().get(4).getUses() == 1 && restored.getExpertise() == 2 && restored.getVillagerXp() == 15 && !restored.graveCreated(), "Natural Spirit trade stock survives load without becoming grave-created");
        helper.assertTrue(SpiritWorkstations.revert(helper.getLevel(), restored) == null, "Natural Spirits cannot become regular villagers");
        player.closeContainer(); player.discard(); spirit.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void spirit_provenance_load_and_workstation_rules(GameTestHelper helper) {
        var grave = grave(helper, "Other Name");
        var source = villager(helper, grave.getBlockPos());
        helper.assertTrue(SpiritWorkstations.convert(helper.getLevel(), grave, source) == null && !source.isRemoved(), "Other inscriptions are not workstations");
        var tag = grave.saveWithoutMetadata(helper.getLevel().registryAccess());
        tag.putString("Inscription", "vIlLaGeR"); grave.loadWithComponents(tag, helper.getLevel().registryAccess());
        source.setAge(-100);
        helper.assertTrue(SpiritWorkstations.convert(helper.getLevel(), grave, source) == null, "Baby villagers do not take the workstation");
        source.setAge(0); source.setVillagerData(source.getVillagerData().setProfession(VillagerProfession.NITWIT));
        helper.assertTrue(SpiritWorkstations.convert(helper.getLevel(), grave, source) == null, "Nitwits retain their identity");
        source.setVillagerData(source.getVillagerData().setProfession(VillagerProfession.NONE));
        var spirit = SpiritWorkstations.convert(helper.getLevel(), grave, source);
        var saved = spirit.saveWithoutId(new CompoundTag()); spirit.discard();
        var loaded = ModEntities.SPIRIT.get().create(helper.getLevel()); loaded.load(saved); helper.getLevel().addFreshEntity(loaded);
        helper.assertTrue(loaded.graveCreated() && loaded.workstation().equals(grave.getBlockPos()) && loaded.workstationLevel(helper.getLevel()) == helper.getLevel() && SpiritWorkstations.validate(helper.getLevel(), loaded), "Ownership and original villager survive save/load");
        var graveSaved = grave.saveWithoutMetadata(helper.getLevel().registryAccess());
        helper.assertTrue(graveSaved.hasUUID("SpiritWorker") && graveSaved.getUUID("SpiritWorker").equals(loaded.getUUID()), "Grave owner claim persists");
        helper.getLevel().setBlock(grave.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(loaded.isRemoved(), "Reloaded Spirit still reverts when grave breaks");
        helper.succeed();
    }
}
