package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

/**
 * Extra Ghast face pass containing only the eyes and mouth.
 *
 * With the base Ghast at 20% opacity, an additional 37.5% alpha pass produces
 * approximately 50% total effective opacity:
 * 0.20 + (1 - 0.20) * 0.375 = 0.50.
 */
public final class GhastFaceOpacityLayer extends RenderLayer<Ghast, GhastModel<Ghast>> {
    private static final float FACE_OVERLAY_ALPHA = 0.375F;

    private static final ResourceLocation NORMAL_FACE =
            ResourceLocation.fromNamespaceAndPath(
                    MortalExpansion.MOD_ID,
                    "textures/entity/ghast/face_overlay.png"
            );

    private static final ResourceLocation SHOOTING_FACE =
            ResourceLocation.fromNamespaceAndPath(
                    MortalExpansion.MOD_ID,
                    "textures/entity/ghast/face_overlay_shooting.png"
            );

    public GhastFaceOpacityLayer(RenderLayerParent<Ghast, GhastModel<Ghast>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Ghast ghast,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ResourceLocation texture = ghast.isCharging() ? SHOOTING_FACE : NORMAL_FACE;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, FACE_OVERLAY_ALPHA);
        try {
            this.getParentModel().renderToBuffer(
                    poseStack,
                    consumer,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    0xFFFFFFFF
            );
        } finally {
            // The parent renderer is still inside its 20% opacity render call.
            RenderSystem.setShaderColor(
                    1.0F,
                    1.0F,
                    1.0F,
                    IntangibleGhastRenderer.INTANGIBLE_ALPHA
            );
        }
    }
}
