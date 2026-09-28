package com.howierux.mortalexpansion.command;

import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.howierux.mortalexpansion.haunt.NaturalHauntGenerator;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
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
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(-69, 69))
                                                .executes(ModCommands::addHaunt)))
                                .then(Commands.literal("player")
                                        .then(Commands.literal("get")
                                                .executes(ModCommands::getPlayerHaunt))
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("tier", StringArgumentType.word())
                                                        .suggests((context, builder) -> {
                                                            for (HauntTier tier : HauntTier.values()) {
                                                                builder.suggest(tier.name().toLowerCase());
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ModCommands::setPlayerHaunt)))
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(-69, 69))
                                                        .executes(ModCommands::addPlayerHaunt))))
                                .then(Commands.literal("tiers")
                                        .executes(ModCommands::listTiers)))
        );
    }

    private static int getHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        int value = HauntManager.getValue(level, pos);
        HauntTier tier = HauntTier.fromValue(value);

        source.sendSuccess(() -> Component.literal(
                "Chunk haunt: " + tier.displayName()
                        + " | value " + value
                        + " | range " + tier.minimumValue() + "-" + tier.maximumValue()
        ), false);
        return value;
    }

    private static int getNaturalHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        LevelChunk chunk = level.getChunkAt(pos);

        HauntTier tier = NaturalHauntGenerator.determineInitialTier(chunk);

        int value = tier.midpointValue();
        source.sendSuccess(() -> Component.literal(
                "Biome-derived natural haunt: " + tier.displayName()
                        + " | baseline value " + value
        ), false);
        return value;
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
        int value = HauntManager.addPoints(level, pos, amount);
        HauntTier result = HauntTier.fromValue(value);

        source.sendSuccess(() -> Component.literal(
                "Chunk haunt is now " + result.displayName() + " | value " + value
        ), true);
        return value;
    }

    private static int getPlayerHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            ServerPlayer player = source.getPlayerOrException();
            int value = PlayerHauntManager.getValue(player);
            HauntTier tier = HauntTier.fromValue(value);

            source.sendSuccess(() -> Component.literal(
                    "Personal haunt: " + tier.displayName()
                            + " | value " + value
                            + " | range " + tier.minimumValue() + "-" + tier.maximumValue()
            ), false);
            return value;
        } catch (Exception exception) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0;
        }
    }

    private static int setPlayerHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String rawTier = StringArgumentType.getString(context, "tier");
        HauntTier tier = HauntTier.parse(rawTier).orElse(null);

        if (tier == null) {
            source.sendFailure(Component.literal("Unknown haunt tier: " + rawTier));
            return 0;
        }

        try {
            ServerPlayer player = source.getPlayerOrException();
            PlayerHauntManager.setTier(player, tier);
            source.sendSuccess(() -> Component.literal(
                    "Set personal haunt to " + tier.displayName() + " (" + tier.id() + ")"
            ), true);
            return tier.id();
        } catch (Exception exception) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0;
        }
    }

    private static int addPlayerHaunt(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        try {
            ServerPlayer player = source.getPlayerOrException();
            int value = PlayerHauntManager.addPoints(player, amount);
            HauntTier tier = HauntTier.fromValue(value);
            source.sendSuccess(() -> Component.literal(
                    "Personal haunt is now " + tier.displayName() + " | value " + value
            ), true);
            return value;
        } catch (Exception exception) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0;
        }
    }

    private static int listTiers(CommandContext<CommandSourceStack> context) {
        String tiers = Arrays.stream(HauntTier.values())
                .map(t -> t.displayName() + "=" + t.minimumValue() + "-" + t.maximumValue())
                .collect(Collectors.joining(", "));

        context.getSource().sendSuccess(() -> Component.literal(tiers), false);
        return HauntTier.values().length;
    }
}
