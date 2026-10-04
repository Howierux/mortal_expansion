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

/** Optional self-Grave spectator respawn with deferred death-item selection. */
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
        var corpse = com.howierux.mortalexpansion.registry.ModEntities.PLAYER_CORPSE.get().create(player.serverLevel());
        if (corpse == null) return;
        corpse.initialize(player);
        player.setData(ModAttachments.SPIRIT_JOURNEY_RETURN_MODE, player.gameMode.getGameModeForPlayer().getId());
        if (!player.serverLevel().addFreshEntity(corpse)) return;
        player.setData(ModAttachments.SPIRIT_JOURNEY_CORPSE, corpse.getUUID().toString());
        player.setData(ModAttachments.SPIRIT_JOURNEY_RESPAWN, true);
        player.connection.player = player.server.getPlayerList().respawn(player, false, Entity.RemovalReason.KILLED);
    }

    public static boolean isActive(ServerPlayer player) {
        return player.isSpectator() && !player.getData(ModAttachments.SPIRIT_JOURNEY_CORPSE).isEmpty();
    }

    public static final int WELL_HAUNT_COST = 10;

    public static boolean recoverAtWell(ServerPlayer player) {
        if (!isActive(player)) return false;
        var well = com.howierux.mortalexpansion.spiritwell.SpiritwellIndex.get(player.serverLevel()).nearest(player.serverLevel(), player.position());
        if (well == null) return false;
        var level = player.serverLevel();
        net.minecraft.world.phys.Vec3 destination = null;
        for (int dy = 0; dy <= 3; dy++) {
            var at = well.getCenter().add(0, dy, 0);
            if (level.noCollision(player, player.getBoundingBox().move(at.subtract(player.position())))) {
                destination = at;
                break;
            }
        }
        if (destination == null) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.spiritwell_blocked"), false);
            return false;
        }
        String corpseId = player.getData(ModAttachments.SPIRIT_JOURNEY_CORPSE);
        var mode = GameType.byId(player.getData(ModAttachments.SPIRIT_JOURNEY_RETURN_MODE));
        if (mode == GameType.SPECTATOR) mode = GameType.SURVIVAL;
        player.teleportTo(level, destination.x, destination.y, destination.z, player.getYRot(), 0);
        player.setData(ModAttachments.SPIRIT_JOURNEY_CORPSE, "");
        player.setGameMode(mode);
        player.setHealth(player.getMaxHealth());
        player.clearFire();
        player.fallDistance = 0;
        player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        com.howierux.mortalexpansion.haunt.PlayerHauntManager.addPoints(player, WELL_HAUNT_COST);
        com.howierux.mortalexpansion.haunt.HauntManager.addPoints(level, well, WELL_HAUNT_COST);
        DeferredDeathItems.recoverHeld(player);
        syncState(player);
        // Unloaded corpses remove themselves when next ticked, once the owner is available.
        try {
            var id = java.util.UUID.fromString(corpseId);
            for (var world : player.server.getAllLevels()) {
                var corpse = world.getEntity(id);
                if (corpse instanceof com.howierux.mortalexpansion.entity.PlayerCorpseEntity body
                        && body.owner().filter(player.getUUID()::equals).isPresent()) body.discard();
            }
        } catch (IllegalArgumentException ignored) { }
        return true;
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) syncState(player);
    }

    public static void syncState(ServerPlayer player) {
        if (player.connection.hasChannel(SpiritJourneyPayloads.State.TYPE)) {
            PacketDistributor.sendToPlayer(player, new SpiritJourneyPayloads.State(
                    player.isSpectator() && !player.getData(ModAttachments.SPIRIT_JOURNEY_CORPSE).isEmpty()));
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isEndConquered()) return;
        boolean journey = player.getData(ModAttachments.SPIRIT_JOURNEY_RESPAWN);
        DeferredDeathItems.resolveChoice(player, journey);
        if (!journey) { syncState(player); return; }
        player.setData(ModAttachments.SPIRIT_JOURNEY_RESPAWN, false);
        player.setGameMode(GameType.SPECTATOR);
        syncState(player);
        if (player.connection.hasChannel(SpiritJourneyPayloads.Eligibility.TYPE)) {
            PacketDistributor.sendToPlayer(player, new SpiritJourneyPayloads.Eligibility(false, true));
        }
    }
}
