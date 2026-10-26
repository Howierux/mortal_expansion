package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingNativeClient;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockAndTintGetter;
import com.mojang.blaze3d.vertex.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(BlockRenderDispatcher.class)
public abstract class FadingChunkRenderMixin {
    @Inject(method="renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V",at=@At("HEAD"),cancellable=true)
    private void mortalExpansion$separatePass(BlockState state,BlockPos pos,BlockAndTintGetter level,PoseStack poses,VertexConsumer consumer,boolean sides,net.minecraft.util.RandomSource random,CallbackInfo callback){if(FadingNativeClient.opacity(pos)<100)callback.cancel();}
}
