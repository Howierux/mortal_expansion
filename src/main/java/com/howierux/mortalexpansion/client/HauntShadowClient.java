package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.mob.GhostEntity;
import com.howierux.mortalexpansion.network.HauntEffectPayloads;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
    private static HumanoidModel<GhostEntity> apparitionModel;
    private static Object modelSet;
    private static final ResourceLocation STEVE = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");
    // Distinct from the 20% opacity used by intangible mobs.
    private static final int APPARITION_COLOR = 0xBFFFFFFF;
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
        if (apparitionModel == null || modelSet != mc.getEntityModels()) {
            modelSet = mc.getEntityModels();
            apparitionModel = new HumanoidModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER));
        }
        var poses = event.getPoseStack();
        poses.pushPose();
        poses.translate(pos.x, pos.y, pos.z);
        poses.mulPose(Axis.YP.rotationDegrees(180.0F - shadow.getYRot()));
        poses.scale(-1.0F, -1.0F, 1.0F);
        poses.translate(0.0F, -1.501F, 0.0F);
        apparitionModel.setupAnim(shadow, 0, 0, 0, 0, 0);
        apparitionModel.renderToBuffer(poses, buffers.getBuffer(RenderType.entityTranslucent(STEVE)),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, APPARITION_COLOR);
        // White eye planes cover Steve's pupils without changing the real Ghost renderer.
        poses.pushPose();
        apparitionModel.head.translateAndRotate(poses);
        var eyes = buffers.getBuffer(RenderType.lightning());
        eye(eyes, poses.last(), -3.0F / 16, -1.0F / 16);
        eye(eyes, poses.last(), 1.0F / 16, 3.0F / 16);
        poses.popPose();
        poses.popPose();
    }
    private static void eye(VertexConsumer vertices, com.mojang.blaze3d.vertex.PoseStack.Pose pose,
            float left, float right) {
        float top = -4.0F / 16, bottom = -3.0F / 16, front = -4.01F / 16;
        vertices.addVertex(pose, left, top, front).setColor(255, 255, 255, 191);
        vertices.addVertex(pose, left, bottom, front).setColor(255, 255, 255, 191);
        vertices.addVertex(pose, right, bottom, front).setColor(255, 255, 255, 191);
        vertices.addVertex(pose, right, top, front).setColor(255, 255, 255, 191);
    }
}
