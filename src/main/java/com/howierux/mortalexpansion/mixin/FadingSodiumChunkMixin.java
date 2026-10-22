package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingNativeClient;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Sodium 0.6 replaces vanilla chunk tessellation; prevent a second, opaque copy. */
@Pseudo
@Mixin(targets="net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer",remap=false)
public abstract class FadingSodiumChunkMixin {
    @Inject(method="renderModel",at=@At("HEAD"),cancellable=true)
    private void mortalExpansion$separatePass(BakedModel model,BlockState state,BlockPos pos,BlockPos origin,CallbackInfo callback){
        if(FadingNativeClient.opacity(pos)<100)callback.cancel();
    }
}
