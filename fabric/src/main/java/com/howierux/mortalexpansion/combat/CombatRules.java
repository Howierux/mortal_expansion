package com.howierux.mortalexpansion.combat;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import com.howierux.mortalexpansion.entity.*;
import com.howierux.mortalexpansion.entity.mob.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.*;
public final class CombatRules {
 public static boolean allowed(LivingEntity target,DamageSource source){return GhostGarbBreathing.garbDrowning(target,source)||source.is(DamageTypes.FELL_OUT_OF_WORLD)||!IntangibleEntities.isIntangible(target)||DemiTangibleDamage.canDamageIntangible(source);}
 public static float amount(LivingEntity target,DamageSource source,float amount){
  if(source.is(DamageTypes.FELL_OUT_OF_WORLD))return amount;
  if(source.getEntity() instanceof net.minecraft.server.level.ServerPlayer p&&source.getDirectEntity()==p&&com.howierux.mortalexpansion.haunt.effects.HauntProtection.state(p).getLong("EmpoweredUntil")>com.howierux.mortalexpansion.haunt.effects.HauntProtection.now(p)&&DemiTangibleDamage.isDemiTangibleWeapon(p.getMainHandItem())&&DemiTangibleDamage.canDamageIntangible(source))amount*=1.25F;
  if(source.getEntity()!=null&&IntangibleEntities.isIntangible(source.getEntity())){int pieces=0;for(var armor:target.getArmorSlots())if(armor.is(ModCombatItemTags.SPECTRAL_PROTECTIVE_ARMOR))pieces++;amount*=1-.05F*Math.min(4,pieces);}
  if(target instanceof PharaohlEntity boss&&!boss.isCurrentlyIntangible())return amount*(DemiTangibleDamage.canDamageIntangible(source)?.5F:1.5F);
  if(IntangibleEntities.isIntangible(target)&&DemiTangibleDamage.isDemiTangibleWeaponDamage(source)&&DemiTangibleDamage.weapon(source).is(CatalogItems.PURIFIED_GRAVESTONE_SWORD))amount*=1.5F;
  if(target instanceof DemonEntity&&DemiTangibleDamage.isDemiTangibleWeaponDamage(source))amount*=.5F;
  return amount;
 }
}
