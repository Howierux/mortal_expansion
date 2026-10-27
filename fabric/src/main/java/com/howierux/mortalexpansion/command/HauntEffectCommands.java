package com.howierux.mortalexpansion.command;

import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;

/** Operator testing: explicit events bypass tier/cooldown, never underlying safety gates. */
public final class HauntEffectCommands {
    private HauntEffectCommands() {}
    public static LiteralArgumentBuilder<CommandSourceStack> branch() {
        return Commands.literal("effect").then(Commands.argument("target",EntityArgument.player())
            .then(Commands.argument("effect",StringArgumentType.word())
                .suggests((context,builder)-> {
                    SharedSuggestionProvider.suggest(java.util.stream.Stream.concat(
                        java.util.stream.Stream.of("random"),Arrays.stream(HauntEffectKind.values())
                        .map(kind->kind.name().toLowerCase(Locale.ROOT))),builder);
                    return builder.buildFuture();
                })
                .executes(context->trigger(context.getSource(),EntityArgument.getPlayer(context,"target"),
                    StringArgumentType.getString(context,"effect")))));
    }
    public static int trigger(CommandSourceStack source,ServerPlayer target,String name) {
        if (!target.isAlive() || target.gameMode.getGameModeForPlayer()==net.minecraft.world.level.GameType.SPECTATOR) {
            source.sendFailure(Component.literal("Target must be alive and outside spectator mode."));return 0;
        }
        List<HauntEffectKind> candidates;
        if (name.equalsIgnoreCase("random")) {
            int tier=PlayerHauntManager.getTier(target).id();
            candidates=new ArrayList<>(Arrays.stream(HauntEffectKind.values())
                .filter(kind->kind.eligible(tier,HauntProtection.protectedPlayer(target))).toList());
        } else {
            HauntEffectKind kind;
            try { kind=HauntEffectKind.valueOf(name.toUpperCase(Locale.ROOT)); }
            catch(IllegalArgumentException exception) {
                source.sendFailure(Component.literal("Unknown Haunt effect: "+name));return 0;
            }
            if (!kind.implemented()) {
                source.sendFailure(Component.literal("Missing function: "+name+" is not implemented yet."));return 0;
            }
            candidates=new ArrayList<>(List.of(kind));
        }
        boolean researchApplicable=name.equalsIgnoreCase("random") && com.howierux.mortalexpansion.guide.MortalityResearch.bonus(target)>0
            && com.howierux.mortalexpansion.guide.MortalityResearch.ready(target);
        boolean firstSelection=true;
        while(!candidates.isEmpty()) {
            int roll=target.getRandom().nextInt(candidates.stream().mapToInt(kind->kind.weight).sum());
            HauntEffectKind chosen=candidates.getFirst();
            for(var kind:candidates) { roll-=kind.weight;if(roll<0){chosen=kind;break;} }
            if(firstSelection && researchApplicable)chosen=com.howierux.mortalexpansion.guide.MortalityResearch.biasedChoice(target,candidates);
            firstSelection=false;
            candidates.remove(chosen);
            // Targeted commands must not defer a ghost to an unrelated player later.
            boolean success=chosen==HauntEffectKind.GHOST
                ? !HauntProtection.protectedPlayer(target) && (com.howierux.mortalexpansion.haunt.Dreamcatchers.prevent(target) || HauntEffects.spawnGhostNear(target))
                : HauntEffects.perform(target,chosen);
            if(!success)continue;
            if(researchApplicable)com.howierux.mortalexpansion.guide.MortalityResearch.spent(target);
            HauntEventController.hint(target,chosen);
            String id=chosen.name().toLowerCase(Locale.ROOT);
            source.sendSuccess(()->Component.literal("Triggered "+id+" for "+target.getGameProfile().getName()+"."),true);
            return 1;
        }
        source.sendFailure(Component.literal("No effect could run: check personal Haunt, protections, book knowledge and nearby targets."));
        return 0;
    }
}
