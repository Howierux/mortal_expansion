package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DemonResistanceGameTests {
    @GameTest(template="deferred_items_test")
    public static void demons_resist_spectral_weapons_but_not_other_magic(GameTestHelper h) {
        var player = h.makeMockServerPlayerInLevel();
        var level = h.getLevel();
        var enchanted = new ItemStack(Items.DIAMOND_SWORD);
        enchanted.set(ModDataComponents.DEMI_TANGIBLE.get(), true);
        var legacy = new ItemStack(Items.IRON_SWORD);
        legacy.enchant(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DEMI_TANGIBLE), 1);
        var weapons = new java.util.ArrayList<ItemStack>();
        for (var holder : level.registryAccess().registryOrThrow(Registries.ITEM)
                .getTagOrEmpty(com.howierux.mortalexpansion.combat.ModCombatItemTags.DEMI_TANGIBLE))
            weapons.add(new ItemStack(holder));
        h.assertFalse(weapons.isEmpty(), "Built-in demi-tangible weapon tag is populated");
        weapons.add(enchanted); weapons.add(legacy);
        for (var weapon : weapons) {
            player.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            var demon = ModEntities.DEMON.get().create(level);
            var ghost = ModEntities.GHOST.get().create(level);
            float before = demon.getHealth(), ghostBefore = ghost.getHealth();
            var source = player.damageSources().playerAttack(player);
            h.assertTrue(demon.hurt(source, 8), "Demi-tangible weapons still hit Demons");
            h.assertTrue(ghost.hurt(source, 8), "Other intangible targets remain vulnerable");
            h.assertTrue(Math.abs((before - demon.getHealth()) * 2 - (ghostBefore - ghost.getHealth())) < 0.001F,
                    "Demons take half the damage, including purified sword bonus");
            demon.discard(); ghost.discard();
        }
        player.setItemSlot(EquipmentSlot.MAINHAND, enchanted);
        var mob = net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
        mob.setItemSlot(EquipmentSlot.MAINHAND, enchanted.copy());
        var demon = ModEntities.DEMON.get().create(level);
        float before = demon.getHealth();
        demon.hurt(mob.damageSources().mobAttack(mob), 8);
        h.assertTrue(Math.abs(before - demon.getHealth() - 4) < 0.001F, "Mob-wielded weapons receive the same resistance");
        demon.invulnerableTime = 0;
        before = demon.getHealth();
        demon.hurt(player.damageSources().indirectMagic(player, player), 8);
        h.assertTrue(Math.abs(before - demon.getHealth() - 8) < 0.001F, "Spells remain full strength even with a weapon held");
        demon.invulnerableTime = 0;
        before = demon.getHealth();
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        h.assertFalse(demon.hurt(player.damageSources().playerAttack(player), 8), "Ordinary weapons still cannot hit intangible Demons");
        h.assertTrue(demon.getHealth() == before, "Ordinary attack does no damage");
        demon.hurt(demon.damageSources().fellOutOfWorld(), 8);
        h.assertTrue(Math.abs(before - demon.getHealth() - 8) < 0.001F, "Void damage remains full strength");
        demon.discard(); mob.discard(); player.discard(); h.succeed();
    }
}
