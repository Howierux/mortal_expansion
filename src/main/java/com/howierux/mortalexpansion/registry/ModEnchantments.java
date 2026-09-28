package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {
    private ModEnchantments() {}

    public static final ResourceKey<Enchantment> DEMI_TANGIBLE = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "demi_tangible")
    );
}
