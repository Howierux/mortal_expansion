package com.howierux.mortalexpansion.worldgen.structure;

import com.howierux.mortalexpansion.registry.ModStructures;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

/**
 * Selects one large rectangular cemetery enclosure using biome-source queries.
 *
 * BiomeSource queries do not load neighboring chunks, so rectangle selection is
 * safe during structure start generation. Actual blocks are placed later by a
 * StructurePiece one intersecting chunk at a time.
 */
public final class GraveyardFenceStructure extends Structure {
    public static final MapCodec<GraveyardFenceStructure> CODEC =
            simpleCodec(GraveyardFenceStructure::new);

    private static final int SAMPLE_STEP = 4;
    private static final int SAMPLE_RADIUS = 10; // 84x84 block search area
    private static final int GRID = SAMPLE_RADIUS * 2 + 1;
    private static final int MIN_BLOCK_WIDTH = 36;
    private static final int MIN_BLOCK_DEPTH = 36;

    public GraveyardFenceStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int centerX = context.chunkPos().getMiddleBlockX();
        int centerZ = context.chunkPos().getMiddleBlockZ();

        boolean[][] graveyard = new boolean[GRID][GRID];
        Climate.Sampler sampler = context.randomState().sampler();

        for (int gz = 0; gz < GRID; gz++) {
            for (int gx = 0; gx < GRID; gx++) {
                int x = centerX + (gx - SAMPLE_RADIUS) * SAMPLE_STEP;
                int z = centerZ + (gz - SAMPLE_RADIUS) * SAMPLE_STEP;

                graveyard[gz][gx] = context.biomeSource()
                        .getNoiseBiome(
                                QuartPos.fromBlock(x),
                                QuartPos.fromBlock(64),
                                QuartPos.fromBlock(z),
                                sampler
                        )
                        .is(ModBiomes.GRAVEYARD);
            }
        }

        Rectangle sampleRect = largestRectangle(graveyard);
        if (sampleRect == null) {
            return Optional.empty();
        }

        int minX = centerX + (sampleRect.minX() - SAMPLE_RADIUS) * SAMPLE_STEP;
        int minZ = centerZ + (sampleRect.minZ() - SAMPLE_RADIUS) * SAMPLE_STEP;
        int maxX = centerX + (sampleRect.maxX() - SAMPLE_RADIUS) * SAMPLE_STEP
                + SAMPLE_STEP - 1;
        int maxZ = centerZ + (sampleRect.maxZ() - SAMPLE_RADIUS) * SAMPLE_STEP
                + SAMPLE_STEP - 1;

        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;

        if (width < MIN_BLOCK_WIDTH || depth < MIN_BLOCK_DEPTH) {
            return Optional.empty();
        }

        // Cap very large Graveyards to a cemetery-sized enclosure while
        // keeping the rectangle centered inside the largest usable area.
        final int maxFenceSize = 64;

        if (width > maxFenceSize) {
            int center = (minX + maxX) / 2;
            minX = center - (maxFenceSize / 2);
            maxX = minX + maxFenceSize - 1;
        }

        if (depth > maxFenceSize) {
            int center = (minZ + maxZ) / 2;
            minZ = center - (maxFenceSize / 2);
            maxZ = minZ + maxFenceSize - 1;
        }

        BlockPos locator = new BlockPos(
                (minX + maxX) / 2,
                64,
                (minZ + maxZ) / 2
        );

        return Optional.of(new GenerationStub(
                locator,
                builder -> builder.addPiece(
                        new GraveyardFencePiece(minX, minZ, maxX, maxZ)
                )
        ));
    }

    private static Rectangle largestRectangle(boolean[][] grid) {
        int width = grid[0].length;
        int[] heights = new int[width];
        Rectangle best = null;
        int bestArea = 0;

        for (int z = 0; z < grid.length; z++) {
            for (int x = 0; x < width; x++) {
                heights[x] = grid[z][x] ? heights[x] + 1 : 0;
            }

            int[] stack = new int[width + 1];
            int stackSize = 0;

            for (int x = 0; x <= width; x++) {
                int h = x == width ? 0 : heights[x];

                while (stackSize > 0
                        && heights[stack[stackSize - 1]] > h) {
                    int index = stack[--stackSize];
                    int height = heights[index];
                    int left = stackSize == 0
                            ? 0
                            : stack[stackSize - 1] + 1;
                    int rectWidth = x - left;
                    int area = rectWidth * height;

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

                if (x < width) {
                    stack[stackSize++] = x;
                }
            }
        }

        return best;
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.GRAVEYARD_FENCE.get();
    }

    private record Rectangle(int minX, int minZ, int maxX, int maxZ) {}
}
