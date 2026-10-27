package com.howierux.mortalexpansion.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
/** The sleep-finished event cannot cancel waking. Suppress vanilla morning skip during Sloth votes only. */
@Mixin(ServerLevel.class)
public abstract class DamnedDaySleepMixin {
    @WrapOperation(method="tick",at=@At(value="INVOKE",target="Lnet/minecraft/server/players/SleepStatus;areEnoughSleeping(I)Z"))
    private boolean mortal$dayVote(SleepStatus status,int percentage,Operation<Boolean> original){
        var level=(ServerLevel)(Object)this;
        if(level.dimension()==Level.OVERWORLD && Math.floorMod(level.getDayTime(),24000)<12000
                && com.howierux.mortalexpansion.guide.DamnedAbilities.dayVote(level))return false;
        return original.call(status,percentage);
    }
}
