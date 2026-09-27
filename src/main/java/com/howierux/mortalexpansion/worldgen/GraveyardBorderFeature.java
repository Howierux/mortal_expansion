package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * Traces the surface edge of Graveyard biome cells.
 *
 * Diagonal-only transitions are converted into a one-block orthogonal step so
 * the Stone Fence can form a continuous Minecraft-style perimeter.
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

        Set<BlockPos> border = new HashSet<>();

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                BlockPos surface = surfaceAir(level, minX + dx, minZ + dz);

                if (level.getBiome(surface).is(ModBiomes.GRAVEYARD)
                        && touchesNonGraveyard(level, surface)) {
                    border.add(surface);
                }
            }
        }

        bridgeDiagonalCorners(level, border, minX, minZ);

        for (BlockPos surface : border) {
            if (!level.getBlockState(surface).canBeReplaced()) {
                continue;
            }

            level.setBlock(
                    surface.below(),
                    ModBlocks.GRAVESTONE.get().defaultBlockState(),
                    2
            );
            level.setBlock(
                    surface,
                    fenceStateFor(level, border, surface),
                    2
            );
        }

        // Recalculate once more now that neighboring fence blocks exist.
        for (BlockPos surface : border) {
            if (level.getBlockState(surface).is(ModBlocks.STONE_FENCE.get())) {
                level.setBlock(surface, fenceStateFor(level, border, surface), 2);
            }
        }

        return !border.isEmpty();
    }

    private static void bridgeDiagonalCorners(
            WorldGenLevel level,
            Set<BlockPos> border,
            int minX,
            int minZ
    ) {
        Set<BlockPos> additions = new HashSet<>();
        Set<BlockPos> snapshot = new HashSet<>(border);

        for (BlockPos pos : snapshot) {
            for (int sx : new int[]{-1, 1}) {
                for (int sz : new int[]{-1, 1}) {
                    BlockPos diagonal = findAtXZ(snapshot, pos.getX() + sx, pos.getZ() + sz);
                    if (diagonal == null) {
                        continue;
                    }

                    BlockPos xStep = surfaceAir(level, pos.getX() + sx, pos.getZ());
                    BlockPos zStep = surfaceAir(level, pos.getX(), pos.getZ() + sz);

                    boolean hasX = findAtXZ(snapshot, xStep.getX(), xStep.getZ()) != null;
                    boolean hasZ = findAtXZ(snapshot, zStep.getX(), zStep.getZ()) != null;

                    if (hasX || hasZ) {
                        continue;
                    }

                    BlockPos candidate = isInsideChunk(xStep, minX, minZ)
                            && level.getBiome(xStep).is(ModBiomes.GRAVEYARD)
                            ? xStep
                            : zStep;

                    if (isInsideChunk(candidate, minX, minZ)
                            && level.getBiome(candidate).is(ModBiomes.GRAVEYARD)) {
                        additions.add(candidate);
                    }
                }
            }
        }

        border.addAll(additions);
    }

    private static BlockState fenceStateFor(
            WorldGenLevel level,
            Set<BlockPos> planned,
            BlockPos pos
    ) {
        return ModBlocks.STONE_FENCE.get().defaultBlockState()
                .setValue(FenceBlock.NORTH, connects(level, planned, pos, Direction.NORTH))
                .setValue(FenceBlock.EAST, connects(level, planned, pos, Direction.EAST))
                .setValue(FenceBlock.SOUTH, connects(level, planned, pos, Direction.SOUTH))
                .setValue(FenceBlock.WEST, connects(level, planned, pos, Direction.WEST));
    }

    private static boolean connects(
            WorldGenLevel level,
            Set<BlockPos> planned,
            BlockPos pos,
            Direction direction
    ) {
        BlockPos neighbor = pos.relative(direction);
        return planned.contains(neighbor)
                || level.getBlockState(neighbor).is(ModBlocks.STONE_FENCE.get());
    }

    private static BlockPos findAtXZ(Set<BlockPos> positions, int x, int z) {
        for (BlockPos pos : positions) {
            if (pos.getX() == x && pos.getZ() == z) {
                return pos;
            }
        }
        return null;
    }

    private static boolean isInsideChunk(BlockPos pos, int minX, int minZ) {
        return pos.getX() >= minX && pos.getX() < minX + 16
                && pos.getZ() >= minZ && pos.getZ() < minZ + 16;
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
