package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** No supported event overrides Entity's collision solver; isolate that API here. */
@Mixin(Entity.class)
public abstract class GhostGarbCollisionMixin {
    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private void mortalExpansion$phase(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity)(Object)this;
        if (GhostGarbPhasing.active(entity)) cir.setReturnValue(GhostGarbPhasing.collide(entity, movement));
        else if(com.howierux.mortalexpansion.entity.mob.UpwardSpectralPhasing.active(entity,movement))
            cir.setReturnValue(com.howierux.mortalexpansion.entity.mob.UpwardSpectralPhasing.collide(entity,movement));
    }
}
