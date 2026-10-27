package com.howierux.mortalexpansion.mixin;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.Level;
import net.minecraft.core.*;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
@Mixin(PistonBaseBlock.class)
public abstract class FadingPistonTransactionMixin {@WrapMethod(method="moveBlocks") private boolean mortalExpansion$pistonMetadata(Level level,BlockPos pos,Direction direction,boolean extending,Operation<Boolean> original){if(extending&&level instanceof net.minecraft.server.level.ServerLevel server&&com.howierux.mortalexpansion.spiritwell.SpiritwellProtection.isProtected(server,pos.relative(direction)))return false;com.howierux.mortalexpansion.fancifier.FadingPistons.before(level,pos);boolean moved=original.call(level,pos,direction,extending);if(moved)com.howierux.mortalexpansion.fancifier.FadingPistons.after(level,pos,direction,extending);return moved;}}
