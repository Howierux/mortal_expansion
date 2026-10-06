package com.howierux.mortalexpansion.quest;

import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import java.util.*;

public final class SpiritQuests {
    public static final TagKey<EntityType<?>> EASY=tag("quest_targets/easy"),STANDARD=tag("quest_targets/standard"),HARD=tag("quest_targets/hard");
    private static TagKey<EntityType<?>> tag(String path){return TagKey.create(Registries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath("mortal_expansion",path));}
    private SpiritQuests(){}
    public static SpiritQuestState state(ServerPlayer p){return p.getData(ModAttachments.SPIRIT_QUESTS);}
    private static void state(ServerPlayer p,SpiritQuestState state){p.setData(ModAttachments.SPIRIT_QUESTS,state);}
    private static long now(ServerPlayer p){return p.server.overworld().getGameTime();}
    public static int difficulty(EntityType<?> type){return type.is(HARD)?3:type.is(STANDARD)?2:type.is(EASY)?1:0;}
    public static List<EntityType<?>> candidates(ServerPlayer p,boolean hostile){
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type->difficulty(type)>0
            && (type.getCategory()==MobCategory.MONSTER)==hostile
            && (p.serverLevel().getDifficulty()!=net.minecraft.world.Difficulty.PEACEFUL || !hostile)).toList();
    }
    public static boolean offer(ServerPlayer p,String giver){return offer(p,giver,true);}
    public static boolean offer(ServerPlayer p,String giver,boolean announce){
        var state=state(p);long now=now(p);
        if(state.active().isPresent() || state.offer().filter(q->q.expires()>now).isPresent() || state.offerAfter()>now)return false;
        boolean hostile=p.getRandom().nextInt(4)!=0;var pool=candidates(p,hostile);
        if(pool.isEmpty())pool=candidates(p,!hostile);if(pool.isEmpty())return false;
        var type=pool.get(p.getRandom().nextInt(pool.size()));int difficulty=difficulty(type);
        int count=difficulty==2?3+p.getRandom().nextInt(3):1+p.getRandom().nextInt(3);
        var quest=new SpiritQuest(UUID.randomUUID().toString(),BuiltInRegistries.ENTITY_TYPE.getKey(type),count,0,difficulty,giver,now+6000);
        state(p,new SpiritQuestState(Optional.of(quest),Optional.empty(),now+1200));if(announce)showOffer(p,quest);return true;
    }
    private static Component target(SpiritQuest q){return Component.translatable(BuiltInRegistries.ENTITY_TYPE.get(q.target()).getDescriptionId());}
    private static String tier(int difficulty){return switch(difficulty){case 1->"Easy";case 2->"Standard";default->"Dangerous";};}
    public static Component describe(SpiritQuest q){return Component.translatable("message.mortal_expansion.quest.offer",q.giver(),q.required(),target(q),tier(q.difficulty()),rewardDescription(q));}
    public static void showOffer(ServerPlayer p,SpiritQuest q){
        p.sendSystemMessage(describe(q));
        p.sendSystemMessage(Component.literal("[Accept] ").withStyle(s->s.withColor(net.minecraft.ChatFormatting.GREEN)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/spiritquest accept "+q.id())))
            .append(Component.literal("[Decline]").withStyle(s->s.withColor(net.minecraft.ChatFormatting.GRAY)
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/spiritquest decline "+q.id())))));
    }
    public static String rewardDescription(SpiritQuest q){int d=q.difficulty();return (d*2)+" emeralds, "+(d==3?"1 purified ectoplasm":d+" ectoplasm")+", "+(d*20)+" XP";}
    public static boolean accept(ServerPlayer p,String id){
        var state=state(p);var q=state.offer().orElse(null);
        if(q==null || !q.id().equals(id) || q.expires()<=now(p) || state.active().isPresent())return false;
        state(p,new SpiritQuestState(Optional.empty(),Optional.of(q),state.offerAfter()));
        p.sendSystemMessage(Component.translatable("message.mortal_expansion.quest.accepted",q.required(),target(q)));return true;
    }
    public static boolean decline(ServerPlayer p,String id){
        var state=state(p);if(state.offer().isEmpty() || !state.offer().get().id().equals(id))return false;
        state(p,new SpiritQuestState(Optional.empty(),state.active(),state.offerAfter()));return true;
    }
    public static boolean abandon(ServerPlayer p){
        var state=state(p);if(state.active().isEmpty())return false;
        state(p,new SpiritQuestState(state.offer(),Optional.empty(),state.offerAfter()));return true;
    }
    public static void status(ServerPlayer p){
        var state=state(p);
        if(state.active().isPresent()){var q=state.active().get();p.sendSystemMessage(Component.translatable("message.mortal_expansion.quest.progress",target(q),q.progress(),q.required()));}
        else if(state.offer().filter(q->q.expires()>now(p)).isPresent())showOffer(p,state.offer().get());
        else p.sendSystemMessage(Component.translatable("message.mortal_expansion.quest.none"));
    }
    public static boolean credit(ServerPlayer p,EntityType<?> type){
        var state=state(p);var q=state.active().orElse(null);
        if(q==null || !q.target().equals(BuiltInRegistries.ENTITY_TYPE.getKey(type)))return false;
        var next=q.advance();boolean done=next.progress()>=next.required();
        // Clear the objective before delivering rewards: duplicate death notifications cannot pay twice.
        state(p,new SpiritQuestState(state.offer(),done?Optional.empty():Optional.of(next),state.offerAfter()));
        if(done){
            deliver(p,new ItemStack(Items.EMERALD,q.difficulty()*2));
            deliver(p,new ItemStack(q.difficulty()==3?ModItems.PURE_ECTOPLASM.get():ModItems.RAW_ECTOPLASM.get(),q.difficulty()==3?1:q.difficulty()));
            p.giveExperiencePoints(q.difficulty()*20);
            p.sendSystemMessage(Component.translatable("message.mortal_expansion.quest.complete",q.giver(),rewardDescription(q)));
        }else p.displayClientMessage(Component.translatable("message.mortal_expansion.quest.progress",target(next),next.progress(),next.required()),true);
        return true;
    }
    private static void deliver(ServerPlayer p,ItemStack stack){
        p.getInventory().add(stack);
        if(!stack.isEmpty()){var drop=p.drop(stack,false);if(drop!=null){drop.setTarget(p.getUUID());drop.setNoPickUpDelay();}}
        p.containerMenu.broadcastChanges();
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void killed(LivingDeathEvent event){
        if(!(event.getEntity() instanceof Mob mob) || !(event.getSource().getEntity() instanceof ServerPlayer p))return;
        // Temporary haunting-owned encounters are not farmable quest targets.
        if(mob.getPersistentData().getBoolean(com.howierux.mortalexpansion.haunt.effects.HauntEffects.EVENT_MOB)
            || mob.getPersistentData().hasUUID(com.howierux.mortalexpansion.haunt.effects.AdditionalHauntEffects.TEMP_OWNER))return;
        credit(p,mob.getType());
    }
}
