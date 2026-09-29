package com.howierux.mortalexpansion.worldgen.structure;

import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * Chunk-safe 56x56 cemetery.
 *
 * Layout:
 * - stone-fence perimeter on Gravestone foundation
 * - four compact 5x5 mausoleums
 * - six organized grave-row sections
 *
 * Every placement is clipped to the chunk bounding box supplied by Minecraft,
 * so this structure can safely span several chunks.
 */
public final class GraveyardCemeteryPiece extends StructurePiece {
    private static final int SIZE = GraveyardCemeteryStructure.SIZE;
    private static final int MAX = SIZE - 1;

    private final int minX;
    private final int minZ;

    public GraveyardCemeteryPiece(int minX, int minZ) {
        super(
                ModStructures.GRAVEYARD_CEMETERY_PIECE.get(),
                0,
                new BoundingBox(minX, 0, minZ, minX + MAX, 320, minZ + MAX)
        );
        this.minX = minX;
        this.minZ = minZ;
    }

    public GraveyardCemeteryPiece(
            StructurePieceSerializationContext context,
            CompoundTag tag
    ) {
        super(ModStructures.GRAVEYARD_CEMETERY_PIECE.get(), tag);
        this.minX = tag.contains("MinX") ? tag.getInt("MinX") : this.boundingBox.minX();
        this.minZ = tag.contains("MinZ") ? tag.getInt("MinZ") : this.boundingBox.minZ();
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext context,
            CompoundTag tag
    ) {
        tag.putInt("MinX", minX);
        tag.putInt("MinZ", minZ);
    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox chunkBB,
            ChunkPos chunkPos,
            BlockPos referencePos
    ) {
        buildPerimeter(level, chunkBB);

        // Four mausoleums, deliberately inset from the perimeter.
        buildMausoleum(level, chunkBB, random, minX + 10, minZ + 10);
        buildMausoleum(level, chunkBB, random, minX + 45, minZ + 10);
        buildMausoleum(level, chunkBB, random, minX + 10, minZ + 45);
        buildMausoleum(level, chunkBB, random, minX + 45, minZ + 45);

        // Six grave-row sections through the central cemetery.
        buildGraveSection(level, chunkBB, minX + 16, minZ + 17, Direction.SOUTH);
        buildGraveSection(level, chunkBB, minX + 28, minZ + 17, Direction.SOUTH);
        buildGraveSection(level, chunkBB, minX + 40, minZ + 17, Direction.SOUTH);
        buildGraveSection(level, chunkBB, minX + 16, minZ + 31, Direction.SOUTH);
        buildGraveSection(level, chunkBB, minX + 28, minZ + 31, Direction.SOUTH);
        buildGraveSection(level, chunkBB, minX + 40, minZ + 31, Direction.SOUTH);
    }

    private void buildPerimeter(WorldGenLevel level, BoundingBox chunkBB) {
        for (int x = minX; x <= minX + MAX; x++) {
            placeFence(level, chunkBB, x, minZ);
            placeFence(level, chunkBB, x, minZ + MAX);
        }

        for (int z = minZ + 1; z < minZ + MAX; z++) {
            placeFence(level, chunkBB, minX, z);
            placeFence(level, chunkBB, minX + MAX, z);
        }
    }

    private void placeFence(
            WorldGenLevel level,
            BoundingBox chunkBB,
            int x,
            int z
    ) {
        BlockPos fencePos = surfaceAir(level, x, z);
        if (!chunkBB.isInside(fencePos)) {
            return;
        }

        level.setBlock(
                fencePos.below(),
                ModBlocks.GRAVESTONE.get().defaultBlockState(),
                2
        );

        BlockState fence = ModBlocks.STONE_FENCE.get()
                .defaultBlockState()
                .setValue(FenceBlock.NORTH, fenceConnects(level, x, z - 1))
                .setValue(FenceBlock.EAST, fenceConnects(level, x + 1, z))
                .setValue(FenceBlock.SOUTH, fenceConnects(level, x, z + 1))
                .setValue(FenceBlock.WEST, fenceConnects(level, x - 1, z));

        level.setBlock(fencePos, fence, 2);
        level.getChunk(fencePos).markPosForPostprocessing(fencePos);
    }

