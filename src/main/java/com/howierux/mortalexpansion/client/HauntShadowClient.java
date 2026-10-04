package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.mob.GhostEntity;
import com.howierux.mortalexpansion.network.HauntEffectPayloads;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Render-only figure: never added to the world, targeted, collided with, or ticked as a mob. */
@EventBusSubscriber(modid = MortalExpansion.MOD_ID, value = Dist.CLIENT)
public final class HauntShadowClient {
    private static GhostEntity shadow;
    private static long expires;
    private HauntShadowClient() {}
    public static void handle(HauntEffectPayloads.Shadow payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            shadow = null;
            if (!payload.visible() || mc.level == null || mc.player == null) return;
            shadow = ModEntities.GHOST.get().create(mc.level);
            if (shadow == null) return;
            shadow.moveTo(payload.x(), payload.y(), payload.z(), mc.player.getYRot() + 180, 0);
            expires = mc.level.getGameTime() + 40;
        });
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (shadow == null) return;
        if (mc.level == null || mc.player == null || shadow.level() != mc.level
                || !mc.player.isAlive() || mc.level.getGameTime() >= expires) { shadow = null; return; }
        Vec3 toward = shadow.position().add(0, 1, 0).subtract(mc.player.getEyePosition());
        if (toward.lengthSqr() <= 9 || mc.player.getLookAngle().dot(toward.normalize()) >= 0.94) shadow = null;
    }
    @SubscribeEvent public static void render(RenderLevelStageEvent event) {
        if (shadow == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || shadow.level() != mc.level) return;
        Vec3 camera = event.getCamera().getPosition(), pos = shadow.position().subtract(camera);
        var buffers = mc.renderBuffers().bufferSource();
        mc.getEntityRenderDispatcher().render(shadow, pos.x, pos.y, pos.z, shadow.getYRot(),
                event.getPartialTick().getGameTimeDeltaPartialTick(false), event.getPoseStack(), buffers, LightTexture.FULL_BRIGHT);
    }
}
