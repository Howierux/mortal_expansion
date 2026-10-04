package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.haunt.PurificationWardTracker;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.mixin.HauntFurnaceAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HauntEffectsGameTests {
    private static ServerPlayer player(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        Vec3 at = Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(7, 2, 7)));
        player.moveTo(at.x, at.y, at.z, 0, 0);
        player.getInventory().clearContent();
        return player;
    }
    private static void floor(GameTestHelper helper) {
        for (int x = 0; x < 15; x++) for (int z = 0; z < 15; z++) {
            helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
            for (int y = 2; y <= 12; y++) helper.setBlock(new BlockPos(x, y, z), Blocks.AIR);
        }
    }
    @GameTest(template = "deferred_items_test")
    public static void personal_tier_controls_events_and_cooldown(GameTestHelper helper) {
        var player = player(helper);
        HauntManager.setTier(helper.getLevel(), player.blockPosition(), HauntTier.ELDRITCH);
        PlayerHauntManager.setTier(player, HauntTier.VOID);
        helper.assertFalse(HauntEventController.tryEffect(player, HauntEffectKind.HIT), "World Haunt cannot trigger personal effects");
        PlayerHauntManager.setTier(player, HauntTier.NATURAL);
        helper.assertFalse(HauntEventController.tryEffect(player, HauntEffectKind.SHOVE), "Minimum personal tier respected");
        helper.assertTrue(HauntEventController.tryEffect(player, HauntEffectKind.HIT), "Natural sound is eligible");
        helper.assertFalse(HauntEventController.tryEffect(player, HauntEffectKind.HIT), "Second event blocked by cooldown");
        var state = HauntProtection.state(player);
        helper.assertTrue(state.getLong("CooldownUntil") == HauntProtection.now(player) + 1200, "Sixty-second cooldown");
        CompoundTag saved = player.saveWithoutId(new CompoundTag());
        HauntProtection.state(player, new CompoundTag());
        player.load(saved);
        helper.assertTrue(HauntProtection.state(player).equals(state), "Cooldown persists in player save/load");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void holy_water_only_cleanses_owned_effects_once_per_minute(GameTestHelper helper) {
        var player = player(helper);
        PlayerHauntManager.setTier(player, HauntTier.DEAD_RISING);
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
        helper.assertTrue(HauntProtection.apply(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true), "Haunt Slowness applied");
        int before = PlayerHauntManager.getValue(player);
        HauntProtection.bless(player);
        helper.assertFalse(player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "Owned effect removed");
        helper.assertTrue(player.getEffect(MobEffects.WEAKNESS).getAmplifier() == 2, "Unrelated stronger potion retained");
        helper.assertTrue(HauntProtection.protectedPlayer(player), "Sixty-second blessing active");
        helper.assertFalse(HauntProtection.apply(player, MobEffects.DIG_SLOWDOWN, 100, 0, true), "Blessing blocks new negative effects");
        HauntProtection.bless(player);
        helper.assertTrue(PlayerHauntManager.getValue(player) == before - 1, "Repeated contact does not repeatedly cleanse");
        helper.assertTrue(HauntProtection.state(player).getLong("BlessedUntil") == HauntProtection.now(player) + 1200, "Blessing length");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void external_potion_replacement_and_wards_are_preserved(GameTestHelper helper) {
        var player = player(helper);
        HauntProtection.apply(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 3));
        HauntProtection.bless(player);
        helper.assertTrue(player.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 3, "External replacement not erased");
        HauntProtection.state(player, new CompoundTag());
        player.removeAllEffects();
        BlockPos ward = helper.absolutePos(new BlockPos(2, 1, 2));
        PurificationWardTracker.add(helper.getLevel(), ward);
        try {
            helper.assertTrue(HauntProtection.protectedPlayer(player), "Ward coverage suppresses negative events");
            helper.assertFalse(HauntEffects.perform(player, HauntEffectKind.SLOW), "Ward blocks negative effect");
            helper.assertTrue(HauntEffects.perform(player, HauntEffectKind.GOOD_POTION), "Beneficial effects remain allowed");
        } finally { PurificationWardTracker.remove(helper.getLevel(), ward); }
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void inventory_shuffle_preserves_reserved_slots_and_components(GameTestHelper helper) {
        var player = player(helper);
        ItemStack named = new ItemStack(Items.DIAMOND, 17);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("Do not lose me"));
        ItemStack other = new ItemStack(Items.EMERALD, 31);
        player.getInventory().setItem(9, named.copy()); player.getInventory().setItem(10, other.copy());
        player.getInventory().setItem(0, new ItemStack(Items.GOLD_INGOT, 64));
        player.getInventory().setItem(38, new ItemStack(Items.DIAMOND_CHESTPLATE));
        player.getInventory().setItem(40, new ItemStack(Items.SHIELD));
        helper.assertTrue(HauntEffects.shuffle(player), "Two occupied main slots swap");
        helper.assertTrue(ItemStack.matches(player.getInventory().getItem(10), named), "Named stack preserved");
        helper.assertTrue(ItemStack.matches(player.getInventory().getItem(9), other), "Other stack preserved");
        helper.assertTrue(player.getInventory().getItem(0).getCount() == 64
                && player.getInventory().getItem(38).is(Items.DIAMOND_CHESTPLATE)
                && player.getInventory().getItem(40).is(Items.SHIELD), "Reserved slots untouched");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void conversions_conserve_items_and_share_chunk_cooldown(GameTestHelper helper) {
        var player = player(helper);
        var data = HauntEventData.get(helper.getLevel());
        data.prune(Long.MAX_VALUE);
        for (int i = 0; i < 36; i++) player.getInventory().setItem(i, new ItemStack(Items.DIAMOND, 64));
        player.getInventory().setItem(9, new ItemStack(Items.COBBLESTONE, 2));
        helper.assertFalse(HauntEffects.convert(player, false), "Full inventory cannot lose input");
        helper.assertTrue(player.getInventory().getItem(9).getCount() == 2, "Blocked input retained");
        player.getInventory().setItem(9, new ItemStack(Items.COBBLESTONE, 1));
        helper.assertTrue(HauntEffects.convert(player, false), "Single input frees its own result slot");
        helper.assertTrue(player.getInventory().getItem(9).is(ModBlocks.GRAVESTONE.get().asItem()), "Exactly one result");
        player.getInventory().setItem(10, new ItemStack(Items.OAK_PLANKS, 1));
        helper.assertFalse(HauntEffects.convert(player, true), "Second benefit blocked by shared chunk cooldown");
        var reloaded = HauntEventData.load(data.save(new CompoundTag(), helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertFalse(reloaded.chunkReady(new ChunkPos(player.blockPosition()).toLong(), HauntProtection.now(player)), "World cooldown persists");
        data.prune(Long.MAX_VALUE);
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void furnace_completes_one_recipe_without_losing_experience(GameTestHelper helper) {
        BlockPos pos = new BlockPos(5, 2, 5);
        helper.setBlock(pos, Blocks.FURNACE);
        var furnace = (AbstractFurnaceBlockEntity) helper.getBlockEntity(pos);
        furnace.setItem(0, new ItemStack(Items.RAW_IRON, 2));
        furnace.setItem(1, new ItemStack(Items.COAL, 1));
        AbstractFurnaceBlockEntity.serverTick(helper.getLevel(), furnace.getBlockPos(), furnace.getBlockState(), furnace);
        helper.assertTrue(HauntEffects.finishFurnace(helper.getLevel(), furnace), "Active valid furnace boosted");
        helper.assertTrue(furnace.getItem(0).getCount() == 1 && furnace.getItem(2).is(Items.IRON_INGOT)
                && furnace.getItem(2).getCount() == 1, "One operation only");
        CompoundTag saved = furnace.saveWithFullMetadata(helper.getLevel().registryAccess());
        helper.assertFalse(saved.getCompound("RecipesUsed").isEmpty(), "Vanilla recipe XP ledger recorded");
        furnace.setItem(2, new ItemStack(Items.IRON_INGOT, 64));
        int before = furnace.getItem(0).getCount();
        helper.assertFalse(HauntEffects.finishFurnace(helper.getLevel(), furnace), "Full output is ineligible");
        helper.assertTrue(furnace.getItem(0).getCount() == before, "Blocked input not consumed");
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void shove_checks_lava_walls_and_falls(GameTestHelper helper) {
        floor(helper);
        var player = player(helper);
        player.setOnGround(true); player.setYRot(0);
        Vec3 original = player.position();
        BlockPos behind = new BlockPos(7, 2, 6);
        helper.setBlock(behind, Blocks.STONE);
        helper.assertFalse(HauntEffects.shove(player), "Wall prevents shove");
        helper.setBlock(behind, Blocks.LAVA);
        helper.assertFalse(HauntEffects.shove(player), "Lava prevents shove");
        helper.setBlock(behind, Blocks.AIR);
        helper.setBlock(behind.below(), Blocks.AIR);
        helper.assertFalse(HauntEffects.shove(player), "Missing floor prevents shove");
        helper.setBlock(behind.below(), Blocks.STONE);
        helper.assertTrue(HauntEffects.shove(player), "Safe one-block displacement succeeds");
        helper.assertTrue(Math.abs(player.position().distanceTo(original) - 1) < 0.001, "One block exactly");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void deferred_well_events_save_and_deliver_at_loaded_wells(GameTestHelper helper) {
        floor(helper);
        var player = player(helper);
        var data = HauntEventData.get(helper.getLevel());
        while (data.pendingGhosts() > 0) data.deliveredGhost();
        player.setPos(player.getX(), player.getY() + 200, player.getZ());
        helper.assertTrue(HauntEffects.nearestWell(player) == null, "No loaded well within 100 blocks");
        helper.assertTrue(HauntEffects.perform(player, HauntEffectKind.GHOST), "Missing well holds trigger");
        helper.assertTrue(data.pendingGhosts() == 1, "Exactly one pending event");
        var reloaded = HauntEventData.load(data.save(new CompoundTag(), helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(reloaded.pendingGhosts() == 1, "Deferred selection survives world save/load");
        Vec3 at = Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(7, 2, 7)));
        player.moveTo(at.x, at.y, at.z, 0, 0);
        helper.setBlock(new BlockPos(7, 4, 7), ModBlocks.SPIRITWELL.get());
        var well = HauntEffects.nearestWell(player);
        helper.assertTrue(well != null, "Loaded nearby well found without a new roll");
        helper.assertTrue(HauntEffects.spawnGhost(player, well), "Held event has a safe delivery location");
        data.deliveredGhost();
        helper.assertTrue(data.pendingGhosts() == 0, "Delivery consumes only one held event");
        for (var ghost : helper.getLevel().getEntitiesOfClass(com.howierux.mortalexpansion.entity.mob.GhostEntity.class,
                player.getBoundingBox().inflate(16), mob -> mob.getPersistentData().getBoolean(HauntEffects.EVENT_MOB))) ghost.discard();
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void passive_spirit_does_not_repeat_while_nearby(GameTestHelper helper) {
        floor(helper);
        var player = player(helper);
        helper.assertTrue(HauntEffects.spawnSpirit(player), "Passive appearance spawned");
        var spirits = helper.getLevel().getEntitiesOfClass(com.howierux.mortalexpansion.entity.mob.SpiritEntity.class,
                player.getBoundingBox().inflate(32), spirit -> spirit.getPersistentData().getBoolean(HauntEffects.EVENT_MOB));
        helper.assertTrue(spirits.size() == 1 && spirits.get(0).distanceTo(player) <= 5, "One passive Spirit within five blocks");
        helper.assertFalse(HauntEffects.spawnSpirit(player), "Nearby event-created Spirit prevents repeats");
        spirits.forEach(net.minecraft.world.entity.Entity::discard);
        player.discard(); helper.succeed();
    }
}
