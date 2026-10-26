package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
@Mixin(SpawnPlacements.class)
public abstract class VanillaHauntSpawnsMixin {
 @Inject(method="checkSpawnRules",at=@At("RETURN"),cancellable=true)private static void mortalExpansion$hauntWeights(EntityType<?> type,ServerLevelAccessor world,MobSpawnType reason,BlockPos pos,RandomSource random,CallbackInfoReturnable<Boolean> cir){if(cir.getReturnValue()&&reason==MobSpawnType.NATURAL&&world instanceof net.minecraft.server.level.ServerLevel level&&level.dimension()==net.minecraft.world.level.Level.OVERWORLD&&level.hasChunkAt(pos)){float chance=com.howierux.mortalexpansion.content.VanillaHauntSpawns.acceptance(type,com.howierux.mortalexpansion.haunt.HauntManager.getTier(level,pos).id());if(chance<1&&random.nextFloat()>=chance)cir.setReturnValue(false);}}
}
