package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.registry.ModEnchantments;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

/**
 * Central damage rules for intangible entities.
 */
public final class DemiTangibleDamage {
    private DemiTangibleDamage() {}

    /**
     * Returns true when this damage source is allowed to affect an intangible
     * target.
     *
     * Current exceptions:
     * - any explosion;
     * - a Ghast's large fireball/direct reflected large-fireball hit;
     * - spectral arrows;
     * - attacks made with an item tagged mortal_expansion:demi_tangible;
     * - attacks made with an item carrying the Demi-Tangible enchantment.
     */
    public static boolean canDamageIntangible(DamageSource source) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }

        Entity direct = source.getDirectEntity();
        if (direct != null
                && (direct.getType() == EntityType.FIREBALL
                || direct.getType() == EntityType.SPECTRAL_ARROW)) {
            return true;
        }

        ItemStack weapon = source.getWeaponItem();
        if (weapon == null || weapon.isEmpty()) {
            return false;
        }

        if (weapon.is(ModCombatItemTags.DEMI_TANGIBLE)) {
            return true;
        }

        return weapon.getTagEnchantments()
                .keySet()
                .stream()
                .anyMatch(enchantment -> enchantment.is(ModEnchantments.DEMI_TANGIBLE));
    }
}
