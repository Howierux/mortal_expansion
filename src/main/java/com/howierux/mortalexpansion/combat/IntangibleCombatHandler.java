package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.entity.IntangibleEntities;
import com.howierux.mortalexpansion.entity.mob.PharaohlEntity;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Prevents ordinary damage from affecting entities classified as intangible.
 */
public final class IntangibleCombatHandler {
    private IntangibleCombatHandler() {}

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        ItemStack weapon = event.getSource().getWeaponItem();
        boolean purifiedGravestoneSword = weapon != null
                && !weapon.isEmpty()
                && weapon.is(ModItems.PURIFIED_GRAVESTONE_SWORD.get());

        if (purifiedGravestoneSword) {
            event.setAmount(event.getAmount() * 1.5F);
        }

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

        if (!DemiTangibleDamage.canDamageIntangible(event.getSource())) {
            event.setCanceled(true);
            return;
        }

    }
}
