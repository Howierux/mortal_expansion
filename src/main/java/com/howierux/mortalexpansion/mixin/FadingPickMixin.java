package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingNativeClient;
import com.howierux.mortalexpansion.registry.ModDataComponents;
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
    @WrapOperation(method="pickBlock",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/phys/HitResult;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack mortalExpansion$pick(BlockState state,HitResult hit,LevelReader level,BlockPos pos,Player player,Operation<ItemStack> original){var stack=original.call(state,hit,level,pos,player);int opacity=FadingNativeClient.opacity(pos);if(!stack.isEmpty() && opacity<100){stack=stack.copy();stack.set(ModDataComponents.FADING.get(),opacity);}return stack;}
}
