package com.howierux.mortalexpansion.command;

import com.howierux.mortalexpansion.haunt.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import java.util.Locale;

public final class HauntCommands {
    private HauntCommands(){}
    public static void register(){CommandRegistrationCallback.EVENT.register((dispatcher,registries,environment)->{
        var haunt=Commands.literal("haunt").then(HauntEffectCommands.branch()).then(Commands.literal("get").executes(c->get(c,false)))
            .then(Commands.literal("natural").executes(HauntCommands::natural))
            .then(Commands.literal("tiers").executes(c->{for(var tier:HauntTier.values())c.getSource().sendSuccess(()->Component.literal(tier.displayName()+": "+tier.minimumValue()+"-"+tier.maximumValue()),false);return 1;}));
        for(boolean player:new boolean[]{false,true}){
            var branch=player?Commands.literal("player").then(Commands.literal("get").executes(c->get(c,true))):haunt;
            branch.then(Commands.literal("set").then(Commands.argument("tier",StringArgumentType.word()).suggests((c,b)->{
                for(var tier:HauntTier.values())b.suggest(tier.name().toLowerCase(Locale.ROOT));return b.buildFuture();
            }).executes(c->set(c,player))));
            branch.then(Commands.literal("add").then(Commands.argument("amount",IntegerArgumentType.integer(-69,69)).executes(c->add(c,player))));
            if(player)haunt.then(branch);
        }
        dispatcher.register(Commands.literal("mortalexpansion").requires(s->s.hasPermission(2)).then(haunt));
    });}
    private static boolean loaded(CommandSourceStack s){var at=new ChunkPos(net.minecraft.core.BlockPos.containing(s.getPosition()));if(s.getLevel().getChunkSource().getChunkNow(at.x,at.z)!=null)return true;s.sendFailure(Component.literal("Load the target chunk before querying or changing Haunt."));return false;}
    private static int get(CommandContext<CommandSourceStack> c,boolean player) throws com.mojang.brigadier.exceptions.CommandSyntaxException{
        var s=c.getSource();if(!player && !loaded(s))return 0;
        int units=player?PlayerHauntManager.getHundredths(s.getPlayerOrException()):HauntManager.getHundredths(s.getLevel(),net.minecraft.core.BlockPos.containing(s.getPosition()));
        s.sendSuccess(()->Component.literal((player?"Personal":"Chunk")+" Haunt: "+HauntTier.fromValue(units/100).displayName()+" | "+String.format(Locale.ROOT,"%.2f",units/100.0)),false);return units/100;
    }
    private static int set(CommandContext<CommandSourceStack> c,boolean player) throws com.mojang.brigadier.exceptions.CommandSyntaxException{
        var tier=HauntTier.parse(StringArgumentType.getString(c,"tier"));if(tier.isEmpty()){c.getSource().sendFailure(Component.literal("Unknown Haunt tier."));return 0;}
        if(player)PlayerHauntManager.setTier(c.getSource().getPlayerOrException(),tier.get());else{if(!loaded(c.getSource()))return 0;HauntManager.setTier(c.getSource().getLevel(),net.minecraft.core.BlockPos.containing(c.getSource().getPosition()),tier.get());}
        return get(c,player);
    }
    private static int add(CommandContext<CommandSourceStack> c,boolean player) throws com.mojang.brigadier.exceptions.CommandSyntaxException{
        int amount=IntegerArgumentType.getInteger(c,"amount");
        if(player)PlayerHauntManager.addPoints(c.getSource().getPlayerOrException(),amount);else{if(!loaded(c.getSource()))return 0;HauntManager.addPoints(c.getSource().getLevel(),net.minecraft.core.BlockPos.containing(c.getSource().getPosition()),amount);}
        return get(c,player);
    }
    private static int natural(CommandContext<CommandSourceStack> c){var s=c.getSource();if(!loaded(s))return 0;var pos=new ChunkPos(net.minecraft.core.BlockPos.containing(s.getPosition()));var tier=NaturalHauntGenerator.determineInitialTier(s.getLevel(),s.getLevel().getChunkSource().getChunkNow(pos.x,pos.z));s.sendSuccess(()->Component.literal("Natural Haunt baseline: "+tier.displayName()+" | "+tier.midpointValue()),false);return tier.midpointValue();}
}