    private boolean isPerimeter(int x, int z) {
        int maxX = minX + MAX;
        int maxZ = minZ + MAX;

        return x >= minX && x <= maxX
                && z >= minZ && z <= maxZ
                && (x == minX || x == maxX || z == minZ || z == maxZ);
    }

    private boolean fenceConnects(WorldGenLevel level, int x, int z) {
        if (isPerimeter(x, z)) {
            return true;
        }

        int y = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                x,
                z
        );

        for (int dy = -2; dy <= 2; dy++) {
            if (level.getBlockState(new BlockPos(x, y + dy, z))
                    .is(ModBlocks.GRAVESTONE.get())) {
                return true;
            }
        }

        return false;
    }

    private void buildGraveSection(
            WorldGenLevel level,
            BoundingBox chunkBB,
            int startX,
            int startZ,
            Direction bodyDirection
    ) {
        Direction rowDirection = bodyDirection.getClockWise();

        // 3 rows x 4 graves. Six sections produces a substantial cemetery
        // without filling every open square with markers.
        for (int row = 0; row < 3; row++) {
            for (int grave = 0; grave < 4; grave++) {
                int headX = startX
                        + bodyDirection.getStepX() * (row * 4)
                        + rowDirection.getStepX() * (grave * 2);
                int headZ = startZ
                        + bodyDirection.getStepZ() * (row * 4)
                        + rowDirection.getStepZ() * (grave * 2);

                placeGravePlot(level, chunkBB, headX, headZ, bodyDirection);
            }
        }
    }

    private void placeGravePlot(
            WorldGenLevel level,
            BoundingBox chunkBB,
            int headX,
            int headZ,
            Direction bodyDirection
    ) {
        BlockPos gravePos = surfaceAir(level, headX, headZ);
        BlockPos dirtOne = surfaceAir(
                level,
                headX + bodyDirection.getStepX(),
                headZ + bodyDirection.getStepZ()
        );
        BlockPos dirtTwo = surfaceAir(
                level,
                headX + bodyDirection.getStepX() * 2,
                headZ + bodyDirection.getStepZ() * 2
        );

        if (chunkBB.isInside(gravePos)) {
            level.setBlock(
                    gravePos,
                    ModBlocks.GRAVE.get().defaultBlockState()
                            .setValue(GraveBlock.FACING, bodyDirection)
                            .setValue(GraveBlock.DEPTH, depthToward(bodyDirection))
                            .setValue(GraveBlock.NATURAL, true),
                    2
            );
            markNaturalGrave(level, gravePos);
        }

        if (chunkBB.isInside(dirtOne.below())) {
            level.setBlock(
                    dirtOne.below(),
                    Blocks.COARSE_DIRT.defaultBlockState(),
                    2
            );
        }

        if (chunkBB.isInside(dirtTwo.below())) {
            level.setBlock(
                    dirtTwo.below(),
                    Blocks.COARSE_DIRT.defaultBlockState(),
                    2
            );
        }
    }

    private void buildMausoleum(
            WorldGenLevel level,
            BoundingBox chunkBB,
            RandomSource random,
            int centerX,
            int centerZ
    ) {
        int surfaceY = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                centerX,
                centerZ
        );
        int floorY = surfaceY - 1;

        // 5x5 floor and roof.
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                placeIfInside(
                        level,
                        chunkBB,
                        new BlockPos(centerX + dx, floorY, centerZ + dz),
                        ModBlocks.GRAVESTONE.get().defaultBlockState()
                );
                placeIfInside(
                        level,
                        chunkBB,
                        new BlockPos(centerX + dx, floorY + 4, centerZ + dz),
                        ModBlocks.GRAVESTONE.get().defaultBlockState()
                );

                for (int dy = 1; dy <= 3; dy++) {
                    BlockPos pos = new BlockPos(
                            centerX + dx,
                            floorY + dy,
                            centerZ + dz
                    );

                    boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                    boolean doorway = dz == 2 && dx == 0 && dy <= 2;

                    if (edge && !doorway) {
                        placeIfInside(
                                level,
                                chunkBB,
                                pos,
                                ModBlocks.GRAVESTONE.get().defaultBlockState()
                        );
                    } else if (!edge) {
                        placeIfInside(
                                level,
                                chunkBB,
                                pos,
                                Blocks.AIR.defaultBlockState()
                        );
                    }
                }
            }
        }

        placeIfInside(
                level,
                chunkBB,
                new BlockPos(centerX - 1, floorY + 1, centerZ + 3),
                ModBlocks.STONE_FENCE.get().defaultBlockState()
        );
        placeIfInside(
                level,
                chunkBB,
                new BlockPos(centerX + 1, floorY + 1, centerZ + 3),
                ModBlocks.STONE_FENCE.get().defaultBlockState()
        );

        decorateMausoleumWithGraveMoss(
                level,
                chunkBB,
                random,
                centerX,
                centerZ,
                floorY
        );

        BlockPos interiorGravePos = new BlockPos(centerX, floorY + 1, centerZ - 1);
        placeIfInside(
                level,
                chunkBB,
                interiorGravePos,
                ModBlocks.GRAVE.get().defaultBlockState()
                        .setValue(GraveBlock.FACING, Direction.SOUTH)
                        .setValue(GraveBlock.DEPTH, 1)
                        .setValue(GraveBlock.NATURAL, true)
        );
        if (chunkBB.isInside(interiorGravePos)) {
            markNaturalGrave(level, interiorGravePos);
        }
    }

    private static void decorateMausoleumWithGraveMoss(
            WorldGenLevel level,
            BoundingBox chunkBB,
            RandomSource random,
            int centerX,
            int centerZ,
            int floorY
    ) {
        for (int dy = 1; dy <= 3; dy++) {
            for (int offset = -2; offset <= 2; offset++) {
                tryPlaceGraveMoss(
                        level, chunkBB, random,
                        new BlockPos(centerX - 3, floorY + dy, centerZ + offset),
                        Direction.EAST, 0.22F
                );
                tryPlaceGraveMoss(
                        level, chunkBB, random,
                        new BlockPos(centerX + 3, floorY + dy, centerZ + offset),
                        Direction.WEST, 0.22F
                );
                tryPlaceGraveMoss(
                        level, chunkBB, random,
                        new BlockPos(centerX + offset, floorY + dy, centerZ - 3),
                        Direction.SOUTH, 0.22F
                );
                tryPlaceGraveMoss(
                        level, chunkBB, random,
                        new BlockPos(centerX + offset, floorY + dy, centerZ + 3),
                        Direction.NORTH, 0.22F
                );
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                tryPlaceGraveMoss(
                        level, chunkBB, random,
                        new BlockPos(centerX + dx, floorY + 5, centerZ + dz),
                        Direction.DOWN, 0.28F
                );
            }
        }
    }

    private static void tryPlaceGraveMoss(
            WorldGenLevel level,
            BoundingBox chunkBB,
            RandomSource random,
            BlockPos mossPos,
            Direction supportDirection,
            float chance
    ) {
        if (!chunkBB.isInside(mossPos) || random.nextFloat() >= chance) {
            return;
        }

        BlockPos supportPos = mossPos.relative(supportDirection);
        if (!chunkBB.isInside(supportPos)
                || !level.getBlockState(supportPos).is(ModBlocks.GRAVESTONE.get())) {
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

    private static void placeIfInside(
            WorldGenLevel level,
            BoundingBox chunkBB,
            BlockPos pos,
            BlockState state
    ) {
        if (chunkBB.isInside(pos)) {
            level.setBlock(pos, state, 2);
        }
    }

    private static void markNaturalGrave(WorldGenLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof GraveBlockEntity grave) {
            grave.markNaturallyGeneratedFromWorldgen();
        }
    }

    private static int depthToward(Direction direction) {
        return switch (direction) {
            case NORTH, WEST -> 0;
            case SOUTH, EAST -> 2;
            default -> 1;
        };
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
}
