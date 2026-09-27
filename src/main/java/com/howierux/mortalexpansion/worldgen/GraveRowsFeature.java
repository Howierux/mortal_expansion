package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Generates organized grave plots in Graveyard biomes.
 *
 * Each plot is:
 *   [Grave][Coarse Dirt][Coarse Dirt]
 *
 * The generated Grave is shifted to the edge of its block that touches the
 * burial plot, while hand-placed Graves may also use the center position.
 */
public final class GraveRowsFeature extends Feature<NoneFeatureConfiguration> {
    private static final Direction[] HORIZONTAL = {
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    };

    public GraveRowsFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        Direction bodyDirection = HORIZONTAL[random.nextInt(HORIZONTAL.length)];
        Direction rowDirection = bodyDirection.getClockWise();

        int rows = 2 + random.nextInt(2);
        int gravesPerRow = 3 + random.nextInt(3);
        int rowWidth = (gravesPerRow - 1) * 2;

        BlockPos origin = context.origin();
        int placed = 0;

        for (int row = 0; row < rows; row++) {
            BlockPos rowStart = origin
                    .relative(bodyDirection, row * 4)
                    .relative(rowDirection, -(rowWidth / 2));

            for (int column = 0; column < gravesPerRow; column++) {
                BlockPos headXZ = rowStart.relative(rowDirection, column * 2);

                if (placePlot(level, headXZ, bodyDirection)) {
                    placed++;
                }
            }
        }

        return placed > 0;
    }

    private static boolean placePlot(
            WorldGenLevel level,
            BlockPos headXZ,
            Direction bodyDirection
    ) {
        BlockPos gravePos = surfaceAir(level, headXZ);
        BlockPos dirtOnePos = surfaceAir(level, headXZ.relative(bodyDirection, 1));
        BlockPos dirtTwoPos = surfaceAir(level, headXZ.relative(bodyDirection, 2));

        if (!level.getBiome(gravePos).is(ModBiomes.GRAVEYARD)
                || !level.getBiome(dirtOnePos).is(ModBiomes.GRAVEYARD)
                || !level.getBiome(dirtTwoPos).is(ModBiomes.GRAVEYARD)) {
            return false;
        }

        BlockPos graveGround = gravePos.below();
        BlockPos dirtOneGround = dirtOnePos.below();
        BlockPos dirtTwoGround = dirtTwoPos.below();

        if (!isUsableGround(level.getBlockState(graveGround))
                || !isUsableGround(level.getBlockState(dirtOneGround))
                || !isUsableGround(level.getBlockState(dirtTwoGround))) {
            return false;
        }

        if (!level.getBlockState(gravePos).canBeReplaced()) {
            return false;
        }

        BlockState graveState = ModBlocks.GRAVE.get()
                .defaultBlockState()
                .setValue(GraveBlock.FACING, bodyDirection)
                .setValue(GraveBlock.DEPTH, depthToward(bodyDirection));

        level.setBlock(gravePos, graveState, 2);
        level.setBlock(dirtOneGround, Blocks.COARSE_DIRT.defaultBlockState(), 2);
        level.setBlock(dirtTwoGround, Blocks.COARSE_DIRT.defaultBlockState(), 2);

        return true;
    }

    private static int depthToward(Direction direction) {
        return switch (direction) {
            case NORTH, WEST -> 0;
            case SOUTH, EAST -> 2;
            default -> 1;
        };
    }

    private static boolean isUsableGround(BlockState state) {
        return state.is(BlockTags.DIRT);
    }

    private static BlockPos surfaceAir(WorldGenLevel level, BlockPos xz) {
        int y = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                xz.getX(),
                xz.getZ()
        );
        return new BlockPos(xz.getX(), y, xz.getZ());
    }
}
