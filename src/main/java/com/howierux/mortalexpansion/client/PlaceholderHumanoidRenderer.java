package com.howierux.mortalexpansion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public final class PlaceholderHumanoidRenderer<T extends Mob>
        extends MobRenderer<T, PlaceholderHumanoidRenderer.SpectralHumanoidModel<T>> {
    private static final float SPECTRAL_ALPHA = 0.20F;
    private static final float HOVER_BASE = 0.08F;
    private static final float HOVER_AMPLITUDE = 0.035F;
    private static final float HOVER_SPEED = 0.12F;

    private final ResourceLocation texture;
    private final Predicate<T> spectral;

    public PlaceholderHumanoidRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation texture,
            Predicate<T> spectral,
            float shadowRadius
    ) {
        super(
                context,
                new SpectralHumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)),
                shadowRadius
        );
        this.texture = texture;
        this.spectral = spectral;
    }

    @Override
    public void render(
            T entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        boolean isSpectral = spectral.test(entity);
        this.model.setSpectral(isSpectral);

        if (isSpectral) {
            float age = entity.tickCount + partialTicks;
            float hover = HOVER_BASE
                    + (float)Math.sin(age * HOVER_SPEED) * HOVER_AMPLITUDE;

            poseStack.pushPose();
            poseStack.translate(0.0D, hover, 0.0D);
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            poseStack.popPose();
            return;
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected float getShadowRadius(T entity) {
        return spectral.test(entity) ? 0.0F : super.getShadowRadius(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }

    @Override
    @Nullable
    protected RenderType getRenderType(
            T entity,
            boolean bodyVisible,
            boolean translucentFlag,
            boolean glowing
    ) {
        if (glowing && !bodyVisible && !translucentFlag) {
            return RenderType.outline(texture);
        }

        if (spectral.test(entity)) {
            return RenderType.entityTranslucent(texture);
        }

        return super.getRenderType(entity, bodyVisible, translucentFlag, glowing);
    }

    public static final class SpectralHumanoidModel<T extends Mob>
            extends HumanoidModel<T> {
        private boolean spectral;

        public SpectralHumanoidModel(ModelPart root) {
            super(root);
        }

        public void setSpectral(boolean spectral) {
            this.spectral = spectral;
        }

        @Override
        public void renderToBuffer(
                PoseStack poseStack,
                com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
                int packedLight,
                int packedOverlay,
                int color
        ) {
            if (!spectral) {
                super.renderToBuffer(
                        poseStack,
                        vertexConsumer,
                        packedLight,
                        packedOverlay,
                        color
                );
                return;
            }

            int currentAlpha = color >>> 24;
            int spectralAlpha = Math.round(SPECTRAL_ALPHA * 255.0F);
            int alpha = Math.min(currentAlpha, spectralAlpha);
            int spectralColor = (color & 0x00FFFFFF) | (alpha << 24);

            super.renderToBuffer(
                    poseStack,
                    vertexConsumer,
                    packedLight,
                    packedOverlay,
                    spectralColor
            );
        }
    }
}
