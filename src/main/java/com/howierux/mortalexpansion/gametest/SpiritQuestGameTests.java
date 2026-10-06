package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.quest.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritQuestGameTests {
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setPos(h.absolutePos(new net.minecraft.core.BlockPos(7,2,7)).getCenter());return p;
    }
    private static SpiritQuest quest(EntityType<?> type,int count,int difficulty){return new SpiritQuest(UUID.randomUUID().toString(),BuiltInRegistries.ENTITY_TYPE.getKey(type),count,0,difficulty,"Departed Spirit",Long.MAX_VALUE);}
    private static void active(net.minecraft.server.level.ServerPlayer p,SpiritQuest q){p.setData(ModAttachments.SPIRIT_QUESTS,new SpiritQuestState(Optional.empty(),Optional.of(q),0));}
    private static int count(net.minecraft.server.level.ServerPlayer p,Item item){return p.getInventory().items.stream().filter(s->s.is(item)).mapToInt(ItemStack::getCount).sum();}
    @GameTest(template="deferred_items_test")
    public static void spirit_offers_require_acceptance_and_survive_giver_departure(GameTestHelper h) throws Exception {
        var p=player(h);var spirit=ModEntities.SPIRIT.get().create(h.getLevel());p.setShiftKeyDown(true);spirit.mobInteract(p,net.minecraft.world.InteractionHand.MAIN_HAND);
        var offered=SpiritQuests.state(p).offer().orElseThrow();h.assertTrue(SpiritQuests.state(p).active().isEmpty(),"Spirit interaction only offers a quest");
        h.assertFalse(SpiritQuests.accept(p,"stale"),"Stale chat tokens cannot accept another offer");
        var dispatcher=new com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>();SpiritQuestCommands.register(dispatcher);
        h.assertTrue(dispatcher.execute("spiritquest accept "+offered.id(),p.createCommandSourceStack().withPermission(0))==1,"Ordinary players can accept their own offer");
        spirit.discard();var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());p.setData(ModAttachments.SPIRIT_QUESTS,SpiritQuestState.EMPTY);p.load(saved);
        h.assertTrue(SpiritQuests.state(p).active().orElseThrow().equals(offered),"Objective survives saving without any live giver");
        h.assertFalse(SpiritQuests.offer(p,"Another spirit"),"Active quests cannot be overwritten");
        h.assertTrue(SpiritQuests.abandon(p) && SpiritQuests.state(p).active().isEmpty(),"Player can abandon an active task");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void native_kills_credit_only_the_owner_and_pay_exactly_once(GameTestHelper h){
        var p=player(h);var other=player(h);var q=quest(EntityType.ZOMBIE,2,2);active(p,q);
        var wrong=EntityType.COW.create(h.getLevel());wrong.setPos(p.position());wrong.hurt(h.getLevel().damageSources().playerAttack(p),1000);
        h.assertTrue(SpiritQuests.state(p).active().get().progress()==0,"Wrong mob type cannot advance");
        var stolen=EntityType.ZOMBIE.create(h.getLevel());stolen.setPos(p.position());stolen.hurt(h.getLevel().damageSources().playerAttack(other),1000);
        h.assertTrue(SpiritQuests.state(p).active().get().progress()==0,"Another player's kill cannot advance the owner quest");
        for(int i=0;i<2;i++){var mob=EntityType.ZOMBIE.create(h.getLevel());mob.setPos(p.position());mob.hurt(h.getLevel().damageSources().playerAttack(p),1000);}
        h.assertTrue(SpiritQuests.state(p).active().isEmpty() && count(p,Items.EMERALD)==4 && count(p,ModItems.RAW_ECTOPLASM.get())==2,"Final native death delivers rewards without returning to giver");
        int xp=p.totalExperience;h.assertFalse(SpiritQuests.credit(p,EntityType.ZOMBIE),"Completed task cannot award again");h.assertTrue(p.totalExperience==xp && xp==40,"XP is delivered exactly once");p.discard();other.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void reward_tiers_and_full_inventory_overflow_are_preserved(GameTestHelper h){
        for(int difficulty=1;difficulty<=3;difficulty++){var p=player(h);active(p,quest(EntityType.ZOMBIE,1,difficulty));SpiritQuests.credit(p,EntityType.ZOMBIE);
            h.assertTrue(count(p,Items.EMERALD)==difficulty*2 && p.totalExperience==difficulty*20,"Harder tasks increase emeralds and XP");
            h.assertTrue(count(p,difficulty==3?ModItems.PURE_ECTOPLASM.get():ModItems.RAW_ECTOPLASM.get())==(difficulty==3?1:difficulty),"Dangerous tasks upgrade material rewards");p.discard();}
        var p=player(h);for(int i=0;i<36;i++)p.getInventory().setItem(i,new ItemStack(Items.STONE,64));active(p,quest(EntityType.ZOMBIE,1,3));SpiritQuests.credit(p,EntityType.ZOMBIE);
        var drops=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,p.getBoundingBox().inflate(3),e->e.getItem().is(Items.EMERALD)||e.getItem().is(ModItems.PURE_ECTOPLASM.get()));
        h.assertTrue(drops.stream().mapToInt(e->e.getItem().getCount()).sum()==7,"Full inventory drops all reward items instead of losing them");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void hostile_bias_and_haunt_commission_need_no_permanent_spirit(GameTestHelper h){
        var p=player(h);p.getRandom().setSeed(42);int hostile=0;
        for(int i=0;i<400;i++){p.setData(ModAttachments.SPIRIT_QUESTS,SpiritQuestState.EMPTY);h.assertTrue(SpiritQuests.offer(p,"A restless spirit"),"Generated pools provide targets");
            if(BuiltInRegistries.ENTITY_TYPE.get(SpiritQuests.state(p).offer().get().target()).getCategory()==MobCategory.MONSTER)hostile++;}
        h.assertTrue(hostile>260 && hostile<340,"Selection favors hostile types at approximately 75 percent");
        p.setData(ModAttachments.SPIRIT_QUESTS,SpiritQuestState.EMPTY);h.assertTrue(com.howierux.mortalexpansion.haunt.effects.HauntEffects.perform(p,com.howierux.mortalexpansion.haunt.effects.HauntEffectKind.SPIRIT_COMMISSION),"Haunt commissions create an optional offer directly");
        h.assertTrue(SpiritQuests.state(p).active().isEmpty(),"A Haunt event cannot auto-accept a quest");
        var offer=SpiritQuests.state(p).offer().get();h.assertTrue(SpiritQuests.decline(p,offer.id()),"Offer can be declined");h.assertFalse(SpiritQuests.offer(p,"Reroll"),"Offer cooldown prevents immediate reward rerolls");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void expired_offers_and_canceled_deaths_cannot_grant_progress(GameTestHelper h){
        var p=player(h);var expired=new SpiritQuest("expired",BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE),1,0,2,"Spirit",0);
        p.setData(ModAttachments.SPIRIT_QUESTS,new SpiritQuestState(Optional.of(expired),Optional.empty(),0));h.assertFalse(SpiritQuests.accept(p,"expired"),"Expired offers cannot be accepted");
        active(p,quest(EntityType.ZOMBIE,1,2));var mob=EntityType.ZOMBIE.create(h.getLevel());mob.setPos(p.position());
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.living.LivingDeathEvent> veto=e->{if(e.getEntity()==mob)e.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.HIGHEST,veto);
        try{mob.hurt(h.getLevel().damageSources().playerAttack(p),1000);h.assertTrue(SpiritQuests.state(p).active().get().progress()==0 && count(p,Items.EMERALD)==0,"Canceled native death cannot pay rewards");}
        finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(veto);mob.discard();p.discard();}h.succeed();
    }
}
