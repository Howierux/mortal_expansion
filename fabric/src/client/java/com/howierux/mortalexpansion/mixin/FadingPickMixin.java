package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingNativeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
@Mixin(Minecraft.class)
public abstract class FadingPickMixin {
    @WrapOperation(method="pickBlock",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/block/Block;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack mortalExpansion$pick(net.minecraft.world.level.block.Block block,LevelReader level,BlockPos pos,BlockState state,Operation<ItemStack> original){var stack=original.call(block,level,pos,state);int opacity=FadingNativeClient.opacity(pos);if(!stack.isEmpty() && opacity<100){stack=stack.copy();stack.set(com.howierux.mortalexpansion.content.ContentRuntime.FADING,opacity);}return stack;}
}
