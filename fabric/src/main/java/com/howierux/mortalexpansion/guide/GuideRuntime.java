package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.machine.AltarEntity;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import java.util.*;

/** Book/research lifecycle; natural Haunt selection belongs to HauntEventController. */
public final class GuideRuntime {
 public static long now(ServerPlayer p){return p.getServer().overworld().getGameTime();}
 public static void completed(ServerPlayer p){((AttachmentTarget)p).setAttached(KnowledgeData.NEXT_INSIGHT,now(p)+1200);var state=com.howierux.mortalexpansion.haunt.effects.HauntProtection.state(p);state.putLong("BenefitCooldownUntil",now(p)+1200);com.howierux.mortalexpansion.haunt.effects.HauntProtection.state(p,state);}
 public static double rollChance(int tier){return switch(tier){case 1->.02;case 2->.05;case 3->.08;case 4->.12;case 5->.16;case 6->.20;default->0;};}
 public static void initialize(){
  DarkholdProgression.initialize();KnowledgeData.initialize();BookRecipes.initialize();PayloadTypeRegistry.playS2C().register(BookOpenPayload.TYPE,BookOpenPayload.CODEC);
  ServerTickEvents.END_WORLD_TICK.register(level->{if(level.getGameTime()%20!=0)return;for(var p:level.players()){
   DarkholdProgression.pulse(p);SpectralInsight.absorbAll(p);MortalityResearch.complete(p);hint(p);
  }});
  UseBlockCallback.EVENT.register((player,world,hand,hit)->{
   if(player instanceof ServerPlayer p&&world.getBlockEntity(hit.getBlockPos()) instanceof net.minecraft.world.level.block.entity.CrafterBlockEntity crafter){var target=(AttachmentTarget)crafter;target.setAttached(KnowledgeData.CRAFTER,KnowledgeData.get(target,KnowledgeData.CRAFTER)|SpectralInsight.knowledge(p));crafter.setChanged();}
   // Missing function: integrate knowledge with other mods' owned automated crafting machines.
   return InteractionResult.PASS;
  });
  CommandRegistrationCallback.EVENT.register((dispatcher,lookup,environment)->dispatcher.register(Commands.literal("mortalexpansion").requires(s->s.hasPermission(2)).then(Commands.literal("insight").executes(c->SpectralInsight.perform(c.getSource().getPlayerOrException())?1:0).then(Commands.argument("targets",EntityArgument.players()).executes(c->{int count=0;for(var p:EntityArgument.getPlayers(c,"targets"))if(SpectralInsight.perform(p))count++;return count;})))));
 }
 private static void hint(ServerPlayer p){
  if(KnowledgeData.get((AttachmentTarget)p,KnowledgeData.HINTED)!=0)return;
  var advancement=p.getServer().getAdvancements().get(ResourceLocation.withDefaultNamespace("adventure/kill_a_mob"));
  if(advancement!=null&&p.getAdvancements().getOrStartProgress(advancement).isDone()){((AttachmentTarget)p).setAttached(KnowledgeData.HINTED,1);p.sendSystemMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.ectcetera.crafting_hint"));}
 }
}
