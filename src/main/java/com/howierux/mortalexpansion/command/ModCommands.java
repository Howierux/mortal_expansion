package com.howierux.mortalexpansion.command;

import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.howierux.mortalexpansion.haunt.NaturalHauntGenerator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Development/debug commands for validating the haunt foundation in-game. */
public final class ModCommands {
    private ModCommands() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mortalexpansion")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("haunt")
                                .then(Commands.literal("get")
                                        .executes(ModCommands::getHaunt))
                                .then(Commands.literal("natural")
                                        .executes(ModCommands::getNaturalHaunt))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("tier", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    for (HauntTier tier : HauntTier.values()) {
                                                        builder.suggest(tier.name().toLowerCase());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ModCommands::setHaunt)))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(-6, 6))
                                                .executes(ModCommands::addHaunt)))
                                .then(Commands.literal("tiers")
                                        .executes(ModCommands::listTiers)))
        );
    }

    private static int getHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        HauntTier tier = HauntManager.getTier(level, pos);

        source.sendSuccess(() -> Component.literal(
                "Chunk haunt: " + tier.displayName() + " (" + tier.id() + ")"
        ), false);
        return tier.id();
    }

    private static int getNaturalHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(pos);

        HauntTier tier = NaturalHauntGenerator.determineInitialTier(level, chunkPos);

        source.sendSuccess(() -> Component.literal(
                "Biome-derived natural haunt: " + tier.displayName() + " (" + tier.id() + ")"
        ), false);
        return tier.id();
    }

    private static int setHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String rawTier = StringArgumentType.getString(context, "tier");
        HauntTier tier = HauntTier.parse(rawTier).orElse(null);

        if (tier == null) {
            source.sendFailure(Component.literal("Unknown haunt tier: " + rawTier));
            return 0;
        }

        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        HauntManager.setTier(level, pos, tier);
        source.sendSuccess(() -> Component.literal("Set chunk haunt to " + tier.displayName()), true);
        return tier.id();
    }

    private static int addHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        HauntTier result = HauntManager.addLevels(level, pos, amount);

        source.sendSuccess(() -> Component.literal(
                "Chunk haunt is now " + result.displayName() + " (" + result.id() + ")"
        ), true);
        return result.id();
    }

    private static int listTiers(CommandContext<CommandSourceStack> context) {
        String tiers = Arrays.stream(HauntTier.values())
                .map(t -> t.id() + "=" + t.displayName())
                .collect(Collectors.joining(", "));

        context.getSource().sendSuccess(() -> Component.literal(tiers), false);
        return HauntTier.values().length;
    }
}
