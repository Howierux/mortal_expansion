package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.block.DreamcatcherBlock;
import com.howierux.mortalexpansion.blockentity.DreamcatcherBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** Flat lunar glyphs rather than the textured moon in the sky. */
public final class DreamcatcherRenderer implements BlockEntityRenderer<DreamcatcherBlockEntity> {
    private static final int BANDS = 24;
    private static final float RADIUS = 0.070F;
    private static final float[] SPANS = new float[BANDS];
    private static final float[] TERMINATORS = {1, 0.7071068F, 0, -0.7071068F, -1, -0.7071068F, 0, 0.7071068F};
    static {
        for (int i = 0; i < BANDS; i++) {
            float y = -1 + (i + 0.5F) * 2 / BANDS;
            SPANS[i] = (float)Math.sqrt(1 - y * y);
        }
    }

    public DreamcatcherRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(DreamcatcherBlockEntity catcher, float partialTick, PoseStack poses,
            MultiBufferSource buffers, int light, int overlay) {
        var level = catcher.getLevel();
        if (level == null) return;
        int phase = Math.floorMod(level.getMoonPhase(), 8);
        boolean purified = catcher.getBlockState().is(com.howierux.mortalexpansion.catalog.CatalogBlocks.PURIFIED_DREAMCATCHER);
        int ink = purified ? 0xDFEFF5 : 0xD5EBC1;
        poses.pushPose();
        // The wooden hoop is centered at texture pixel (32, 27.5) on the 64px sprite.
        poses.translate(0.5, 1.0 - 27.5 / 64.0, 0.5);
        poses.mulPose(Axis.YP.rotationDegrees(-catcher.getBlockState().getValue(DreamcatcherBlock.FACING).toYRot()));
        // Match the flat model on all four walls, just ahead of its surface.
        // Native untextured color quads keep the charm legible without lighting
        // or a shader/texture dependency. The outline makes new moon visible.
        var vertices = buffers.getBuffer(RenderType.debugQuads());
        for (int i = 0; i < BANDS; i++) {
            float bottom = (-1 + i * 2.0F / BANDS) * RADIUS;
            float top = bottom + 2 * RADIUS / BANDS;
            float span = SPANS[i] * RADIUS;
            quad(vertices, poses, -span * 1.10F, span * 1.10F, bottom * 1.10F, top * 1.10F, -0.466F, ink);
            quad(vertices, poses, -span, span, bottom, top, -0.4658F, 0x26332D);
            if (phase != 4) {
                float boundary = span * TERMINATORS[phase];
                float left = phase <= 4 ? -span : -boundary;
                float right = phase <= 4 ? boundary : span;
                quad(vertices, poses, left, right, bottom, top, -0.4656F, ink);
            }
        }
        poses.popPose();
    }

    private static void quad(VertexConsumer vertices, PoseStack poses, float left, float right,
            float bottom, float top, float z, int color) {
        vertices.addVertex(poses.last(), left, bottom, z).setColor(0xFF000000 | color);
        vertices.addVertex(poses.last(), right, bottom, z).setColor(0xFF000000 | color);
        vertices.addVertex(poses.last(), right, top, z).setColor(0xFF000000 | color);
        vertices.addVertex(poses.last(), left, top, z).setColor(0xFF000000 | color);
    }
}
