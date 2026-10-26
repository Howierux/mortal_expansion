package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.entity.mob.*;
import com.howierux.mortalexpansion.guide.DarkholdProgression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.EnumMap;
/** Select from pre-baked models; the secret is checked for the local viewer on every render. */
public final class DemonRenderer extends MobRenderer<DemonEntity,DemonModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("mortal_expansion","textures/entity/demon.png");
    private final EnumMap<SinAffinity,DemonModel> models=new EnumMap<>(SinAffinity.class);
    public DemonRenderer(EntityRendererProvider.Context context){
        super(context,new DemonModel(context.bakeLayer(DemonModel.layer(SinAffinity.PRIDE)),SinAffinity.PRIDE),0);
        for(var sin:SinAffinity.values())models.put(sin,new DemonModel(context.bakeLayer(DemonModel.layer(sin)),sin));
    }
    private boolean visible(){return DarkholdProgression.canSeeDemon(Minecraft.getInstance().player);}
    @Override public boolean shouldRender(DemonEntity demon,Frustum camera,double x,double y,double z){return visible() && super.shouldRender(demon,camera,x,y,z);}
    @Override public void render(DemonEntity demon,float yaw,float delta,PoseStack pose,MultiBufferSource buffers,int light){
        if(!visible())return;model=models.get(demon.sinAffinity());
        pose.pushPose();pose.translate(0,.08+Math.sin((demon.tickCount+delta)*.12)*.035,0);
        super.render(demon,yaw,delta,pose,buffers,light);pose.popPose();
    }
    @Override public ResourceLocation getTextureLocation(DemonEntity demon){return TEXTURE;}
    @Override protected RenderType getRenderType(DemonEntity demon,boolean bodyVisible,boolean translucent,boolean glowing){
        if(glowing && !bodyVisible && !translucent)return RenderType.outline(TEXTURE);
        return RenderType.entityTranslucent(TEXTURE);
    }
}
