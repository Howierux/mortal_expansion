package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Player body tint uses the mob's 20% opacity; leather armor keeps its placeholder textures. */
@Mixin(LivingEntityRenderer.class)
public abstract class GhostGarbRenderMixin {
    @Unique private boolean mortalExpansion$ghostBody;
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void mortalExpansion$capture(LivingEntity entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light, CallbackInfo ci) {
        mortalExpansion$ghostBody = GhostGarbPhasing.active(entity);
    }
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void mortalExpansion$translucent(LivingEntity entity, boolean visible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (visible && GhostGarbPhasing.active(entity)) cir.setReturnValue(RenderType.entityTranslucent(((LivingEntityRenderer)(Object)this).getTextureLocation(entity)));
    }
    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), index = 4)
    private int mortalExpansion$opacity(int color) {
        return mortalExpansion$ghostBody ? (color & 0x00FFFFFF) | (Math.min(color >>> 24, 51) << 24) : color;
    }
}
