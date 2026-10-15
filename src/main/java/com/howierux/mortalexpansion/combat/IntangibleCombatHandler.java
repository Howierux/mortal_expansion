package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.entity.IntangibleEntities;
import com.howierux.mortalexpansion.entity.mob.PharaohlEntity;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Prevents ordinary damage from affecting entities classified as intangible.
 */
public final class IntangibleCombatHandler {
    private IntangibleCombatHandler() {}

    public static final float ARMOR_REDUCTION_PER_PIECE = 0.05F;
    public static final int MAX_PROTECTIVE_ARMOR_PIECES = 4;

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        // Void is a world boundary, never a spectral defense exception.
        if(event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD))return;
        applyIntangibleArmorProtection(event);

        ItemStack weapon = event.getSource().getWeaponItem();
        boolean purifiedGravestoneSword = weapon != null
                && !weapon.isEmpty()
                && weapon.is(ModItems.PURIFIED_GRAVESTONE_SWORD.get());

        if (event.getEntity() instanceof PharaohlEntity pharaohl
                && !pharaohl.isCurrentlyIntangible()) {
            boolean demiTangible =
                    DemiTangibleDamage.canDamageIntangible(event.getSource());

            event.setAmount(
                    event.getAmount() * (demiTangible ? 0.5F : 1.5F)
            );
            return;
        }

        if (!IntangibleEntities.isIntangible(event.getEntity())) {
            return;
        }

        if (GhostGarbBreathing.garbDrowning(event.getEntity(), event.getSource())) return;
        if (!DemiTangibleDamage.canDamageIntangible(event.getSource())) {
            event.setCanceled(true);
            return;
        }

        if (purifiedGravestoneSword) {
            event.setAmount(event.getAmount() * 1.5F);
        }
    }

    private static void applyIntangibleArmorProtection(LivingIncomingDamageEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker == null || !IntangibleEntities.isIntangible(attacker)) {
            return;
        }

        int qualifyingPieces = 0;
        for (ItemStack armor : event.getEntity().getArmorSlots()) {
            if (isSpectralArmor(armor)) {
                qualifyingPieces++;
            }
        }

        if (qualifyingPieces > 0) {
            float multiplier = 1.0F - (ARMOR_REDUCTION_PER_PIECE * qualifyingPieces);
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    private static boolean isSpectralArmor(ItemStack stack) {
        return stack.is(ModCombatItemTags.SPECTRAL_PROTECTIVE_ARMOR);
    }
}
