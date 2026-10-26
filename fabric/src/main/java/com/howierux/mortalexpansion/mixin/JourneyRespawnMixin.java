package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
/** Attachment API copies through AFTER_RESPAWN; resolve only after all such callbacks have completed. */
@Mixin(PlayerList.class)
public abstract class JourneyRespawnMixin {
 @WrapMethod(method="respawn") private ServerPlayer mortalExpansion$resolvedRespawn(ServerPlayer old,boolean alive,Entity.RemovalReason reason,Operation<ServerPlayer> original){var p=original.call(old,alive,reason);p.connection.player=p;com.howierux.mortalexpansion.travel.JourneyRuntime.respawn(old,p,alive);return p;}
}
