package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.blockentity.SpiritwellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/** Vanilla Nether Star sprite, always visible from the camera, at 22% opacity. */
public final class SpiritwellRenderer implements BlockEntityRenderer<SpiritwellBlockEntity> {
    private static final ResourceLocation STAR = ResourceLocation.withDefaultNamespace("item/nether_star");
    public SpiritwellRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    public void render(SpiritwellBlockEntity well, float partialTick, PoseStack poses,
            MultiBufferSource buffers, int light, int overlay) {
        if (well.getLevel() == null) return;
        if (SpiritJourneyClient.isActive()) {
            net.minecraft.client.renderer.blockentity.BeaconRenderer.renderBeaconBeam(poses, buffers,
                    net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_LOCATION,
                    partialTick, 1.0F, well.getLevel().getGameTime(), 0, 1024,
                    0xFFB8FFFF, 0.2F, 0.25F);
        }
        float time = (well.getLevel().getGameTime() % 24000) + partialTick;
        // Position is surface + 2. The visual floats around that precise height.
        float phase = (float) (well.getBlockPos().asLong() & 255);
        poses.pushPose();
        poses.translate(0.5, Math.sin((time + phase) * 0.04) * 0.08, 0.5);
        poses.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poses.mulPose(Axis.ZP.rotationDegrees(time * 0.5F + phase));
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(STAR);
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
        PoseStack.Pose pose = poses.last();
        vertex(vertices, pose, -0.375F, -0.375F, sprite.getU0(), sprite.getV1());
        vertex(vertices, pose,  0.375F, -0.375F, sprite.getU1(), sprite.getV1());
        vertex(vertices, pose,  0.375F,  0.375F, sprite.getU1(), sprite.getV0());
        vertex(vertices, pose, -0.375F,  0.375F, sprite.getU0(), sprite.getV0());
        poses.popPose();
    }
    private static void vertex(VertexConsumer vertices, PoseStack.Pose pose, float x, float y, float u, float v) {
        vertices.addVertex(pose, x, y, 0).setColor(255, 255, 255, 56)
                .setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
    }
    @Override public boolean shouldRenderOffScreen(SpiritwellBlockEntity well) { return SpiritJourneyClient.isActive(); }
    @Override public int getViewDistance() { return 256; }
    @Override public boolean shouldRender(SpiritwellBlockEntity well, net.minecraft.world.phys.Vec3 camera) {
        return net.minecraft.world.phys.Vec3.atCenterOf(well.getBlockPos()).multiply(1, 0, 1)
                .closerThan(camera.multiply(1, 0, 1), getViewDistance());
    }
    @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(SpiritwellBlockEntity well) {
        var pos = well.getBlockPos();
        return SpiritJourneyClient.isActive()
                ? new net.minecraft.world.phys.AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1,
                        pos.getY() + 1024, pos.getZ() + 1)
                : new net.minecraft.world.phys.AABB(pos);
    }
}
