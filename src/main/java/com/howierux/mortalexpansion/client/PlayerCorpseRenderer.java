package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.entity.PlayerCorpseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

/** A solid player-shaped body lying on its back, using the owner's available skin. */
public final class PlayerCorpseRenderer extends EntityRenderer<PlayerCorpseEntity> {
    private final HumanoidModel<PlayerCorpseEntity> wide;
    private final HumanoidModel<PlayerCorpseEntity> slim;
    public PlayerCorpseRenderer(EntityRendererProvider.Context context) {
        super(context);
        wide = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        slim = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM));
    }
    private net.minecraft.client.resources.PlayerSkin skin(PlayerCorpseEntity corpse) {
        var id = corpse.owner().orElse(corpse.getUUID());
        var connection = Minecraft.getInstance().getConnection();
        var info = connection == null ? null : connection.getPlayerInfo(id);
        return info == null ? DefaultPlayerSkin.get(id) : info.getSkin();
    }
    @Override public ResourceLocation getTextureLocation(PlayerCorpseEntity corpse) {
        return skin(corpse).texture();
    }
    @Override public void render(PlayerCorpseEntity corpse, float yaw, float partialTick,
            PoseStack poses, MultiBufferSource buffers, int light) {
        var skin = skin(corpse);
        var model = skin.model() == net.minecraft.client.resources.PlayerSkin.Model.SLIM ? slim : wide;
        poses.pushPose();
        poses.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        poses.translate(0, 0.26, 0);
        poses.mulPose(Axis.XP.rotationDegrees(90));
        poses.scale(-1, -1, 1);
        poses.translate(0, -0.75, 0);
        // ArmorStand supplies pose angles to HumanoidModel; its default pose is suitable at rest.
        model.setupAnim(corpse, 0, 0, 0, 0, 0);
        model.renderToBuffer(poses, buffers.getBuffer(RenderType.entityCutoutNoCull(skin.texture())),
                light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        poses.popPose();
        super.render(corpse, yaw, partialTick, poses, buffers, light);
    }
}
