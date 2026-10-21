package com.howierux.mortalexpansion.respawn;

import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;

import javax.annotation.Nullable;

public final class GraveRespawnHandler {
    private GraveRespawnHandler() {}

    @SubscribeEvent
    public static void onRespawnPosition(PlayerRespawnPositionEvent event) {
        if (event.isFromEndFight()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        // Leave the native bed/anchor/world-spawn transition intact after journey death.
        if (SpiritJourneyHandler.isJourneyDeathRespawn(player)) return;
        String encoded = player.getData(ModAttachments.GRAVE_RESPAWN_POINT);
        GraveTarget target = GraveTarget.parse(encoded);
        if (target == null) {
            return;
        }

        ServerLevel targetLevel = player.getServer().getLevel(target.dimension());
        if (targetLevel == null) {
            player.setData(ModAttachments.GRAVE_RESPAWN_POINT, "");
            return;
        }

        if (!(targetLevel.getBlockEntity(target.pos()) instanceof GraveBlockEntity grave)
                || !grave.isRespawnGraveFor(player.getUUID())) {
            player.setData(ModAttachments.GRAVE_RESPAWN_POINT, "");
            return;
        }

        BlockPos spawnPos = target.pos().above();

        event.setDimensionTransition(
                new DimensionTransition(
                        targetLevel,
                        Vec3.atBottomCenterOf(spawnPos),
                        Vec3.ZERO,
                        player.getYRot(),
                        player.getXRot(),
                        false,
                        DimensionTransition.DO_NOTHING
                )
        );

        // Preserve the player's ordinary bed/world spawn for future fallback.
        event.setCopyOriginalSpawnPosition(true);

        if (!grave.isNaturallyGenerated()) {
            PlayerHauntManager.addPoints(player, 1);
            targetLevel.destroyBlock(target.pos(), false);
            player.setData(ModAttachments.GRAVE_RESPAWN_POINT, "");
        }
    }

    public record GraveTarget(ResourceKey<Level> dimension, BlockPos pos) {
        @Nullable
        public static GraveTarget parse(String encoded) {
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
