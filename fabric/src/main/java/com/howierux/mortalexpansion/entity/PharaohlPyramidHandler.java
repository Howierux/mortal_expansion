package com.howierux.mortalexpansion.entity;

import com.howierux.mortalexpansion.entity.mob.PharaohlEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashSet;
import java.util.Set;

/**
 * Spawns one Pharaohl per player per vanilla Desert Pyramid.
 */
public final class PharaohlPyramidHandler {
    private static final int CHECK_INTERVAL = 20;

    private PharaohlPyramidHandler() {}

    public static void tick(ServerPlayer player){
        if(player.tickCount%CHECK_INTERVAL!=0)return;ServerLevel level=player.serverLevel();var start=com.howierux.mortalexpansion.content.LoadedEncounters.find(level,player.blockPosition(),BuiltinStructures.DESERT_PYRAMID,48,true);if(start==null)return;
        if (!start.isValid()) {
            return;
        }

        BoundingBox box = start.getBoundingBox();
        String pyramidKey = createPyramidKey(level, box);

        Set<String> triggered = ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)player).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.PYRAMIDS);
        if (triggered.contains(pyramidKey)) {
            return;
        }

        if (hasActivePharaohl(level, pyramidKey)) {
            return;
        }

        BlockPos spawnPos = findCentralTerracotta(level, box);
        if (spawnPos == null) {
            return;
        }

        PharaohlEntity pharaohl = com.howierux.mortalexpansion.entity.CombatEntities.PHARAOHL.create(level);
        if (pharaohl == null) {
            return;
        }

        pharaohl.setPyramidKey(pyramidKey);
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
        ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)player).setAttached(com.howierux.mortalexpansion.content.ContentRuntime.PYRAMIDS,java.util.Set.copyOf(updated));
    }

    private static boolean hasActivePharaohl(ServerLevel level, String pyramidKey) {
        for (net.minecraft.world.entity.Entity entity : level.getAllEntities()) {
            if (entity instanceof PharaohlEntity pharaohl
                    && pharaohl.isAlive()
                    && pyramidKey.equals(pharaohl.getPyramidKey())) {
                return true;
            }
        }

        return false;
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
            if (level.hasChunkAt(pos)&&level.getBlockState(pos).is(Blocks.BLUE_TERRACOTTA)) {
                return pos;
            }
        }

        for (int radius = 1; radius <= 2; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    for (int y = box.maxY(); y >= box.minY(); y--) {
                        BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                        if (level.hasChunkAt(pos)&&level.getBlockState(pos).is(Blocks.BLUE_TERRACOTTA)) {
                            return pos;
                        }
                    }
                }
            }
        }

        return null;
    }
}
