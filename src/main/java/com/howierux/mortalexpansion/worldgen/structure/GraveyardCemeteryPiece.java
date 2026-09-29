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
        // Centers are nudged so every mausoleum fits entirely inside one
        // chunk. This prevents structure generation from synchronously
        // reading/writing neighboring chunks.
        BlockPos mausoleumNW = safeMausoleumCenter(minX + 10, minZ + 10);
        BlockPos mausoleumNE = safeMausoleumCenter(minX + 45, minZ + 10);
        BlockPos mausoleumSW = safeMausoleumCenter(minX + 10, minZ + 45);
        BlockPos mausoleumSE = safeMausoleumCenter(minX + 45, minZ + 45);

        buildMausoleum(level, chunkBB, random, mausoleumNW.getX(), mausoleumNW.getZ());
        buildMausoleum(level, chunkBB, random, mausoleumNE.getX(), mausoleumNE.getZ());
        buildMausoleum(level, chunkBB, random, mausoleumSW.getX(), mausoleumSW.getZ());
        buildMausoleum(level, chunkBB, random, mausoleumSE.getX(), mausoleumSE.getZ());

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
        if (!containsXZ(chunkBB, x, z)) {
            return;
        }

        BlockPos fencePos = surfaceAir(level, x, z);

        level.setBlock(
                fencePos.below(),
                ModBlocks.GRAVESTONE.get().defaultBlockState(),
                2
        );

        BlockState fence = ModBlocks.STONE_FENCE.get()
                .defaultBlockState()
                .setValue(FenceBlock.NORTH, fenceConnects(x, z - 1))
                .setValue(FenceBlock.EAST, fenceConnects(x + 1, z))
                .setValue(FenceBlock.SOUTH, fenceConnects(x, z + 1))
                .setValue(FenceBlock.WEST, fenceConnects(x - 1, z));

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

    private boolean fenceConnects(int x, int z) {
        // Perimeter connectivity is fully known from the cemetery geometry.
        // Never inspect another chunk while a structure piece is generating.
        return isPerimeter(x, z);
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
        if (containsXZ(chunkBB, headX, headZ)) {
            BlockPos gravePos = surfaceAir(level, headX, headZ);
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

        int dirtOneX = headX + bodyDirection.getStepX();
        int dirtOneZ = headZ + bodyDirection.getStepZ();
        if (containsXZ(chunkBB, dirtOneX, dirtOneZ)) {
            BlockPos dirtOne = surfaceAir(level, dirtOneX, dirtOneZ);
            level.setBlock(
                    dirtOne.below(),
                    Blocks.COARSE_DIRT.defaultBlockState(),
                    2
            );
        }

        int dirtTwoX = headX + bodyDirection.getStepX() * 2;
        int dirtTwoZ = headZ + bodyDirection.getStepZ() * 2;
        if (containsXZ(chunkBB, dirtTwoX, dirtTwoZ)) {
            BlockPos dirtTwo = surfaceAir(level, dirtTwoX, dirtTwoZ);
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
        if (!containsXZ(chunkBB, centerX, centerZ)) {
            return;
        }

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

    private static BlockPos safeMausoleumCenter(int x, int z) {
        ChunkPos chunk = new ChunkPos(new BlockPos(x, 0, z));
        int safeX = Math.max(
                chunk.getMinBlockX() + 3,
                Math.min(chunk.getMaxBlockX() - 3, x)
        );
        int safeZ = Math.max(
                chunk.getMinBlockZ() + 3,
                Math.min(chunk.getMaxBlockZ() - 3, z)
        );
        return new BlockPos(safeX, 0, safeZ);
    }

    private static boolean containsXZ(BoundingBox box, int x, int z) {
        return x >= box.minX()
                && x <= box.maxX()
                && z >= box.minZ()
                && z <= box.maxZ();
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
