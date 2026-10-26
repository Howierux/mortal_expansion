package com.howierux.mortalexpansion.equipment;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

/** Native transient equipment attributes; callers use the same rules on both logical sides. */
public final class EquipmentRuntime {
 public static final DataComponentType<Integer> SCEPTER_CHARGE=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("scepter_charge"),DataComponentType.<Integer>builder().persistent(Codec.intRange(0,12)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
 public static final DataComponentType<Long> PICKAXE_REPAIR_TIME=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("pickaxe_repair_time"),DataComponentType.<Long>builder().persistent(Codec.LONG).build());
 private static final net.minecraft.resources.ResourceLocation JUMP=Catalog.id("haint_garb_jump"),POWER=Catalog.id("bloodstone_power"),SPEED=Catalog.id("bloodstone_speed");
 public static void initialize(){EquipmentMining.initialize();}
 public static boolean fullGarb(Player p){return full(p,false);}
 public static boolean fullHaint(Player p){return full(p,true);}
 public static final net.minecraft.tags.TagKey<Item> GARB=net.minecraft.tags.TagKey.create(Registries.ITEM,Catalog.id("spectral_garb")),HAINT=net.minecraft.tags.TagKey.create(Registries.ITEM,Catalog.id("haints_garb"));
 private static boolean full(Player p,boolean haint){if(p.isSpectator())return false;for(var stack:p.getArmorSlots())if(stack.isEmpty()||!stack.is(haint?HAINT:GARB))return false;return true;}
 public static boolean bloodstone(ItemStack s){return s.is(CatalogItems.BLOODSTONE_SWORD)||s.is(CatalogItems.BLOODSTONE_PICKAXE)||s.is(CatalogItems.BLOODSTONE_AXE)||s.is(CatalogItems.BLOODSTONE_SHOVEL)||s.is(CatalogItems.BLOODSTONE_HOE);}
 public static double multiplier(HauntTier tier){return 1+.1*(Math.clamp(tier.id(),0,6)-1);}
 public static float mining(Player p,float original){return bloodstone(p.getMainHandItem())&&!p.isSpectator()?(float)(original*multiplier(PlayerHauntManager.getTier(p))):original;}
 public static void tick(Player p){
  if(p instanceof net.minecraft.server.level.ServerPlayer server)com.howierux.mortalexpansion.haunt.effects.HauntProtection.equipmentTick(server);
  modifier(p,Attributes.JUMP_STRENGTH,JUMP,fullHaint(p)?.3:0,AttributeModifier.Operation.ADD_VALUE);
  double bonus=!p.isSpectator()&&bloodstone(p.getMainHandItem())?multiplier(PlayerHauntManager.getTier(p))-1:0;
  modifier(p,Attributes.ATTACK_DAMAGE,POWER,bonus,AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
  modifier(p,Attributes.ATTACK_SPEED,SPEED,bonus,AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
 }
 private static void modifier(Player p,Holder<Attribute> attribute,net.minecraft.resources.ResourceLocation id,double amount,AttributeModifier.Operation op){
  var current=p.getAttribute(attribute);if(current==null)return;
  var prior=current.getModifier(id);if(prior!=null&&prior.amount()==amount)return;
  current.removeModifier(id);if(amount!=0)current.addTransientModifier(new AttributeModifier(id,amount,op));
 }
 /** Dispatch after choosing a negative Haunt roll, so protection does not increase positive-event odds.
  * The Haunt dispatcher calls this gate for both natural and operator-selected risks. */
 public static boolean preventHaunting(net.minecraft.server.level.ServerPlayer p){
  if(!fullGarb(p))return false;
  p.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.haunt.garb_prevented"),true);return true;
 }
 public static boolean loaded(Level level,BlockPos center,int radius){
  for(int x=(center.getX()-radius)>>4;x<=(center.getX()+radius)>>4;x++)for(int z=(center.getZ()-radius)>>4;z<=(center.getZ()+radius)>>4;z++)if(!level.hasChunk(x,z))return false;return true;
 }
}
