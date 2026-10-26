package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import com.howierux.mortalexpansion.travel.JourneyRuntime;
/** Select a valid grave before vanilla can spend an unrelated Respawn Anchor charge. Failed journeys use vanilla. */
@Mixin(ServerPlayer.class)
public abstract class JourneySpawnPointMixin {
 @Inject(method="findRespawnPositionAndUseSpawnBlock",at=@At("HEAD"),cancellable=true)
 private void mortalExpansion$graveSpawn(boolean alive,DimensionTransition.PostDimensionTransition after,CallbackInfoReturnable<DimensionTransition> cir){var p=(ServerPlayer)(Object)this;if(alive||!p.isDeadOrDying()||p.getServer().isHardcore())return;boolean journey=JourneyRuntime.data(p).getBoolean("PendingJourney");if(!JourneyRuntime.body(p).isEmpty()&&!journey)return;var grave=JourneyRuntime.graveTarget(p);if(grave==null)return;var at=journey?grave.pos().above().getBottomCenter():JourneyRuntime.safe(p,grave.level(),grave.pos());if(at!=null)cir.setReturnValue(new DimensionTransition(grave.level(),at,net.minecraft.world.phys.Vec3.ZERO,p.getYRot(),0,after));}
}
