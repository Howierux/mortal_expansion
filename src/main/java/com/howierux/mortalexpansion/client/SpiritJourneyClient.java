package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.network.SpiritJourneyPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class SpiritJourneyClient {
    private SpiritJourneyClient() {}
    public static void handleEligibility(SpiritJourneyPayloads.Eligibility payload, IPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        if (payload.entered() && mc.screen instanceof DeathScreen) mc.setScreen(null);
        else if (mc.screen instanceof SpiritJourneyDeathScreen screen) screen.mortalExpansion$setJourneyAvailable(payload.available());
    }
}
