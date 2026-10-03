package com.howierux.mortalexpansion.respawn;

import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.network.SpiritJourneyPayloads;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Stage one: an optional self-Grave respawn into vanilla spectator flight. */
public final class SpiritJourneyHandler {
    private SpiritJourneyHandler() {}

    public static boolean hasSelfGrave(ServerPlayer player) {
        var target = GraveRespawnHandler.GraveTarget.parse(player.getData(ModAttachments.GRAVE_RESPAWN_POINT));
        if (target == null) return false;
        ServerLevel level = player.server.getLevel(target.dimension());
        return level != null && level.getBlockEntity(target.pos()) instanceof GraveBlockEntity grave
                && grave.isRespawnGraveFor(player.getUUID())
                && grave.getInscription().equalsIgnoreCase(player.getGameProfile().getName());
    }

    public static boolean canChoose(ServerPlayer player) {
        return player.isDeadOrDying() && hasSelfGrave(player);
    }

    public static void sendEligibility(ServerPlayer player) {
        if (player.connection.hasChannel(SpiritJourneyPayloads.Eligibility.TYPE)) {
            PacketDistributor.sendToPlayer(player, new SpiritJourneyPayloads.Eligibility(canChoose(player), false));
        }
    }

    public static void choose(ServerPlayer player) {
        // The client cannot opt in while alive or with a removed/other-name Grave.
        if (!canChoose(player)) {
            sendEligibility(player);
            return;
        }
        player.setData(ModAttachments.SPIRIT_JOURNEY_RESPAWN, true);
        player.connection.player = player.server.getPlayerList().respawn(player, false, Entity.RemovalReason.KILLED);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isEndConquered()
                || !player.getData(ModAttachments.SPIRIT_JOURNEY_RESPAWN)) return;
        player.setData(ModAttachments.SPIRIT_JOURNEY_RESPAWN, false);
        player.setGameMode(GameType.SPECTATOR);
        if (player.connection.hasChannel(SpiritJourneyPayloads.Eligibility.TYPE)) {
            PacketDistributor.sendToPlayer(player, new SpiritJourneyPayloads.Eligibility(false, true));
        }
    }
}
