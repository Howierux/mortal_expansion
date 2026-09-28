package com.howierux.mortalexpansion.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

public final class PlaceholderHumanoidRenderer<T extends Mob>
        extends MobRenderer<T, HumanoidModel<T>> {
    private final ResourceLocation texture;
    private final boolean translucent;

    public PlaceholderHumanoidRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation texture,
            boolean translucent,
            float shadowRadius
    ) {
        super(
                context,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)),
                shadowRadius
        );
        this.texture = texture;
        this.translucent = translucent;
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

        if (translucent) {
            return RenderType.entityTranslucent(texture);
        }

        return super.getRenderType(entity, bodyVisible, translucentFlag, glowing);
    }
}
