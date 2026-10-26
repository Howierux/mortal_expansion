package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.equipment.EquipmentMining;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerPlayerGameMode.class)
public abstract class EquipmentMiningMixin {
 @Shadow protected ServerPlayer player;
 @Inject(method="handleBlockBreakAction",at=@At("HEAD")) private void mortalExpansion$face(BlockPos pos,ServerboundPlayerActionPacket.Action action,Direction face,int height,int sequence,CallbackInfo ci){if(action==ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK)EquipmentMining.face(player,pos,face,true);else if(action==ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK)EquipmentMining.face(player,pos,face,false);}
 @WrapMethod(method="destroyBlock") private boolean mortalExpansion$harvest(BlockPos pos,Operation<Boolean> original){var plan=EquipmentMining.plan(player,pos);if(plan==null)return original.call(pos);var before=player.serverLevel().getBlockState(pos);boolean result=original.call(pos);EquipmentMining.after(player,pos,plan,before,result);return result;}
}
