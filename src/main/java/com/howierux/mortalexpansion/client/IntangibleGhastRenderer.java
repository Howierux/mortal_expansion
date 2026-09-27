package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

import javax.annotation.Nullable;

/**
 * Intangible Ghast renderer.
 *
 * The Ghast body/tentacles are rendered at 20% opacity, while only the actual
 * eyes and mouth pixels are raised to roughly 50% opacity. Both normal and
 * charging expressions have their own adjusted texture.
 */
public final class IntangibleGhastRenderer extends GhastRenderer {
    private static final ResourceLocation GHAST_LOCATION =
            ResourceLocation.fromNamespaceAndPath(
                    MortalExpansion.MOD_ID,
                    "textures/entity/ghast/ghast.png"
            );

    private static final ResourceLocation GHAST_SHOOTING_LOCATION =
            ResourceLocation.fromNamespaceAndPath(
                    MortalExpansion.MOD_ID,
                    "textures/entity/ghast/ghast_shooting.png"
            );

    public IntangibleGhastRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Ghast ghast) {
        return ghast.isCharging() ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
    }

    @Override
    @Nullable
    protected RenderType getRenderType(
            Ghast ghast,
            boolean bodyVisible,
            boolean translucent,
            boolean glowing
    ) {
        ResourceLocation texture = getTextureLocation(ghast);

        if (glowing && !bodyVisible && !translucent) {
            return RenderType.outline(texture);
        }

        return RenderType.entityTranslucent(texture);
    }
}
