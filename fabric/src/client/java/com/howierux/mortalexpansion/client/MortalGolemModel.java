package com.howierux.mortalexpansion.client;


import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

/** Native golem animation and UV layout, with a flat pumpkin face instead of a nose. */
public final class MortalGolemModel extends IronGolemModel<IronGolem> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("mortal_expansion", "mortal_golem"), "main");
    public MortalGolemModel(ModelPart root) {super(root);}
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F),
            PartPose.offset(0.0F, -7.0F, -2.0F)
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
                .texOffs(0, 40)
                .addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F)
                .texOffs(0, 70)
                .addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.5F)),
            PartPose.offset(0.0F, -7.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_arm", CubeListBuilder.create().texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_arm", CubeListBuilder.create().texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_leg", CubeListBuilder.create().texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(-4.0F, 11.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_leg", CubeListBuilder.create().texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(5.0F, 11.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
