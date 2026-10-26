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

/** Insight plus the two already functional altar benefits. Other Haunt-event pools remain pending. */
public final class GuideRuntime {
 public static long now(ServerPlayer p){return p.getServer().overworld().getGameTime();}
 public static void completed(ServerPlayer p){((AttachmentTarget)p).setAttached(KnowledgeData.NEXT_INSIGHT,now(p)+1200);}
 public static double rollChance(int tier){return switch(tier){case 1->.02;case 2->.05;case 3->.08;case 4->.12;case 5->.16;case 6->.20;default->0;};}
 public static void initialize(){
  KnowledgeData.initialize();BookRecipes.initialize();PayloadTypeRegistry.playS2C().register(BookOpenPayload.TYPE,BookOpenPayload.CODEC);
  ServerTickEvents.END_WORLD_TICK.register(level->{if(level.getGameTime()%20!=0)return;for(var p:level.players()){
   SpectralInsight.absorbAll(p);MortalityResearch.complete(p);hint(p);
   if(level.getGameTime()%600==0&&p.isAlive()&&!p.isSpectator()&&!p.isCreative()&&((AttachmentTarget)p).getAttachedOrCreate(KnowledgeData.NEXT_INSIGHT)<=now(p)&&p.getRandom().nextDouble()<rollChance(PlayerHauntManager.getTier(p).id()))rollBenefit(p);
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
 private record Benefit(int kind,int weight,AltarEntity altar){}
 private static void rollBenefit(ServerPlayer p){
  int tier=PlayerHauntManager.getTier(p).id();boolean insight=SpectralInsight.ready(p);var options=new ArrayList<Benefit>();if(insight)options.add(new Benefit(0,4,null));
  AltarEntity gift=null,blessing=null;double gd=Double.MAX_VALUE,bd=Double.MAX_VALUE;
  for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){var chunk=p.serverLevel().getChunkSource().getChunkNow(p.chunkPosition().x+dx,p.chunkPosition().z+dz);if(chunk==null)continue;
   for(var be:chunk.getBlockEntities().values())if(be instanceof AltarEntity altar&&!altar.isImpure()){
    double distance=p.distanceToSqr(altar.getBlockPos().getCenter());if(distance>256)continue;var input=altar.getItem(0);
    if(distance<gd&&(input.isEmpty()||input.is(CatalogItems.HOLY_SALT)&&input.getCount()<input.getMaxStackSize())){gift=altar;gd=distance;}
    int duration=PurityOfferings.burnTicks(input);if(tier>=2&&distance<bd&&!altar.hasBlessedOffering()&&duration>0&&duration+duration/2<=AltarEntity.MAX_BURN_TICKS-altar.getBurnTicks()){blessing=altar;bd=distance;}
   }
  }
  if(gift!=null)options.add(new Benefit(1,3,gift));if(blessing!=null)options.add(new Benefit(2,3,blessing));if(options.isEmpty())return;
  Benefit choice;int total=options.stream().mapToInt(Benefit::weight).sum();
  if(insight&&p.getRandom().nextDouble()<MortalityResearch.chance(4.0/total,MortalityResearch.bonus(p)))choice=options.stream().filter(o->o.kind()==0).findFirst().orElseThrow();
  else {var other=options.stream().filter(o->o.kind()!=0).toList();if(other.isEmpty())return;int roll=p.getRandom().nextInt(other.stream().mapToInt(Benefit::weight).sum());choice=other.getLast();for(var option:other){roll-=option.weight();if(roll<0){choice=option;break;}}}
  boolean success=choice.kind()==0?SpectralInsight.perform(p):choice.kind()==1?choice.altar().addOfferingGift():choice.altar().blessNextOffering();
  if(success){completed(p);if(insight&&MortalityResearch.bonus(p)>0)MortalityResearch.spent(p);if(choice.kind()!=0)p.displayClientMessage(net.minecraft.network.chat.Component.translatable(choice.kind()==1?"message.mortal_expansion.fabric.offering_gift":"message.mortal_expansion.fabric.offering_blessing"),false);}
 }
}
