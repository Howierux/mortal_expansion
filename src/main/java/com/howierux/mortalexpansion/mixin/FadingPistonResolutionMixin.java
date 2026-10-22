package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.fancifier.FadingPistons;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.piston.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
@Mixin(PistonBaseBlock.class)
public abstract class FadingPistonResolutionMixin {
    @WrapOperation(method="moveBlocks",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"))
    private boolean mortalExpansion$resolved(PistonStructureResolver resolver,Operation<Boolean> original,@Local(argsOnly=true) Level level,@Local(argsOnly=true) BlockPos pos){boolean success=original.call(resolver);if(success)FadingPistons.resolved(level,pos,resolver);return success;}
}
