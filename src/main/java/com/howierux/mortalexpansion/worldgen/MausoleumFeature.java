package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Generates a compact 5x5 Gravestone mausoleum with a two-block doorway,
 * stone-fence entrance posts, and a Grave marker inside.
 */
public final class MausoleumFeature extends Feature<NoneFeatureConfiguration> {
    private static final int RADIUS = 2;

    public MausoleumFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos centerXZ = context.origin();

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                BlockPos surface = surfaceAir(level, centerXZ.getX() + dx, centerXZ.getZ() + dz);

                if (!level.getBiome(surface).is(ModBiomes.GRAVEYARD)) {
                    return false;
                }

                minY = Math.min(minY, surface.getY());
                maxY = Math.max(maxY, surface.getY());
            }
        }

        if (maxY - minY > 1) {
            return false;
        }

        int floorY = minY - 1;
        int centerX = centerXZ.getX();
        int centerZ = centerXZ.getZ();

        // Clear the building volume and lay a full Gravestone floor.
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                BlockPos floor = new BlockPos(centerX + dx, floorY, centerZ + dz);
                level.setBlock(floor, ModBlocks.GRAVESTONE.get().defaultBlockState(), 3);

                for (int dy = 1; dy <= 3; dy++) {
                    level.setBlock(floor.above(dy), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        // Perimeter walls, three blocks tall. The south-center two blocks are
        // left open as the entrance.
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    boolean edge = Math.abs(dx) == RADIUS || Math.abs(dz) == RADIUS;
                    if (!edge) {
                        continue;
                    }

                    boolean doorway = dz == RADIUS && dx == 0 && dy <= 2;
                    if (!doorway) {
                        level.setBlock(
                                new BlockPos(centerX + dx, floorY + dy, centerZ + dz),
                                ModBlocks.GRAVESTONE.get().defaultBlockState(),
                                3
                        );
                    }
                }
            }
        }

        // Flat Gravestone roof.
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                level.setBlock(
                        new BlockPos(centerX + dx, floorY + 4, centerZ + dz),
                        ModBlocks.GRAVESTONE.get().defaultBlockState(),
                        3
                );
            }
        }

        // Fence posts flank the entrance.
        level.setBlock(
                new BlockPos(centerX - 1, floorY + 1, centerZ + RADIUS + 1),
                ModBlocks.STONE_FENCE.get().defaultBlockState(),
                3
        );
        level.setBlock(
                new BlockPos(centerX + 1, floorY + 1, centerZ + RADIUS + 1),
                ModBlocks.STONE_FENCE.get().defaultBlockState(),
                3
        );

        // One Grave marker against the rear interior wall.
        Direction facing = random.nextBoolean() ? Direction.SOUTH : Direction.NORTH;
        level.setBlock(
                new BlockPos(centerX, floorY + 1, centerZ - 1),
                ModBlocks.GRAVE.get().defaultBlockState().setValue(GraveBlock.FACING, facing),
                3
        );

        return true;
    }

    private static BlockPos surfaceAir(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }
}
