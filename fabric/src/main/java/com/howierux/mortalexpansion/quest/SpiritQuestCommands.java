package com.howierux.mortalexpansion.quest;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
public final class SpiritQuestCommands {
    private SpiritQuestCommands(){}
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("spiritquest").executes(c->{SpiritQuests.status(c.getSource().getPlayerOrException());return 1;})
            .then(Commands.literal("status").executes(c->{SpiritQuests.status(c.getSource().getPlayerOrException());return 1;}))
            .then(Commands.literal("accept").then(Commands.argument("offer",StringArgumentType.word()).executes(c->respond(c,SpiritQuests.accept(c.getSource().getPlayerOrException(),StringArgumentType.getString(c,"offer"))))))
            .then(Commands.literal("decline").then(Commands.argument("offer",StringArgumentType.word()).executes(c->respond(c,SpiritQuests.decline(c.getSource().getPlayerOrException(),StringArgumentType.getString(c,"offer"))))))
            .then(Commands.literal("abandon").executes(c->respond(c,SpiritQuests.abandon(c.getSource().getPlayerOrException())))));
    }
    private static int respond(CommandContext<CommandSourceStack> context,boolean success){
        if(success)context.getSource().sendSuccess(()->Component.translatable("message.mortal_expansion.quest.updated"),false);
        else context.getSource().sendFailure(Component.translatable("message.mortal_expansion.quest.unavailable"));return success?1:0;
    }
}
