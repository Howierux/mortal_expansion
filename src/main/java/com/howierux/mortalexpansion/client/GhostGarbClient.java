package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;

@EventBusSubscriber(modid = MortalExpansion.MOD_ID, value = Dist.CLIENT)
public final class GhostGarbClient {
    private GhostGarbClient() {}
    @SubscribeEvent public static void wallOverlay(RenderBlockScreenEffectEvent event) {
        if (GhostGarbPhasing.active(event.getPlayer()) && event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.BLOCK) event.setCanceled(true);
    }
}
