package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.spiritwell.SpiritwellBlockEntity;
import net.minecraft.client.renderer.blockentity.*;
public final class SpiritwellBeamRenderer implements BlockEntityRenderer<SpiritwellBlockEntity> {
 public SpiritwellBeamRenderer(BlockEntityRendererProvider.Context context){}
 public void render(SpiritwellBlockEntity well,float partial,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay){if(JourneyClient.active&&well.getLevel()!=null)BeaconRenderer.renderBeaconBeam(pose,buffers,net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png"),partial,1,well.getLevel().getGameTime(),0,256,0xFF70FF99,.15F,.2F);}
 public boolean shouldRenderOffScreen(SpiritwellBlockEntity well){return true;}
 public int getViewDistance(){return 256;}
}
