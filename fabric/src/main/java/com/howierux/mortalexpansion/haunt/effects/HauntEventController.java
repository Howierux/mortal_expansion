package com.howierux.mortalexpansion.haunt.effects;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.level.*;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import java.util.*;
public final class HauntEventController {
 public static final SoundEvent TAPPING=net.minecraft.sounds.SoundEvents.WOOD_HIT;
 public static void initialize(){HauntState.initialize();
  PayloadTypeRegistry.playS2C().register(com.howierux.mortalexpansion.network.HauntEffectPayloads.Shadow.TYPE,com.howierux.mortalexpansion.network.HauntEffectPayloads.Shadow.CODEC);
  ServerTickEvents.END_WORLD_TICK.register(HauntEventController::tick);
  CommandRegistrationCallback.EVENT.register((dispatcher,lookup,env)->dispatcher.register(Commands.literal("mortalexpansion").requires(source->source.hasPermission(2)).then(Commands.literal("haunt").then(Commands.argument("targets",EntityArgument.players()).then(Commands.argument("effect",com.mojang.brigadier.arguments.StringArgumentType.word()).suggests((context,builder)->{for(var kind:HauntEffectKind.values())builder.suggest(kind.name().toLowerCase(Locale.ROOT));for(var key:List.of("random","positive","negative"))builder.suggest(key);return builder.buildFuture();}).executes(context->{String key=com.mojang.brigadier.arguments.StringArgumentType.getString(context,"effect");int successes=0;for(var p:EntityArgument.getPlayers(context,"targets")){HauntEffectKind kind;try{kind=HauntEffectKind.valueOf(key.toUpperCase(Locale.ROOT));}catch(IllegalArgumentException error){var choices=Arrays.stream(HauntEffectKind.values()).filter(HauntEffectKind::implemented).filter(k->key.equals("random")||key.equals("positive")&&!k.negative||key.equals("negative")&&k.negative).toList();if(choices.isEmpty())continue;kind=choices.get(p.getRandom().nextInt(choices.size()));}if(trigger(p,kind,true))successes++;}int count=successes;context.getSource().sendSuccess(()->net.minecraft.network.chat.Component.literal("Triggered or prevented "+count+" Haunt effect(s). Unavailable targets/effects were skipped."),false);return successes;}))))));
 }
 public static boolean ready(ServerPlayer p,boolean negative){var state=HauntProtection.state(p);long now=HauntProtection.now(p);return state.getLong(negative?"CooldownUntil":"BenefitCooldownUntil")<=now&&state.getLong(negative?"ActiveUntil":"BenefitActiveUntil")<=now;}
 public static boolean ready(ServerPlayer p){return ready(p,true);}
 public static void completed(ServerPlayer p,int ticks){completed(p,ticks,true);}
 public static void completed(ServerPlayer p,int ticks,boolean negative){var state=HauntProtection.state(p);long now=HauntProtection.now(p);state.putLong(negative?"CooldownUntil":"BenefitCooldownUntil",now+1200);state.putLong(negative?"ActiveUntil":"BenefitActiveUntil",now+ticks);HauntProtection.state(p,state);if(!negative&&ticks==0)GuideRuntime.completed(p);}
 public static boolean trigger(ServerPlayer p,HauntEffectKind kind,boolean forced){
  if(!p.isAlive()||p.isSpectator()||!kind.implemented()||!forced&&(!ready(p,kind.negative)||PlayerHauntManager.getTier(p).id()<kind.tier))return false;
  if(kind.negative&&HauntProtection.fullGarb(p)){completed(p,0,true);com.howierux.mortalexpansion.equipment.EquipmentRuntime.preventHaunting(p);return true;}
  if(kind.negative&&HauntProtection.protectedPlayer(p))return false;
  long pending=HauntEventData.get(p.serverLevel()).pendingGhosts();if(!HauntEffects.perform(p,kind))return false;
  if(com.howierux.mortalexpansion.haunt.Dreamcatchers.consumeFeedback(p)){completed(p,0,kind.negative);return true;}
  if(kind==HauntEffectKind.GHOST&&HauntEventData.get(p.serverLevel()).pendingGhosts()>pending)return true;
  completed(p,kind.activeTicks,kind.negative);hint(p,kind);return true;
 }
 public static boolean tryEffect(ServerPlayer p,HauntEffectKind kind){return trigger(p,kind,false);}
 public static void hint(ServerPlayer p,HauntEffectKind kind){if(com.howierux.mortalexpansion.haunt.Dreamcatchers.consumeFeedback(p))return;if(kind!=HauntEffectKind.SPECTRAL_INSIGHT)p.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.haunt."+kind.name().toLowerCase(Locale.ROOT)),false);}
 public static void rollPool(ServerPlayer p,boolean negative){
  int tier=PlayerHauntManager.getTier(p).id();if(!ready(p,negative)||p.getRandom().nextDouble()>=HauntEffectKind.chance(tier))return;
  if(negative&&HauntProtection.protectedPlayer(p)&&!HauntProtection.fullGarb(p))return;
  var choices=new ArrayList<>(Arrays.stream(HauntEffectKind.values()).filter(k->k.negative==negative&&k.implemented()&&tier>=k.tier).toList());
  boolean research=!negative&&SpectralInsight.ready(p)&&MortalityResearch.bonus(p)>0;
  if(research&&p.getRandom().nextDouble()<MortalityResearch.chance(4.0/Math.max(4,choices.stream().mapToInt(k->k.weight).sum()),MortalityResearch.bonus(p))&&trigger(p,HauntEffectKind.SPECTRAL_INSIGHT,false)){MortalityResearch.spent(p);return;}
  while(!choices.isEmpty()){int roll=p.getRandom().nextInt(choices.stream().mapToInt(k->k.weight).sum());HauntEffectKind chosen=choices.getLast();for(var kind:choices){roll-=kind.weight;if(roll<0){chosen=kind;break;}}choices.remove(chosen);if(trigger(p,chosen,false)){if(research)MortalityResearch.spent(p);return;}}
 }
 public static void tick(ServerLevel level){
  if(level.getGameTime()%20!=0)return;AdditionalHauntEffects.tick(level);var events=HauntEventData.get(level);
  for(var p:level.players()){if(HauntProtection.protectedPlayer(p))HauntProtection.clearNegative(p);if(!p.isAlive()||p.isSpectator()||p.isCreative())continue;
   if(events.pendingGhosts()>0&&ready(p,true)&&!HauntProtection.protectedPlayer(p)&&(com.howierux.mortalexpansion.haunt.Dreamcatchers.prevent(p)||HauntEffects.spawnGhostNear(p))){events.deliveredGhost();completed(p,0,true);hint(p,HauntEffectKind.GHOST);break;}
  }
  if(level.getGameTime()%600==0){events.prune(level.getServer().overworld().getGameTime());for(var p:level.players())if(p.isAlive()&&!p.isSpectator()&&!p.isCreative()){rollPool(p,false);rollPool(p,true);}}
 }
}
