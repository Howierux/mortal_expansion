package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class FadingBlockEntityRenderMixin {
    @ModifyVariable(method="render",at=@At("HEAD"),argsOnly=true)
    private MultiBufferSource mortalExpansion$buffers(MultiBufferSource buffers,BlockEntity be,float partial,PoseStack poses,MultiBufferSource ignored){return FadingRenderTypes.buffers(buffers,FadingNativeClient.opacity(be.getBlockPos()));}
}
