package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.entity.IntangibleEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Prevents ordinary damage from affecting entities classified as intangible.
 */
public final class IntangibleCombatHandler {
    private IntangibleCombatHandler() {}

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!IntangibleEntities.isIntangible(event.getEntity())) {
            return;
        }

        if (!DemiTangibleDamage.canDamageIntangible(event.getSource())) {
            event.setCanceled(true);
        }
    }
}
