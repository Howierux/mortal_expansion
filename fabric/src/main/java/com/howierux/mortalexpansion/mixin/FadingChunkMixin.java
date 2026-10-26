package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.fancifier.FadingWorld;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LevelChunk.class)
public abstract class FadingChunkMixin {
    @Inject(method="setBlockState",at=@At("RETURN"))
    private void mortalExpansion$changed(BlockPos pos,BlockState state,boolean moving,CallbackInfoReturnable<BlockState> callback){FadingWorld.changed((LevelChunk)(Object)this,pos,callback.getReturnValue(),state);}
}
