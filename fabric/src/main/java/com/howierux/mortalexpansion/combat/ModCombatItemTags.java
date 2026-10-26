package com.howierux.mortalexpansion.combat;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Item tags used by Mortal Expansion combat rules.
 */
public final class ModCombatItemTags {
    private ModCombatItemTags() {}

    public static final TagKey<Item> SPECTRAL_PROTECTIVE_ARMOR = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("mortal_expansion", "spectral_protective_armor"));

    /**
     * Items capable of physically striking intangible entities.
     *
     * Bloodstone and Spectralite tools are members by default. Future
     * enchantment-based support should feed into the same central permission
     * check rather than duplicating combat logic.
     */
    public static final TagKey<Item> DEMI_TANGIBLE = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("mortal_expansion", "demi_tangible")
    );
}
