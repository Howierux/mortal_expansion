package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.guide.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
/** Native hunger, exhaustion, healing, saving and packets remain unchanged; only saturation's upper clamp changes. */
@Mixin(FoodData.class)
public abstract class DamnedFoodDataMixin implements DamnedNutrition {
 @Unique private Player mortal$owner;
 @Override public void mortalExpansion$owner(Player p){mortal$owner=p;}
 @WrapOperation(method="add",at=@At(value="INVOKE",target="Lnet/minecraft/util/Mth;clamp(FFF)F"))
 private float mortal$uncapSaturation(float value,float min,float max,Operation<Float> original){
  if(mortal$owner!=null && DamnedAbilities.active(mortal$owner,DamnedSecret.GLUTTONY)){
   if(mortal$owner instanceof net.minecraft.server.level.ServerPlayer p)DamnedAbilities.hint(p,"gluttony");
   return Math.max(min,value);
  }
  return original.call(value,min,max);
 }
}
