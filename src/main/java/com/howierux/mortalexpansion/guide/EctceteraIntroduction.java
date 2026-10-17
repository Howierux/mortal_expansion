package com.howierux.mortalexpansion.guide;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

/** Introduces the guide privately when vanilla Monster Hunter is earned. */
public final class EctceteraIntroduction {
    private static final ResourceLocation MONSTER_HUNTER =
            ResourceLocation.withDefaultNamespace("adventure/kill_a_mob");

    private EctceteraIntroduction() {}

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && MONSTER_HUNTER.equals(event.getAdvancement().id())) {
            player.sendSystemMessage(Component.translatable("message.mortal_expansion.ectcetera.crafting_hint"));
        }
    }
}
