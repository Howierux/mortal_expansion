package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GhostGarbGameTests {
    private static ServerPlayer equipped(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.GHOST_GARB_HELMET.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.GHOST_GARB_CHESTPLATE.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.GHOST_GARB_LEGGINGS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.GHOST_GARB_BOOTS.get()));
        for (int i = 0; i < 61; i++) player.tick();
        player.doTick();
        return player;
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_damage_and_set_rules(GameTestHelper helper) {
        var player = equipped(helper);
        helper.assertTrue(player.getArmorValue() == 7, "Full leather defense");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).getMaxDamage() == 165
                && player.getItemBySlot(EquipmentSlot.CHEST).getMaxDamage() == 240
                && player.getItemBySlot(EquipmentSlot.LEGS).getMaxDamage() == 225
                && player.getItemBySlot(EquipmentSlot.FEET).getMaxDamage() == 195, "Iron durability");
        var zombie = net.minecraft.world.entity.EntityType.ZOMBIE.create(helper.getLevel());
        var ghost = ModEntities.GHOST.get().create(helper.getLevel());
        helper.assertFalse(player.hurt(player.damageSources().mobAttack(zombie), 4), "Ordinary melee blocked");
        helper.assertFalse(player.hurt(player.damageSources().inFire(), 4), "Ordinary environmental damage blocked");
        helper.assertTrue(player.hurt(player.damageSources().mobAttack(ghost), 4), "Intangible attackers hurt garb wearers");
        player.invulnerableTime = 0;
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.assertFalse(GhostGarbPhasing.active(player), "Partial set has no intangibility");
        helper.assertTrue(player.hurt(player.damageSources().mobAttack(zombie), 4), "Removing a piece restores normal damage");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_phases_walls_but_keeps_floors_and_barriers(GameTestHelper helper) {
        var player = equipped(helper);
        var start = helper.absolutePos(new BlockPos(5, 2, 5));
        for (int x = 4; x <= 8; x++) for (int z = 4; z <= 6; z++) helper.getLevel().setBlock(new BlockPos(x + start.getX() - 5, start.getY() - 1, z + start.getZ() - 5), Blocks.STONE.defaultBlockState(), 3);
        player.moveTo(start.getX() + .5, start.getY(), start.getZ() + .5, 0, 0);
        helper.getLevel().setBlock(start.east(), Blocks.STONE.defaultBlockState(), 3);
        helper.getLevel().setBlock(start.east().above(), Blocks.STONE.defaultBlockState(), 3);
        Vec3 before = player.position();
        player.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(1, -1, 0));
        helper.assertTrue(Math.abs(player.getX() - before.x - 1) < .01 && Math.abs(player.getY() - before.y) < .01, "Wall phased while floor collision retained");
        helper.getLevel().setBlock(start.east(2), com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LOG.get().defaultBlockState(), 3);
        helper.getLevel().setBlock(start.east(2).above(), com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LOG.get().defaultBlockState(), 3);
        before = player.position();
        player.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(2, 0, 0));
        helper.assertTrue(player.getX() - before.x < 1, "Spectral-solid barrier blocks swept movement");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_blocks_haunt_without_blocking_benefits(GameTestHelper helper) {
        var player = equipped(helper);
        helper.assertFalse(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true), "Negative haunt blocked");
        helper.assertTrue(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 100, 0, false), "Beneficial effect allowed");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.assertTrue(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true), "Incomplete set loses protection");
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.GHOST_GARB_HELMET.get()));
        HauntProtection.clearNegative(player);
        helper.assertFalse(player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN), "Equipping cleans existing haunt-owned effects");
        helper.assertTrue(player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED), "Beneficial effect preserved");
        player.discard(); helper.succeed();
    }
}



