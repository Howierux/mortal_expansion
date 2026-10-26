package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.entity.mob.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.*;
/** Native animated humanoid with small, baked sin-specific additions. Hitboxes remain shared. */
public final class DemonModel extends HumanoidModel<DemonEntity> {
    private final SinAffinity sin;
    public DemonModel(ModelPart root,SinAffinity sin){super(root);this.sin=sin;}
    public static ModelLayerLocation layer(SinAffinity sin){return new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("mortal_expansion","demon_"+sin.getSerializedName()),"main");}
    private static void horn(PartDefinition head,String id,float x,float height,float pitch,float roll){
        head.addOrReplaceChild(id,CubeListBuilder.create().texOffs(56,0).addBox(-1,-height,-1,2,height,2),
                PartPose.offsetAndRotation(x,-7,0,pitch,0,roll));
    }
    private static void brow(PartDefinition head,String id,float x,float y,float roll){
        head.addOrReplaceChild(id,CubeListBuilder.create().texOffs(56,16).addBox(-1.25F,-.25F,-.25F,2.5F,.5F,.5F),
                PartPose.offsetAndRotation(x,y,-4.1F,0,0,roll));
    }
    private static void ornament(PartDefinition parent,String id,float x,float y,float z,float w,float h){
        parent.addOrReplaceChild(id,CubeListBuilder.create().texOffs(56,24).addBox(-w/2,-h/2,-.25F,w,h,.5F),PartPose.offset(x,y,z));
    }
    public static LayerDefinition createBodyLayer(SinAffinity sin){
        var mesh=HumanoidModel.createMesh(CubeDeformation.NONE,0);var root=mesh.getRoot();
        root.addOrReplaceChild("hat",CubeListBuilder.create(),PartPose.ZERO);
        var head=root.getChild("head");var body=root.getChild("body");
        switch(sin){
            case PRIDE->{
                horn(head,"crown_left",-3,4,0,-.12F);horn(head,"crown_center",0,5,0,0);horn(head,"crown_right",3,4,0,.12F);
                ornament(head,"crest",0,-6.7F,-4.1F,1,2);brow(head,"brow_left",-2,-5.6F,-.12F);brow(head,"brow_right",2,-5.6F,.12F);
            }
            case ENVY->{
                horn(head,"horn_left",-3,4,-.1F,-.45F);horn(head,"horn_right",3,2,-.1F,.45F);
                brow(head,"brow_left",-2,-4.8F,-.25F);brow(head,"brow_right",2,-4.8F,.25F);
            }
            case GLUTTONY->{
                horn(head,"horn_left",-3,2,-.3F,-.6F);horn(head,"horn_right",3,2,-.3F,.6F);
                head.addOrReplaceChild("jaw",CubeListBuilder.create().texOffs(0,8).addBox(-4,0,-4.6F,8,2,3),PartPose.ZERO);
                body.addOrReplaceChild("belly",CubeListBuilder.create().texOffs(16,20).addBox(-4,3,-3.2F,8,7,2),PartPose.ZERO);
                ornament(head,"tooth_left",-2,-.2F,-4.9F,1,1);ornament(head,"tooth_right",2,-.2F,-4.9F,1,1);
            }
            case WRATH->{
                horn(head,"horn_left",-3,5,-.45F,-.5F);horn(head,"horn_right",3,5,-.45F,.5F);
                brow(head,"brow_left",-2,-4.8F,.35F);brow(head,"brow_right",2,-4.8F,-.35F);
                for(var arm:new String[]{"left_arm","right_arm"})root.getChild(arm).addOrReplaceChild("shoulder",
                        CubeListBuilder.create().texOffs(56,0).addBox(-1,-2,-1,2,2,2),PartPose.offset(0,-2,0));
            }
            case SLOTH->{
                horn(head,"horn_left",-3,3,1.05F,-.35F);horn(head,"horn_right",3,3,1.05F,.35F);
                brow(head,"lid_left",-2,-4.5F,0);brow(head,"lid_right",2,-4.5F,0);
            }
            case GREED->{
                horn(head,"horn_left",-3,3,0,-.15F);horn(head,"horn_right",3,3,0,.15F);
                ornament(head,"ring_left",-4.2F,-2.5F,0,1,2);ornament(head,"ring_right",4.2F,-2.5F,0,1,2);
                for(int x=-2;x<=2;x+=2)ornament(body,"coin_"+(x+2),x,2,-2.3F,1,1.5F);
                ornament(body,"buckle",0,9,-2.3F,2,2);
            }
            case LUST->{
                horn(head,"horn_left",-3,4,-.85F,-.35F);horn(head,"horn_right",3,4,-.85F,.35F);
                ornament(head,"brow_gem",0,-5.8F,-4.2F,1,1.5F);
                brow(head,"brow_left",-2,-5.5F,-.3F);brow(head,"brow_right",2,-5.5F,.3F);
                ornament(body,"collar_left",-2,1,-2.3F,1,2);ornament(body,"collar_right",2,1,-2.3F,1,2);
            }
        }
        return LayerDefinition.create(mesh,64,64);
    }
    @Override public void setupAnim(DemonEntity entity,float swing,float amount,float age,float yaw,float pitch){
        super.setupAnim(entity,swing,amount,age,yaw,pitch);
        if(sin==SinAffinity.SLOTH){head.xRot+=.22F;body.xRot+=.08F;rightArm.xRot+=.15F;leftArm.xRot+=.15F;}
        if(sin==SinAffinity.PRIDE)head.xRot-=.10F;
    }
    @Override public void renderToBuffer(PoseStack pose,VertexConsumer consumer,int light,int overlay,int color){
        int tint=sin.color();int alpha=Math.min(color>>>24,51);
        int tinted=(alpha<<24)|(((color>>16&255)*(tint>>16&255)/255)<<16)
                |(((color>>8&255)*(tint>>8&255)/255)<<8)|((color&255)*(tint&255)/255);
        super.renderToBuffer(pose,consumer,light,overlay,tinted);
    }
}
