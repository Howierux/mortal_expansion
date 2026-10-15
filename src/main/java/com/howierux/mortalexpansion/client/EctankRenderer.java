package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public final class EctankRenderer implements BlockEntityRenderer<EctoFluidBlockEntity> {
    public EctankRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(EctoFluidBlockEntity tank,float partialTick,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        if(tank.isPipe() || tank.tank.isEmpty())return;
        var fluid=tank.tank.getFluid();var extensions=IClientFluidTypeExtensions.of(fluid.getFluid());
        var texture=extensions.getStillTexture(fluid);if(texture==null)return;
        var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
        int tint=extensions.getTintColor(fluid);int red=(tint>>16)&255,green=(tint>>8)&255,blue=tint&255;
        float min=2.05F/16,max=13.95F/16,bottom=2.02F/16,top=bottom+(11.9F/16)*tank.tank.getFluidAmount()/tank.tank.getCapacity();
        var vertices=buffers.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
        for(var face:Direction.values()){
            float[][] points=switch(face){
                case UP->new float[][]{{min,top,min},{min,top,max},{max,top,max},{max,top,min}};
                case DOWN->new float[][]{{min,bottom,max},{min,bottom,min},{max,bottom,min},{max,bottom,max}};
                case NORTH->new float[][]{{max,bottom,min},{min,bottom,min},{min,top,min},{max,top,min}};
                case SOUTH->new float[][]{{min,bottom,max},{max,bottom,max},{max,top,max},{min,top,max}};
                case WEST->new float[][]{{min,bottom,min},{min,bottom,max},{min,top,max},{min,top,min}};
                case EAST->new float[][]{{max,bottom,max},{max,bottom,min},{max,top,min},{max,top,max}};
            };
            for(int i=0;i<4;i++)vertices.addVertex(poses.last(),points[i][0],points[i][1],points[i][2]).setColor(red,green,blue,210)
                    .setUv(i==0||i==3?sprite.getU0():sprite.getU1(),i<2?sprite.getV1():sprite.getV0())
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(poses.last(),face.getStepX(),face.getStepY(),face.getStepZ());
        }
    }
}
