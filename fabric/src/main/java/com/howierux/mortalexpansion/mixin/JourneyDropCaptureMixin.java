package com.howierux.mortalexpansion.mixin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
/** Capture native, post-Vanishing death drops before spawn/automatic-pickup callbacks can observe them. */
@Mixin(ServerPlayer.class)
public abstract class JourneyDropCaptureMixin {
 @WrapOperation(method="drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
 private boolean mortalExpansion$capture(Level level,Entity entity,Operation<Boolean> original){if(entity instanceof ItemEntity item&&com.howierux.mortalexpansion.travel.DeferredDeathItems.capture((ServerPlayer)(Object)this,item))return true;return original.call(level,entity);}
}
