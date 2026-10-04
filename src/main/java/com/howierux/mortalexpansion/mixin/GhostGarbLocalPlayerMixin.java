package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla's client-only wall ejection must not fight the shared phasing solver. */
@Mixin(LocalPlayer.class)
public abstract class GhostGarbLocalPlayerMixin {
    @Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
    private void mortalExpansion$stayInWalls(double x, double z, CallbackInfo ci) {
        if (GhostGarbPhasing.active((LocalPlayer)(Object)this)) ci.cancel();
    }
}
