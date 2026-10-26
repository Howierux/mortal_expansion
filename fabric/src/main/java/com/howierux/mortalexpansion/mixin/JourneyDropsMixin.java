package com.howierux.mortalexpansion.mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(Player.class)
public abstract class JourneyDropsMixin {
 @WrapMethod(method="dropEquipment") private void mortalExpansion$escrow(Operation<Void> original){if((Object)this instanceof ServerPlayer p)com.howierux.mortalexpansion.travel.DeferredDeathItems.captureDeath(p,()->original.call());else original.call();}
}
