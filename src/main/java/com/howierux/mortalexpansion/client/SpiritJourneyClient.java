package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.network.SpiritJourneyPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@net.neoforged.fml.common.EventBusSubscriber(modid = com.howierux.mortalexpansion.MortalExpansion.MOD_ID,
        value = net.neoforged.api.distmarker.Dist.CLIENT)
public final class SpiritJourneyClient {
    private static boolean active;
    private SpiritJourneyClient() {}
    public static boolean isActive() {
        var mc = Minecraft.getInstance();
        return active && mc.player != null && mc.player.isSpectator();
    }
    public static void handleState(SpiritJourneyPayloads.State payload, IPayloadContext context) {
        context.enqueueWork(() -> active = payload.active());
    }
    @net.neoforged.bus.api.SubscribeEvent
    public static void onLogout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        active = false;
    }
    public static void handleEligibility(SpiritJourneyPayloads.Eligibility payload, IPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (payload.entered() && mc.screen instanceof DeathScreen) mc.setScreen(null);
        else if (mc.screen instanceof SpiritJourneyDeathScreen screen) screen.mortalExpansion$setJourneyAvailable(payload.available());
    }
}
