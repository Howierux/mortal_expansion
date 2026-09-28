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
 * Builds at most one main cemetery enclosure per 2x2-chunk (32x32) cell.
 *
 * The previous 64x64 implementation made every Graveyard chunk scan 4,096
 * terrain columns. Teleporting into newly generated Graveyard terrain could
 * therefore multiply that work across many chunks and stall world generation.
 *
 * This version first elects a single Graveyard chunk as the cell owner using
 * only four center-biome checks. Only that owner performs the 32x32 scan.
 */
public final class GraveyardBorderFeature extends Feature<NoneFeatureConfiguration> {
    private static final int CELL_SIZE = 32;
    private static final int CHUNK_SIZE = 16;
    private static final int MIN_WIDTH = 8;
    private static final int MIN_DEPTH = 8;

    public GraveyardBorderFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int currentChunkX = Math.floorDiv(origin.getX(), CHUNK_SIZE);
        int currentChunkZ = Math.floorDiv(origin.getZ(), CHUNK_SIZE);

        int cellChunkX = Math.floorDiv(currentChunkX, 2) * 2;
        int cellChunkZ = Math.floorDiv(currentChunkZ, 2) * 2;

        ChunkOwner owner = findOwner(level, cellChunkX, cellChunkZ);
        if (owner == null
                || owner.chunkX() != currentChunkX
                || owner.chunkZ() != currentChunkZ) {
            return false;
        }

        int cellMinX = cellChunkX * CHUNK_SIZE;
        int cellMinZ = cellChunkZ * CHUNK_SIZE;

        boolean[][] usable = new boolean[CELL_SIZE][CELL_SIZE];

        for (int z = 0; z < CELL_SIZE; z++) {
            for (int x = 0; x < CELL_SIZE; x++) {
                usable[z][x] = isUsableGraveyardColumn(
                        level,
                        cellMinX + x,
                        cellMinZ + z
                );
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
            border.add(surfaceAir(
                    level,
                    cellMinX + x,
                    cellMinZ + rectangle.minZ()
            ));
            border.add(surfaceAir(
                    level,
                    cellMinX + x,
                    cellMinZ + rectangle.maxZ()
            ));
        }

        for (int z = rectangle.minZ() + 1; z < rectangle.maxZ(); z++) {
            border.add(surfaceAir(
                    level,
                    cellMinX + rectangle.minX(),
                    cellMinZ + z
            ));
            border.add(surfaceAir(
                    level,
                    cellMinX + rectangle.maxX(),
                    cellMinZ + z
            ));
        }

        for (BlockPos surface : border) {
            if (level.getBlockState(surface).canBeReplaced()) {
                level.setBlock(
                        surface.below(),
                        ModBlocks.GRAVESTONE.get().defaultBlockState(),
                        2
                );
            }
        }

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

        for (BlockPos surface : border) {
            if (level.getBlockState(surface).is(ModBlocks.STONE_FENCE.get())) {
                level.setBlock(
                        surface,
                        fenceStateFor(level, border, surface),
                        2
                );
            }
        }

        return true;
    }

    /**
     * Pick one Graveyard chunk per 2x2 cell before doing any expensive scan.
     * A cell with no Graveyard at any chunk center gets no enclosure.
     */
    private static ChunkOwner findOwner(
            WorldGenLevel level,
            int cellChunkX,
            int cellChunkZ
    ) {
        for (int dz = 0; dz < 2; dz++) {
            for (int dx = 0; dx < 2; dx++) {
                int chunkX = cellChunkX + dx;
                int chunkZ = cellChunkZ + dz;
                int centerX = chunkX * CHUNK_SIZE + 8;
                int centerZ = chunkZ * CHUNK_SIZE + 8;
                BlockPos center = surfaceAir(level, centerX, centerZ);

                if (level.getBiome(center).is(ModBiomes.GRAVEYARD)) {
                    return new ChunkOwner(chunkX, chunkZ);
                }
            }
        }

        return null;
    }

    private static boolean isUsableGraveyardColumn(
            WorldGenLevel level,
            int x,
            int z
    ) {
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

    private static Rectangle largestRectangle(boolean[][] grid) {
        int widthLimit = grid[0].length;
        int depthLimit = grid.length;
        int[] heights = new int[widthLimit];

        Rectangle best = null;
        int bestArea = 0;

        for (int z = 0; z < depthLimit; z++) {
            for (int x = 0; x < widthLimit; x++) {
                heights[x] = grid[z][x] ? heights[x] + 1 : 0;
            }

            int[] stack = new int[widthLimit + 1];
            int stackSize = 0;

            for (int x = 0; x <= widthLimit; x++) {
                int currentHeight = x == widthLimit ? 0 : heights[x];

                while (stackSize > 0
                        && heights[stack[stackSize - 1]] > currentHeight) {
                    int index = stack[--stackSize];
                    int height = heights[index];
                    int left = stackSize == 0
                            ? 0
                            : stack[stackSize - 1] + 1;
                    int rectangleWidth = x - left;
                    int area = rectangleWidth * height;

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

                if (x < widthLimit) {
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
                .setValue(
                        FenceBlock.NORTH,
                        connects(level, planned, pos, Direction.NORTH)
                )
                .setValue(
                        FenceBlock.EAST,
                        connects(level, planned, pos, Direction.EAST)
                )
                .setValue(
                        FenceBlock.SOUTH,
                        connects(level, planned, pos, Direction.SOUTH)
                )
                .setValue(
                        FenceBlock.WEST,
                        connects(level, planned, pos, Direction.WEST)
                );
    }

    private static boolean connects(
            WorldGenLevel level,
            Set<BlockPos> planned,
            BlockPos pos,
            Direction direction
    ) {
        BlockPos neighbor = pos.relative(direction);
        return planned.contains(neighbor)
                || level.getBlockState(neighbor)
                        .is(ModBlocks.STONE_FENCE.get());
    }

    private static BlockPos surfaceAir(
            WorldGenLevel level,
            int x,
            int z
    ) {
        int y = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                x,
                z
        );
        return new BlockPos(x, y, z);
    }

    private record ChunkOwner(int chunkX, int chunkZ) {}

    private record Rectangle(
            int minX,
            int minZ,
            int maxX,
            int maxZ
    ) {
        int width() {
            return maxX - minX + 1;
        }

        int depth() {
            return maxZ - minZ + 1;
        }
    }
}
