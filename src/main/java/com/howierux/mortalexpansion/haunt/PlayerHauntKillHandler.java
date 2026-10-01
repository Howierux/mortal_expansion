package com.howierux.mortalexpansion.haunt;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/** A tiny personal Haunt cost for player kills of mobs outside Smite's target tag. */
public final class PlayerHauntKillHandler {
    private PlayerHauntKillHandler() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        // Canceled deaths are excluded by the event bus. Projectile sources retain their owner.
        if (!(event.getEntity() instanceof Mob mob)
                || mob.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        PlayerHauntManager.addHundredths(player, FractionalHaunt.MOB_KILL_INCREASE);
    }
}
