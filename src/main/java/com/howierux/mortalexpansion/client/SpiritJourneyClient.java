package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.network.SpiritJourneyPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@net.neoforged.fml.common.EventBusSubscriber(modid = com.howierux.mortalexpansion.MortalExpansion.MOD_ID,
        value = net.neoforged.api.distmarker.Dist.CLIENT)
public final class SpiritJourneyClient {
    public static final net.minecraft.client.KeyMapping JOURNEY_MENU = new net.minecraft.client.KeyMapping(
            "key.mortal_expansion.journey_menu", org.lwjgl.glfw.GLFW.GLFW_KEY_J, "key.categories.mortal_expansion");
    @net.neoforged.bus.api.SubscribeEvent
    public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        while (JOURNEY_MENU.consumeClick()) {
            if (isActive() && mc.screen == null) mc.setScreen(new com.howierux.mortalexpansion.client.screen.SpiritJourneyScreen());
        }
    }
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
        if (payload.entered()) {
            if (mc.screen instanceof DeathScreen) mc.setScreen(null);
            if (mc.player != null) mc.player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.mortal_expansion.journey_menu", net.minecraft.network.chat.Component.keybind("key.mortal_expansion.journey_menu")), false);
        }
        else if (mc.screen instanceof SpiritJourneyDeathScreen screen) screen.mortalExpansion$setJourneyAvailable(payload.available());
    }
}
