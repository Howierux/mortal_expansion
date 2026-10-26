package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.equipment.EquipmentRuntime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(Player.class)
public abstract class EquipmentPlayerMixin {
 @Inject(method="tick",at=@At("HEAD")) private void mortalExpansion$equipment(CallbackInfo ci){EquipmentRuntime.tick((Player)(Object)this);}
 @Inject(method="getDestroySpeed",at=@At("RETURN"),cancellable=true) private void mortalExpansion$bloodstone(BlockState state,CallbackInfoReturnable<Float> cir){cir.setReturnValue(EquipmentRuntime.mining((Player)(Object)this,cir.getReturnValue()));}
}
