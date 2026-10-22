package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingNativeClient;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class FadingVisibilityMixin {
    @ModifyReturnValue(method="isSolidRender",at=@At("RETURN"))
    private boolean mortalExpansion$visibleBehind(boolean original,BlockGetter level,BlockPos pos){return original && (level instanceof com.howierux.mortalexpansion.fancifier.FancifierConnections || FadingNativeClient.opacity(pos)==100);}
}
