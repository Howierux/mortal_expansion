package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.guide.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Player.class)
public abstract class DamnedPlayerFoodMixin {
 @Inject(method="canEat",at=@At("HEAD"),cancellable=true)
 private void mortal$eatWhenFull(boolean always,CallbackInfoReturnable<Boolean> ci){
  var p=(Player)(Object)this;if(DamnedSecret.GLUTTONY.unlocked(p) && p.isAlive() && !p.isSpectator())ci.setReturnValue(true);
 }
 @Inject(method="getFoodData",at=@At("RETURN"))
 private void mortal$bindFood(CallbackInfoReturnable<FoodData> ci){((DamnedNutrition)ci.getReturnValue()).mortalExpansion$owner((Player)(Object)this);}
}
