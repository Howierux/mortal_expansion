package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(FallingBlockRenderer.class)
public abstract class FadingFallingRenderMixin {
    @ModifyVariable(method="render(Lnet/minecraft/world/entity/item/FallingBlockEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at=@At("HEAD"),argsOnly=true)
    private MultiBufferSource mortalExpansion$buffers(MultiBufferSource buffers,FallingBlockEntity entity,float yaw,float delta,PoseStack poses,MultiBufferSource ignored,int light){return FadingRenderTypes.buffers(buffers,(((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)entity).hasAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_ENTITY)?((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)entity).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.FADING_ENTITY):100));}
}
