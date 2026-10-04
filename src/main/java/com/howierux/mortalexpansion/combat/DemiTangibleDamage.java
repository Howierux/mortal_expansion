package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.registry.ModEnchantments;
import com.howierux.mortalexpansion.registry.ModDataComponents;
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
     * - magic/magical-tagged damage across mod namespaces;
     * - intangible attackers and sources bypassing invulnerability;
     * - any explosion;
     * - a Ghast's large fireball/direct reflected large-fireball hit;
     * - spectral arrows;
     * - attacks made with an item tagged mortal_expansion:demi_tangible;
     * - attacks made with an item carrying the Demi-Tangible enchantment.
     */
    /** Exact tag names opt into magic behavior; avoid matching unrelated tags containing 'magic'. */
    public static boolean isMagicTag(net.minecraft.resources.ResourceLocation id) {
        String path = id.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        return name.equals("magic") || name.equals("magical") || name.equals("is_magic") || name.equals("is_magical");
    }

    public static boolean canDamageIntangible(DamageSource source) {
        if (source.getEntity() != null && com.howierux.mortalexpansion.entity.IntangibleEntities.isIntangible(source.getEntity())) return true;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;
        if (source.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC)
                || source.typeHolder().tags().anyMatch(tag -> isMagicTag(tag.location()))) return true;
        if (source.is(com.howierux.mortalexpansion.effect.ModDamageTypes.SANCTIFICATION)) {
            return true;
        }

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

        if (Boolean.TRUE.equals(weapon.get(ModDataComponents.DEMI_TANGIBLE.get()))) {
            return true;
        }

        // Backward compatibility for test items created before Demi-Tangible
        // was moved off the vanilla enchantment system.
        return weapon.getTagEnchantments()
                .keySet()
                .stream()
                .anyMatch(enchantment -> enchantment.is(ModEnchantments.DEMI_TANGIBLE));
    }
}
