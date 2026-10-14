package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.mob.MortalGolemEntity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public final class MortalGolemRenderer extends IronGolemRenderer {
    public MortalGolemRenderer(EntityRendererProvider.Context context){super(context);layers.clear();}
    @Override public ResourceLocation getTextureLocation(IronGolem golem) {
        int tier=golem instanceof MortalGolemEntity mortal?mortal.fleshTier():1;
        return ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"textures/entity/mortal_golem_"+tier+".png");
    }
}
