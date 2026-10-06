package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.command.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HauntCommandGameTests {
    @GameTest(template="deferred_items_test")
    public static void specific_command_bypasses_tier_and_cooldown_without_changing_haunt(GameTestHelper h) throws Exception {
        var p=h.makeMockServerPlayerInLevel();PlayerHauntManager.setTier(p,HauntTier.VOID);
        var state=HauntProtection.state(p);state.putLong("BenefitCooldownUntil",HauntProtection.now(p)+1200);HauntProtection.state(p,state);
        var dispatcher=new com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>();ModCommands.register(dispatcher);
        h.assertTrue(dispatcher.execute("mortalexpansion haunt effect @s good_potion",p.createCommandSourceStack().withPermission(2))==1,"Real command resolves its single target");
        h.assertTrue(p.hasEffect(MobEffects.MOVEMENT_SPEED)||p.hasEffect(MobEffects.DIG_SPEED),"Requested effect applied");
        h.assertTrue(PlayerHauntManager.getTier(p)==HauntTier.VOID && HauntProtection.state(p).equals(state),"Debug invocation keeps Haunt and automatic cooldown unchanged");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void random_command_and_missing_functions_report_actual_success(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var source=p.createCommandSourceStack().withPermission(2);
        PlayerHauntManager.setTier(p,HauntTier.VOID);
        h.assertTrue(HauntEffectCommands.trigger(source,p,"random")==0,"Void has no eligible random effects");
        h.assertTrue(HauntEffectCommands.trigger(source,p,"helpful_visitation")==0,"Unimplemented effects cannot pretend to run");
        h.assertTrue(HauntEffectCommands.trigger(source,p,"unknown")==0,"Unknown names fail clearly");
        PlayerHauntManager.setTier(p,HauntTier.NATURAL);
        h.assertTrue(HauntEffectCommands.trigger(source,p,"random")==1,"Random eligible pool tries alternatives until a real event succeeds");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void command_permission_and_spectator_safety_are_enforced(GameTestHelper h) throws Exception {
        var p=h.makeMockServerPlayerInLevel();
        var dispatcher=new com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>();ModCommands.register(dispatcher);
        boolean denied=false;try {dispatcher.execute("mortalexpansion haunt effect @s hit",p.createCommandSourceStack().withPermission(0));}
        catch(com.mojang.brigadier.exceptions.CommandSyntaxException expected){denied=true;}
        h.assertTrue(denied,"Non-operators cannot trigger effects");
        p.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
        h.assertTrue(HauntEffectCommands.trigger(p.createCommandSourceStack().withPermission(2),p,"hit")==0,"Spectator target is rejected");
        p.discard();h.succeed();
    }
}
