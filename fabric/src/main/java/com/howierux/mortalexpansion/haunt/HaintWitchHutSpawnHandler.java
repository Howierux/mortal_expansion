package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.entity.mob.HaintEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;

/**
 * Rare special Haint spawning around vanilla witch huts.
 *
 * This is deliberately separate from ordinary mob spawning: Haints do not
 * enter the normal biome spawn pool. At Abominable+ Haunt, a loaded player
 * near a witch hut can very rarely cause one Haint to appear around the hut.
 */
public final class HaintWitchHutSpawnHandler {
    private static final int CHECK_INTERVAL = 200; // 10 seconds
    private static final int HUT_DETECTION_RADIUS = 48;
    private static final int SPAWN_MIN_RADIUS = 10;
    private static final int SPAWN_MAX_RADIUS = 24;
    private static final int ACTIVE_HAINT_RADIUS = 64;
    private static final float SPAWN_CHANCE_PER_CHECK = 0.01F; // 1%

    private HaintWitchHutSpawnHandler() {}

    public static void tick(ServerPlayer player){if(player.tickCount%CHECK_INTERVAL!=0)return;
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(ServerLevel.OVERWORLD)
                || !HauntManager.isAtLeast(level, player.blockPosition(), HauntTier.ABOMINABLE)) {
            return;
        }

        Structure swampHut = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.SWAMP_HUT)
                .value();

        StructureStart hut = findNearbyHut(level, player.blockPosition(), swampHut);
        if (hut == null) {
            return;
        }

        BoundingBox hutBox = hut.getBoundingBox();
        BlockPos hutCenter = new BlockPos(
                (hutBox.minX() + hutBox.maxX()) / 2,
                (hutBox.minY() + hutBox.maxY()) / 2,
                (hutBox.minZ() + hutBox.maxZ()) / 2
        );

        if (hasNearbyHaint(level, hutCenter)) {
            return;
        }

        if (level.getRandom().nextFloat() >= SPAWN_CHANCE_PER_CHECK) {
            return;
        }

        BlockPos spawnPos = chooseSpawnPosition(level, hutCenter);
        if (spawnPos == null) {
            return;
        }

        HaintEntity haint = com.howierux.mortalexpansion.entity.CombatEntities.HAINT.create(level);
        if (haint == null) {
            return;
        }

        haint.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F,
                0.0F
        );

        level.addFreshEntity(haint);
    }

    private static StructureStart findNearbyHut(ServerLevel level,BlockPos pos,Structure ignored){return com.howierux.mortalexpansion.content.LoadedEncounters.find(level,pos,BuiltinStructures.SWAMP_HUT,HUT_DETECTION_RADIUS,false);}
    private static boolean hasNearbyHaint(ServerLevel level, BlockPos hutCenter) {
        double radiusSq = ACTIVE_HAINT_RADIUS * ACTIVE_HAINT_RADIUS;

        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof HaintEntity
                    && entity.isAlive()
                    && entity.distanceToSqr(
                            hutCenter.getX() + 0.5D,
                            hutCenter.getY() + 0.5D,
                            hutCenter.getZ() + 0.5D
                    ) <= radiusSq) {
                return true;
            }
        }

        return false;
    }

    private static BlockPos chooseSpawnPosition(ServerLevel level, BlockPos hutCenter) {
        for (int attempt = 0; attempt < 8; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0D;
            int radius = Mth.nextInt(
                    level.getRandom(),
                    SPAWN_MIN_RADIUS,
                    SPAWN_MAX_RADIUS
            );

            int x = hutCenter.getX() + Mth.floor(Math.cos(angle) * radius);
            int z = hutCenter.getZ() + Mth.floor(Math.sin(angle) * radius);
            if(!level.hasChunk(x>>4,z>>4))continue;
            int y = level.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    x,
                    z
            );

            BlockPos feet = new BlockPos(x, y, z);
            if (level.getBlockState(feet).canBeReplaced()
                    && level.getBlockState(feet.above()).canBeReplaced()
                    && !level.getFluidState(feet).isSource()) {
                return feet;
            }
        }

        return null;
    }
}
