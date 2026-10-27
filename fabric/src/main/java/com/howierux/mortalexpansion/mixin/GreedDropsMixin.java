package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.world.level.block.Block.class)
public abstract class GreedDropsMixin {
 @Inject(method="dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",at=@At("HEAD"),cancellable=true) private static void mortal$capture(net.minecraft.world.level.block.state.BlockState state,net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.entity.BlockEntity be,net.minecraft.world.entity.Entity e,net.minecraft.world.item.ItemStack tool,CallbackInfo ci){if(com.howierux.mortalexpansion.guide.DamnedAbilities.suppressGreedDrops())ci.cancel();}
}
