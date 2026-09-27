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
 * Renders vanilla Ghast artwork translucently at 69% opacity.
 *
 * Using the vanilla texture directly avoids maintaining a modified copy of the
 * Ghast texture and keeps resource-pack compatibility.
 */
public final class IntangibleGhastRenderer extends GhastRenderer {
    private static final float INTANGIBLE_ALPHA = 0.69F;

    public IntangibleGhastRenderer(EntityRendererProvider.Context context) {
        super(context);
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
