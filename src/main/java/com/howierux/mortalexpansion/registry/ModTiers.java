package com.howierux.mortalexpansion.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Custom Mortal Expansion tool tiers.
 */
public final class ModTiers {
    private ModTiers() {}

    /**
     * Bloodstone keeps diamond's mining speed, harvest capability, and
     * enchantability, but has 1.67x diamond durability.
     *
     * Attack damage for individual Bloodstone tools is scaled separately so
     * their actual final damage matches 1.67x the equivalent diamond tool.
     */
    public static final Tier BLOODSTONE = new Tier() {
        @Override
        public int getUses() {
            return 2607;
        }

        @Override
        public float getSpeed() {
            return 8.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 3.0F;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 10;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.BLOODSTONE.get());
        }
    };
}
