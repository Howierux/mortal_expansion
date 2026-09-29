package com.howierux.mortalexpansion.respawn;

import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GraveRespawnHandler {
    private GraveRespawnHandler() {}

    private static final Map<UUID, RestoreSpawn> RESTORE_SPAWNS = new HashMap<>();

    private record RestoreSpawn(@Nullable ServerPlayer.RespawnConfig config) {}

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        String encoded = player.getData(ModAttachments.GRAVE_RESPAWN_POINT);
        GraveTarget target = GraveTarget.parse(encoded);
        if (target == null) {
            return;
        }

        ServerLevel targetLevel = player.getServer().getLevel(target.dimension());
        if (targetLevel == null) {
            return;
        }

        if (!(targetLevel.getBlockEntity(target.pos()) instanceof GraveBlockEntity grave)
                || !grave.isRespawnGraveFor(player.getUUID())) {
            player.setData(ModAttachments.GRAVE_RESPAWN_POINT, "");
            return;
        }

        ServerPlayer.RespawnConfig previous = player.getRespawnConfig();
        RESTORE_SPAWNS.put(player.getUUID(), new RestoreSpawn(previous));

        BlockPos respawnPos = target.pos().above();
        player.setRespawnPosition(
                new ServerPlayer.RespawnConfig(
                        new LevelData.RespawnData(
                                GlobalPos.of(target.dimension(), respawnPos),
                                player.getYRot(),
                                0
                        ),
                        true
                ),
                false
        );

        if (!grave.isNaturallyGenerated()) {
            PlayerHauntManager.addPoints(player, 1);
            targetLevel.destroyBlock(target.pos(), false);
            player.setData(ModAttachments.GRAVE_RESPAWN_POINT, "");
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        RestoreSpawn restore = RESTORE_SPAWNS.remove(player.getUUID());
        if (restore != null) {
            player.setRespawnPosition(restore.config(), false);
        }
    }

    private record GraveTarget(ResourceKey<Level> dimension, BlockPos pos) {
        @Nullable
        static GraveTarget parse(String encoded) {
            if (encoded == null || encoded.isBlank()) {
                return null;
            }

            String[] parts = encoded.split("\\|");
            if (parts.length != 4) {
                return null;
            }

            try {
                ResourceLocation dimensionId = ResourceLocation.parse(parts[0]);
                ResourceKey<Level> dimension = ResourceKey.create(
                        Registries.DIMENSION,
                        dimensionId
                );

                return new GraveTarget(
                        dimension,
                        new BlockPos(
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3])
                        )
                );
            } catch (RuntimeException ignored) {
                return null;
            }
        }
    }
}
