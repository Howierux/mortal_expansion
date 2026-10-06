package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GarbFeedbackGameTests {
    private static void equip(net.minecraft.server.level.ServerPlayer p,Item... pieces) {
        var slots=new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
        for(int i=0;i<4;i++)p.setItemSlot(slots[i],new ItemStack(pieces[i]));
    }
    @GameTest(template="deferred_items_test")
    public static void all_spectral_sets_roll_and_prevent_risks_without_spending_benefit_cooldown(GameTestHelper h) {
        Item[][] sets={
            {ModItems.GHOST_GARB_HELMET.get(),ModItems.GHOST_GARB_CHESTPLATE.get(),ModItems.GHOST_GARB_LEGGINGS.get(),ModItems.GHOST_GARB_BOOTS.get()},
            {ModItems.HAINT_GARB_HELMET.get(),ModItems.HAINT_GARB_CHESTPLATE.get(),ModItems.HAINT_GARB_LEGGINGS.get(),ModItems.HAINT_GARB_BOOTS.get()},
            {ModItems.SPECTRALITE_HAINT_HELMET.get(),ModItems.SPECTRALITE_HAINT_CHESTPLATE.get(),ModItems.SPECTRALITE_HAINT_LEGGINGS.get(),ModItems.SPECTRALITE_HAINT_BOOTS.get()}};
        for(var set:sets) {
            var p=h.makeMockServerPlayerInLevel();equip(p,set);PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);p.getRandom().setSeed(42);
            long pending=HauntEventData.get(h.getLevel()).pendingGhosts();
            for(int i=0;i<1000 && HauntEventController.ready(p,true);i++)HauntEventController.rollPool(p,true);
            var state=HauntProtection.state(p);long now=HauntProtection.now(p);
            h.assertTrue(state.getLong("CooldownUntil")==now+1200,"A successful protected roll starts normal risk cooldown");
            h.assertTrue(state.getLong("ActiveUntil")==now && HauntEventController.ready(p,false),"No negative active duration and independent benefits remain ready");
            h.assertTrue(HauntEventData.get(h.getLevel()).pendingGhosts()==pending,"Blocked rolls cannot queue ghosts");
            var before=state.copy();HauntEventController.rollPool(p,true);h.assertTrue(HauntProtection.state(p).equals(before),"Cooldown prevents repeated notification rolls");
            h.assertTrue(HauntEventController.tryEffect(p,HauntEffectKind.GOOD_POTION),"Gear still permits independent benefits");p.discard();
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void void_and_failed_rolls_do_not_report_prevention(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();equip(p,ModItems.GHOST_GARB_HELMET.get(),ModItems.GHOST_GARB_CHESTPLATE.get(),ModItems.GHOST_GARB_LEGGINGS.get(),ModItems.GHOST_GARB_BOOTS.get());
        PlayerHauntManager.setTier(p,HauntTier.VOID);
        for(int i=0;i<20;i++)HauntEventController.rollPool(p,true);
        h.assertTrue(HauntEventController.ready(p,true),"Void never produces a prevented risk");
        PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);p.getRandom().setSeed(42);HauntEventController.rollPool(p,true);
        h.assertTrue(HauntEventController.ready(p,true),"Failed chance roll does not create feedback or cooldown");p.discard();h.succeed();
    }
}
