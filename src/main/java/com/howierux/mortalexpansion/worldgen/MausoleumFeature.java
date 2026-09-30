package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
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

        // Keep the entire mausoleum, entrance posts, and Grave Moss decoration
        // inside the chunk currently being generated.
        ChunkPos sourceChunk = new ChunkPos(centerXZ);
        int localX = centerXZ.getX() - sourceChunk.getMinBlockX();
        int localZ = centerXZ.getZ() - sourceChunk.getMinBlockZ();
        if (localX < 3 || localX > 12 || localZ < 3 || localZ > 12) {
            return false;
        }

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

        decorateWithGraveMoss(level, random, centerX, centerZ, floorY);

        // One Grave marker against the rear interior wall.
        Direction facing = random.nextBoolean() ? Direction.SOUTH : Direction.NORTH;
        BlockPos gravePos = new BlockPos(centerX, floorY + 1, centerZ - 1);
        level.setBlock(
                gravePos,
                ModBlocks.GRAVE.get().defaultBlockState()
                        .setValue(GraveBlock.FACING, facing)
                        .setValue(GraveBlock.DEPTH, 1)
                        .setValue(GraveBlock.NATURAL, true),
                2
        );
        if (level.getBlockEntity(gravePos) instanceof GraveBlockEntity grave) {
            grave.markNaturallyGeneratedFromWorldgen();
        }

        return true;
    }

    private static void decorateWithGraveMoss(
            WorldGenLevel level,
            RandomSource random,
            int centerX,
            int centerZ,
            int floorY
    ) {
        for (int dy = 1; dy <= 3; dy++) {
            for (int offset = -2; offset <= 2; offset++) {
                tryPlaceMoss(level, random,
                        new BlockPos(centerX - 3, floorY + dy, centerZ + offset),
                        Direction.EAST, 0.22F);
                tryPlaceMoss(level, random,
                        new BlockPos(centerX + 3, floorY + dy, centerZ + offset),
                        Direction.WEST, 0.22F);
                tryPlaceMoss(level, random,
                        new BlockPos(centerX + offset, floorY + dy, centerZ - 3),
                        Direction.SOUTH, 0.22F);
                tryPlaceMoss(level, random,
                        new BlockPos(centerX + offset, floorY + dy, centerZ + 3),
                        Direction.NORTH, 0.22F);
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                tryPlaceMoss(level, random,
                        new BlockPos(centerX + dx, floorY + 5, centerZ + dz),
                        Direction.DOWN, 0.28F);
            }
        }
    }

    private static void tryPlaceMoss(
            WorldGenLevel level,
            RandomSource random,
            BlockPos mossPos,
            Direction supportDirection,
            float chance
    ) {
        if (random.nextFloat() >= chance) {
            return;
        }

        BlockPos supportPos = mossPos.relative(supportDirection);
        if (!level.getBlockState(supportPos).is(ModBlocks.GRAVESTONE.get())) {
            return;
        }

        BlockState existing = level.getBlockState(mossPos);
        if (!existing.isAir() && !existing.is(ModBlocks.GRAVE_MOSS.get())) {
            return;
        }

        BlockState mossState = existing.is(ModBlocks.GRAVE_MOSS.get())
                ? existing
                : ModBlocks.GRAVE_MOSS.get().defaultBlockState();

        level.setBlock(
                mossPos,
                mossState.setValue(
                        MultifaceBlock.getFaceProperty(supportDirection),
                        true
                ),
                2
        );
    }

    private static BlockPos surfaceAir(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }
}
