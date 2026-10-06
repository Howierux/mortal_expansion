package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.quest.*;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.network.SpiritInteractionPayloads;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.gametest.*;
import net.minecraft.world.InteractionHand;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritMenuGameTests {
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h){var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setPos(h.absolutePos(new net.minecraft.core.BlockPos(7,2,7)).getCenter());return p;}
    private static com.howierux.mortalexpansion.entity.mob.SpiritEntity spirit(GameTestHelper h,net.minecraft.server.level.ServerPlayer p){var spirit=ModEntities.SPIRIT.get().create(h.getLevel());spirit.setPos(p.position());h.getLevel().addFreshEntity(spirit);return spirit;}
    @GameTest(template="deferred_items_test")
    public static void right_click_buttons_offer_accept_and_keep_active_progress(GameTestHelper h){
        var p=player(h);var spirit=spirit(h,p);spirit.mobInteract(p,InteractionHand.MAIN_HAND);
        h.assertTrue(!SpiritInteraction.token(p).isEmpty() && SpiritQuests.state(p).offer().isEmpty(),"Normal right click opens choices without creating a quest");
        h.assertTrue(SpiritInteraction.choose(p,SpiritInteraction.token(p),1),"Business button opens optional offer");
        var quest=SpiritQuests.state(p).offer().orElseThrow();var token=SpiritInteraction.token(p);
        h.assertTrue(SpiritInteraction.choose(p,token,2) && SpiritQuests.state(p).active().get().equals(quest),"Accept button accepts only the displayed offer");
        h.assertFalse(SpiritInteraction.choose(p,token,2),"Double-click token cannot accept again");
        h.assertTrue(SpiritInteraction.choose(p,SpiritInteraction.token(p),1) && SpiritQuests.state(p).active().get().equals(quest),"Business button shows active progress without replacing it");
        SpiritInteraction.choose(p,SpiritInteraction.token(p),5);p.discard();spirit.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void interaction_tokens_reject_forgery_distance_dead_givers_and_wrong_actions(GameTestHelper h){
        var p=player(h);var other=player(h);var spirit=spirit(h,p);var token=SpiritInteraction.open(p,spirit);
        h.assertFalse(SpiritInteraction.choose(other,token,1),"Another player cannot use an interaction token");
        h.assertFalse(SpiritInteraction.choose(p,"forged",1),"Unopened token cannot create quests");
        p.setPos(p.position().add(20,0,0));h.assertFalse(SpiritInteraction.choose(p,token,1),"Walking away invalidates choices");
        p.setPos(spirit.position());token=SpiritInteraction.open(p,spirit);h.assertFalse(SpiritInteraction.choose(p,token,2),"Main menu cannot accept a hidden offer");
        token=SpiritInteraction.open(p,spirit);spirit.discard();h.assertFalse(SpiritInteraction.choose(p,token,0),"Removed Spirit cannot open trades");p.discard();other.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spirit_menu_payloads_round_trip_and_decline_keeps_no_active_quest(GameTestHelper h){
        var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
        try{var original=new SpiritInteractionPayloads.Open("token",net.minecraft.network.chat.Component.literal("Spirit"),net.minecraft.network.chat.Component.literal("Offer"),1);
            SpiritInteractionPayloads.Open.CODEC.encode(buffer,original);h.assertTrue(SpiritInteractionPayloads.Open.CODEC.decode(buffer).equals(original),"Client receives title, offer and mode");
            var choice=new SpiritInteractionPayloads.Choice("token",2);SpiritInteractionPayloads.Choice.CODEC.encode(buffer,choice);h.assertTrue(SpiritInteractionPayloads.Choice.CODEC.decode(buffer).equals(choice),"Button choice preserves authorization token");
        }finally{buffer.release();}
        var p=player(h);var spirit=spirit(h,p);SpiritInteraction.open(p,spirit);SpiritInteraction.choose(p,SpiritInteraction.token(p),1);
        h.assertTrue(SpiritInteraction.choose(p,SpiritInteraction.token(p),3) && SpiritQuests.state(p).offer().isEmpty() && SpiritQuests.state(p).active().isEmpty(),"Decline button leaves no quest");
        SpiritInteraction.choose(p,SpiritInteraction.token(p),5);p.discard();spirit.discard();h.succeed();
    }
}
