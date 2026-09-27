package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Traces the surface edge of Graveyard biome cells inside the current chunk.
 * Boundary terrain is replaced with Gravestone and topped with Stone Fence.
 */
public final class GraveyardBorderFeature extends Feature<NoneFeatureConfiguration> {
    private static final Direction[] HORIZONTAL = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    public GraveyardBorderFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int minX = origin.getX() & ~15;
        int minZ = origin.getZ() & ~15;
        int placed = 0;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = minX + dx;
                int z = minZ + dz;
                BlockPos surface = surfaceAir(level, x, z);

                if (!level.getBiome(surface).is(ModBiomes.GRAVEYARD)) {
                    continue;
                }

                if (!touchesNonGraveyard(level, surface)) {
                    continue;
                }

                BlockPos foundation = surface.below();
                BlockState above = level.getBlockState(surface);

                if (!above.canBeReplaced()) {
                    continue;
                }

                level.setBlock(foundation, ModBlocks.GRAVESTONE.get().defaultBlockState(), 3);
                level.setBlock(surface, ModBlocks.STONE_FENCE.get().defaultBlockState(), 3);
                placed++;
            }
        }

        return placed > 0;
    }

    private static boolean touchesNonGraveyard(WorldGenLevel level, BlockPos surface) {
        for (Direction direction : HORIZONTAL) {
            BlockPos neighborXZ = surface.relative(direction);
            BlockPos neighborSurface = surfaceAir(level, neighborXZ.getX(), neighborXZ.getZ());
            if (!level.getBiome(neighborSurface).is(ModBiomes.GRAVEYARD)) {
                return true;
            }
        }
        return false;
    }

    private static BlockPos surfaceAir(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }
}
