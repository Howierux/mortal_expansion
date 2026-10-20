package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.block.DreamcatcherBlock;
import com.howierux.mortalexpansion.blockentity.DreamcatcherBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** A live lunar charm; both tiers share the native moon atlas and level clock. */
public final class DreamcatcherRenderer implements BlockEntityRenderer<DreamcatcherBlockEntity> {
    private static final ResourceLocation MOON = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");

    public DreamcatcherRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(DreamcatcherBlockEntity catcher, float partialTick, PoseStack poses,
            MultiBufferSource buffers, int light, int overlay) {
        var level = catcher.getLevel();
        if (level == null) return;
        int phase = Math.floorMod(level.getMoonPhase(), 8);
        float u0 = (phase % 4) / 4.0F + 0.0625F, u1 = u0 + 0.125F;
        float v0 = (phase / 4) / 2.0F + 0.125F, v1 = v0 + 0.25F;
        poses.pushPose();
        poses.translate(0.5, 0.61, 0.5);
        poses.mulPose(Axis.YP.rotationDegrees(-catcher.getBlockState().getValue(DreamcatcherBlock.FACING).toYRot()));
        // Local +Z faces out. The SOUTH model lies at z=0.03125; sit just
        // ahead of that surface, then rotate the same anchor for every wall.
        float z = -0.466F, half = 0.125F;
        var vertices = buffers.getBuffer(RenderType.entityTranslucentEmissive(MOON));
        float[][] corners = {{-half,-half,u0,v1},{half,-half,u1,v1},{half,half,u1,v0},{-half,half,u0,v0}};
        for (float[] corner : corners) {
            vertices.addVertex(poses.last(), corner[0], corner[1], z).setColor(255,255,255,255)
                    .setUv(corner[2],corner[3]).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT).setNormal(poses.last(),0,0,1);
        }
        poses.popPose();
    }
}
