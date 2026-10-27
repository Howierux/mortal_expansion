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

public final class SpectralMobRenderer<T extends Mob>
        extends MobRenderer<T, SpectralMobRenderer.SpectralHumanoidModel<T>> {
    private static final float SPECTRAL_ALPHA = 0.20F;

    private final ResourceLocation texture;
    private final Predicate<T> spectral;
    private final Predicate<T> visible;
    private final java.util.function.ToIntFunction<T> tint;

    public SpectralMobRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation texture,
            Predicate<T> spectral,
            float shadowRadius
    ) { this(context,texture,spectral,shadowRadius,entity->0xFFFFFFFF); }
    public SpectralMobRenderer(EntityRendererProvider.Context context,ResourceLocation texture,
            Predicate<T> spectral,float shadowRadius,java.util.function.ToIntFunction<T> tint) {
        this(context,texture,spectral,shadowRadius,tint,entity->true);
    }
    public SpectralMobRenderer(EntityRendererProvider.Context context,ResourceLocation texture,
            Predicate<T> spectral,float shadowRadius,java.util.function.ToIntFunction<T> tint,Predicate<T> visible) {
        super(
                context,
                new SpectralHumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)),
                shadowRadius
        );
        this.texture = texture;
        this.spectral = spectral;
        this.tint = tint;
        this.visible = visible;
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
        if(!visible.test(entity))return;
        boolean spectralNow = spectral.test(entity);
        this.model.setSpectral(spectralNow);
        this.model.tint=tint.applyAsInt(entity);

        poseStack.pushPose();
        if (spectralNow) {
            double hover = 0.08D
                    + Math.sin((entity.tickCount + partialTicks) * 0.12D)
                    * 0.035D;
            poseStack.translate(0.0D, hover, 0.0D);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    // Cull before dispatcher rendering so fire, outlines and nameplates cannot reveal hidden creatures.
    @Override public boolean shouldRender(T entity,net.minecraft.client.renderer.culling.Frustum camera,
            double x,double y,double z){return visible.test(entity) && super.shouldRender(entity,camera,x,y,z);}

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
        private int tint=0xFFFFFFFF;

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
            color=(color&0xFF000000)
                    | (((color>>16&255)*(tint>>16&255)/255)<<16)
                    | (((color>>8&255)*(tint>>8&255)/255)<<8) | ((color&255)*(tint&255)/255);
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
