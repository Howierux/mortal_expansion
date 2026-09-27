package com.howierux.mortalexpansion.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

import javax.annotation.Nullable;

/**
 * Renders vanilla Ghast artwork translucently at 20% opacity.
 *
 * Eyes and mouth receive a second masked render pass so those facial features
 * remain readable at roughly 50% effective opacity.
 */
public final class IntangibleGhastRenderer extends GhastRenderer {
    public static final float INTANGIBLE_ALPHA = 0.20F;

    public IntangibleGhastRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new GhastFaceOpacityLayer(this));
    }

    @Override
    public void render(
            Ghast ghast,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, INTANGIBLE_ALPHA);
        try {
            super.render(ghast, entityYaw, partialTick, poseStack, buffer, packedLight);
        } finally {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    @Nullable
    protected RenderType getRenderType(
            Ghast ghast,
            boolean bodyVisible,
            boolean translucent,
            boolean glowing
    ) {
        ResourceLocation texture = super.getTextureLocation(ghast);

        if (glowing && !bodyVisible && !translucent) {
            return RenderType.outline(texture);
        }

        return RenderType.entityTranslucent(texture);
    }
}
