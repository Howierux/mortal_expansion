package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds the largest axis-aligned rectangle of usable Graveyard terrain inside
 * the generating chunk and fences that rectangle instead of tracing the
 * irregular biome edge.
 */
public final class GraveyardBorderFeature extends Feature<NoneFeatureConfiguration> {
    private static final int CHUNK_SIZE = 16;
    private static final int MIN_WIDTH = 5;
    private static final int MIN_DEPTH = 5;

    public GraveyardBorderFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int minX = origin.getX() & ~15;
        int minZ = origin.getZ() & ~15;

        boolean[][] usable = new boolean[CHUNK_SIZE][CHUNK_SIZE];

        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                usable[z][x] = isUsableGraveyardColumn(level, minX + x, minZ + z);
            }
        }

        Rectangle rectangle = largestRectangle(usable);
        if (rectangle == null
                || rectangle.width() < MIN_WIDTH
                || rectangle.depth() < MIN_DEPTH) {
            return false;
        }

        Set<BlockPos> border = new HashSet<>();

        for (int x = rectangle.minX(); x <= rectangle.maxX(); x++) {
            border.add(surfaceAir(level, minX + x, minZ + rectangle.minZ()));
            border.add(surfaceAir(level, minX + x, minZ + rectangle.maxZ()));
        }

        for (int z = rectangle.minZ() + 1; z < rectangle.maxZ(); z++) {
            border.add(surfaceAir(level, minX + rectangle.minX(), minZ + z));
            border.add(surfaceAir(level, minX + rectangle.maxX(), minZ + z));
        }

        // Gravestone foundation first.
        for (BlockPos surface : border) {
            if (level.getBlockState(surface).canBeReplaced()) {
                level.setBlock(
                        surface.below(),
                        ModBlocks.GRAVESTONE.get().defaultBlockState(),
                        2
                );
            }
        }

        // Then Stone Fence with explicit connections. This avoids depending on
        // worldgen neighbor-update order.
        for (BlockPos surface : border) {
            if (!level.getBlockState(surface).canBeReplaced()) {
                continue;
            }

            level.setBlock(
                    surface,
                    fenceStateFor(level, border, surface),
                    2
            );
        }

        // Re-evaluate once all fence blocks are present.
        for (BlockPos surface : border) {
            if (level.getBlockState(surface).is(ModBlocks.STONE_FENCE.get())) {
                level.setBlock(surface, fenceStateFor(level, border, surface), 2);
            }
        }

        return true;
    }

    /**
     * Graveyard cells on water, sand, sandstone, or gravel-like beach terrain
     * are excluded even if a modded worldgen combination produces an unusual
     * coastal overlap.
     */
    private static boolean isUsableGraveyardColumn(WorldGenLevel level, int x, int z) {
        BlockPos surface = surfaceAir(level, x, z);
        BlockState ground = level.getBlockState(surface.below());

        if (!level.getBiome(surface).is(ModBiomes.GRAVEYARD)) {
            return false;
        }

        if (!level.getFluidState(surface).isEmpty()
                || !level.getFluidState(surface.below()).isEmpty()) {
            return false;
        }

        if (ground.is(Blocks.SAND)
                || ground.is(Blocks.RED_SAND)
                || ground.is(Blocks.SANDSTONE)
                || ground.is(Blocks.RED_SANDSTONE)
                || ground.is(Blocks.GRAVEL)) {
            return false;
        }

        return ground.is(BlockTags.DIRT)
                || ground.is(Blocks.STONE)
                || ground.is(ModBlocks.GRAVESTONE.get());
    }

    /**
     * O(width * depth) histogram algorithm for the maximum all-true rectangle.
     */
    private static Rectangle largestRectangle(boolean[][] grid) {
        int[] heights = new int[CHUNK_SIZE];
        Rectangle best = null;
        int bestArea = 0;

        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                heights[x] = grid[z][x] ? heights[x] + 1 : 0;
            }

            int[] stack = new int[CHUNK_SIZE + 1];
            int stackSize = 0;

            for (int x = 0; x <= CHUNK_SIZE; x++) {
                int currentHeight = x == CHUNK_SIZE ? 0 : heights[x];

                while (stackSize > 0
                        && heights[stack[stackSize - 1]] > currentHeight) {
                    int index = stack[--stackSize];
                    int height = heights[index];
                    int left = stackSize == 0 ? 0 : stack[stackSize - 1] + 1;
                    int width = x - left;
                    int area = width * height;

                    if (area > bestArea) {
                        bestArea = area;
                        best = new Rectangle(
                                left,
                                z - height + 1,
                                x - 1,
                                z
                        );
                    }
                }

                if (x < CHUNK_SIZE) {
                    stack[stackSize++] = x;
                }
            }
        }

        return best;
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

    private static BlockPos surfaceAir(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                x,
                z
        );
        return new BlockPos(x, y, z);
    }

    private record Rectangle(int minX, int minZ, int maxX, int maxZ) {
        int width() {
            return maxX - minX + 1;
        }

        int depth() {
            return maxZ - minZ + 1;
        }
    }
}
