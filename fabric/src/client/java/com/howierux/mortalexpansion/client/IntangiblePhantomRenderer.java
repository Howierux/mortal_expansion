package com.howierux.mortalexpansion.client;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.monster.Phantom;
public final class IntangiblePhantomRenderer extends PhantomRenderer {
 public IntangiblePhantomRenderer(EntityRendererProvider.Context context){super(context);model=new PhantomModel<Phantom>(context.bakeLayer(ModelLayers.PHANTOM)){
  @Override public void renderToBuffer(PoseStack pose,VertexConsumer vertices,int light,int overlay,int color){super.renderToBuffer(pose,vertices,light,overlay,(color&0xFFFFFF)|(Math.min(color>>>24,51)<<24));}
 };}
 @Override protected RenderType getRenderType(Phantom entity,boolean visible,boolean translucent,boolean glowing){return !visible&&!translucent?(glowing?RenderType.outline(getTextureLocation(entity)):null):RenderType.entityTranslucent(getTextureLocation(entity));}
}
