package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.haunt.HauntDaylightRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class HauntMovementMixin {
    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3 mortalExpansion$stayInSustainingHaunt(Vec3 movement, MoverType moverType, Vec3 original) {
        if (moverType == MoverType.SELF && (Object) this instanceof Mob mob) {
            return HauntDaylightRules.limitMovement(mob, movement);
        }
        return movement;
    }
}
