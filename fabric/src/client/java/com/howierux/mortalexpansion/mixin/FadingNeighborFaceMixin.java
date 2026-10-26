package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingNativeClient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Block.class)
public abstract class FadingNeighborFaceMixin {
    @Inject(method="shouldRenderFace",at=@At("HEAD"),cancellable=true)
    private static void mortalExpansion$exposeNeighbor(BlockState state,BlockGetter level,BlockPos pos,Direction face,BlockPos neighbor,CallbackInfoReturnable<Boolean> callback){
        if(level instanceof com.howierux.mortalexpansion.fancifier.FancifierFaces || level instanceof com.howierux.mortalexpansion.fancifier.FancifierConnections)return;
        int alpha=FadingNativeClient.opacity(neighbor);if(alpha<100 && alpha!=FadingNativeClient.opacity(pos))callback.setReturnValue(true);
    }
}
