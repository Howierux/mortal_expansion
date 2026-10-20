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
     * enchantability, but has 1.5x diamond durability.
     *
     * Individual tools use Diamond damage/speed baselines; personal Haunt
     * scales their power and speed from 90% at Void through 150% at Eldritch.
     */
    public static final Tier BLOODSTONE = new Tier() {
        @Override
        public int getUses() {
            return 2342;
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

    /**
     * Spectralite is the smithing upgrade above Bloodstone. It keeps
     * Netherite's mining speed, harvest capability, and enchantability while
     * increasing Bloodstone durability by 1.3x.
     *
     * Individual tool damage is also scaled to 1.3x the Bloodstone equivalent.
     */
    public static final Tier SPECTRALITE = new Tier() {
        @Override
        public int getUses() {
            return 3045;
        }

        @Override
        public float getSpeed() {
            return 9.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 4.0F;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(net.minecraft.world.item.Items.NETHERITE_INGOT);
        }
    };
}
