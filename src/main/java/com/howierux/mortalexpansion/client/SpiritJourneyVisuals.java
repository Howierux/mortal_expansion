package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

/** Isolates the version-sensitive renderer API; no additional rendering mixin is needed. */
@EventBusSubscriber(modid = MortalExpansion.MOD_ID, value = Dist.CLIENT)
public final class SpiritJourneyVisuals {
    private static final ResourceLocation GRAYSCALE = ResourceLocation.fromNamespaceAndPath(
            MortalExpansion.MOD_ID, "shaders/post/spirit_journey.json");
    private static PostChain ownedEffect;
    private SpiritJourneyVisuals() {}

    @SubscribeEvent public static void beforeFrame(RenderFrameEvent.Pre event) {
        var mc = Minecraft.getInstance();
        var renderer = mc.gameRenderer;
        if (SpiritJourneyClient.isActive()) {
            // Camera changes and resource reloads can replace the renderer's chain.
            if (ownedEffect == null || renderer.currentEffect() != ownedEffect) {
                renderer.loadEffect(GRAYSCALE);
                ownedEffect = renderer.currentEffect();
            }
        } else {
            if (ownedEffect != null && renderer.currentEffect() == ownedEffect) {
                renderer.shutdownEffect();
                if (mc.level != null) renderer.checkEntityPostEffect(mc.getCameraEntity());
            }
            ownedEffect = null;
        }
    }
}
