package com.howierux.mortalexpansion.entity;

import com.howierux.mortalexpansion.entity.mob.PharaohlEntity;
import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Spawns one Pharaohl per player per vanilla Desert Pyramid.
 */
public final class PharaohlPyramidHandler {
    private static final int CHECK_INTERVAL = 20;

    private PharaohlPyramidHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        Structure desertPyramid = level.registryAccess()
                .lookupOrThrow(Registries.STRUCTURE)
                .getOrThrow(BuiltinStructures.DESERT_PYRAMID)
                .value();

        StructureStart start = level.structureManager()
                .getStructureWithPieceAt(player.blockPosition(), desertPyramid);

        if (!start.isValid()) {
            return;
        }

        BoundingBox box = start.getBoundingBox();
        String pyramidKey = createPyramidKey(level, box);

        Set<String> triggered = player.getData(ModAttachments.PHARAOHL_PYRAMIDS);
        if (triggered.contains(pyramidKey)) {
            return;
        }

        BlockPos spawnPos = findCentralTerracotta(level, box);
        if (spawnPos == null) {
            return;
        }

        PharaohlEntity pharaohl = ModEntities.PHARAOHL.get().create(level);
        if (pharaohl == null) {
            return;
        }

        pharaohl.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY() + 1.0D,
                spawnPos.getZ() + 0.5D,
                player.getYRot(),
                0.0F
        );

        if (!level.addFreshEntity(pharaohl)) {
            return;
        }

        Set<String> updated = new HashSet<>(triggered);
        updated.add(pyramidKey);
        player.setData(ModAttachments.PHARAOHL_PYRAMIDS, updated);
    }

    private static String createPyramidKey(ServerLevel level, BoundingBox box) {
        return level.dimension().location()
                + ":"
                + box.minX()
                + ","
                + box.minY()
                + ","
                + box.minZ();
    }

    /**
     * Vanilla Desert Pyramids have blue terracotta at the center of the floor.
     * Search the center column first, then a tiny center-adjacent area as a
     * defensive fallback in case terrain adaptation shifts the structure.
     */
    private static BlockPos findCentralTerracotta(ServerLevel level, BoundingBox box) {
        int centerX = (box.minX() + box.maxX()) / 2;
        int centerZ = (box.minZ() + box.maxZ()) / 2;

        for (int y = box.maxY(); y >= box.minY(); y--) {
            BlockPos pos = new BlockPos(centerX, y, centerZ);
            if (level.getBlockState(pos).is(Blocks.BLUE_TERRACOTTA)) {
                return pos;
            }
        }

        for (int radius = 1; radius <= 2; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    for (int y = box.maxY(); y >= box.minY(); y--) {
                        BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                        if (level.getBlockState(pos).is(Blocks.BLUE_TERRACOTTA)) {
                            return pos;
                        }
                    }
                }
            }
        }

        return null;
    }
}
