package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.entity.mob.HauntDaylightShelterGoal;
import com.howierux.mortalexpansion.haunt.HauntDaylightRules;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class HauntMobMixin {
    @Shadow @Final protected GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mortalExpansion$addShelterGoal(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob instanceof PathfinderMob pathfinder && HauntDaylightRules.applies(mob)) {
            goalSelector.addGoal(1, new HauntDaylightShelterGoal(pathfinder));
        }
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void mortalExpansion$preventHauntedSunburn(CallbackInfoReturnable<Boolean> cir) {
        if (HauntDaylightRules.sunlightProtected((Mob) (Object) this)) cir.setReturnValue(false);
    }
}
