package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

import javax.annotation.Nullable;

/**
 * Ghast renderer used by Mortal Expansion's intangible visual language.
 *
 * The textures preserve the vanilla Ghast artwork but reduce non-transparent
 * texel alpha to 69% of vanilla, i.e. a 31% opacity reduction.
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

    /**
     * Vanilla Ghasts normally render with an opaque/cutout entity type.
     * A translucent render type is required for the reduced-alpha texture to
     * blend instead of behaving as a binary cutout.
     */
    @Override
    @Nullable
    protected RenderType getRenderType(
            Ghast ghast,
            boolean bodyVisible,
            boolean translucent,
            boolean glowing
    ) {
        if (glowing && !bodyVisible && !translucent) {
            return RenderType.outline(getTextureLocation(ghast));
        }

        return RenderType.entityTranslucent(getTextureLocation(ghast));
    }
}
