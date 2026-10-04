package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla rejects newly overlapping walls even after a custom collision solver.
 * Skip only this ordinary-wall check; speed and position validation remain active.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class GhostGarbServerMovementMixin {
    @Shadow public ServerPlayer player;
    @Inject(method = "isPlayerCollidingWithAnythingNew", at = @At("HEAD"), cancellable = true)
    private void mortalExpansion$allowWalls(LevelReader level, AABB box, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (GhostGarbPhasing.active(player)
                && com.howierux.mortalexpansion.entity.mob.SpectralPhasingRules.canEnterHorizontally(player, player.getBoundingBox())) cir.setReturnValue(false);
    }
}
