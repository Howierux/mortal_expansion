package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.entity.mob.SpiritEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

public final class SpiritVillagerRenderer extends MobRenderer<SpiritEntity, SpiritVillagerRenderer.Model> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mortal_expansion","textures/entity/spirit.png");
    public SpiritVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new Model(context.bakeLayer(ModelLayers.VILLAGER)), 0);
    }
    @Override public ResourceLocation getTextureLocation(SpiritEntity spirit) { return TEXTURE; }
    @Override protected RenderType getRenderType(SpiritEntity spirit, boolean visible, boolean translucent, boolean glowing) {
        if (!visible && !translucent) return glowing ? RenderType.outline(TEXTURE) : null;
        return RenderType.entityTranslucent(TEXTURE);
    }
    @Override protected void scale(SpiritEntity spirit, PoseStack pose, float partialTick) {
        float scale = 0.9375F * spirit.getAgeScale(); pose.scale(scale, scale, scale);
    }
    public static final class Model extends VillagerModel<SpiritEntity> {
        public Model(ModelPart root) { super(root); }
        @Override public void renderToBuffer(PoseStack pose, VertexConsumer vertices, int light, int overlay, int color) {
            int alpha = Math.min(color >>> 24, 51);
            super.renderToBuffer(pose, vertices, light, overlay, (color & 0xFFFFFF) | alpha << 24);
        }
    }
}
