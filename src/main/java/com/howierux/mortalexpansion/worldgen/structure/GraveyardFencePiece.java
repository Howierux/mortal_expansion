package com.howierux.mortalexpansion.worldgen.structure;

import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * Chunk-safe rectangular Graveyard fence.
 *
 * Minecraft calls postProcess separately for each chunk intersecting this
 * piece's bounding box. We only touch positions inside the supplied chunkBB.
 */
public final class GraveyardFencePiece extends StructurePiece {
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;

    public GraveyardFencePiece(int minX, int minZ, int maxX, int maxZ) {
        super(
                ModStructures.GRAVEYARD_FENCE_PIECE.get(),
                0,
                new BoundingBox(minX, 0, minZ, maxX, 320, maxZ)
        );
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    public GraveyardFencePiece(
            StructurePieceSerializationContext context,
            CompoundTag tag
    ) {
        super(ModStructures.GRAVEYARD_FENCE_PIECE.get(), tag);
        this.minX = tag.contains("MinX") ? tag.getInt("MinX") : this.boundingBox.minX();
        this.minZ = tag.contains("MinZ") ? tag.getInt("MinZ") : this.boundingBox.minZ();
        this.maxX = tag.contains("MaxX") ? tag.getInt("MaxX") : this.boundingBox.maxX();
        this.maxZ = tag.contains("MaxZ") ? tag.getInt("MaxZ") : this.boundingBox.maxZ();
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext context,
            CompoundTag tag
    ) {
        tag.putInt("MinX", minX);
        tag.putInt("MinZ", minZ);
        tag.putInt("MaxX", maxX);
        tag.putInt("MaxZ", maxZ);
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
        for (int x = minX; x <= maxX; x++) {
            placeFenceColumn(level, chunkBB, x, minZ);
            if (maxZ != minZ) {
                placeFenceColumn(level, chunkBB, x, maxZ);
            }
        }

        for (int z = minZ + 1; z < maxZ; z++) {
            placeFenceColumn(level, chunkBB, minX, z);
            if (maxX != minX) {
                placeFenceColumn(level, chunkBB, maxX, z);
            }
        }
    }

    private void placeFenceColumn(
            WorldGenLevel level,
            BoundingBox chunkBB,
            int x,
            int z
    ) {
        int y = level.getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                x,
                z
        );

        BlockPos fencePos = new BlockPos(x, y, z);
        if (!chunkBB.isInside(fencePos)) {
            return;
        }

        BlockPos foundation = fencePos.below();
        level.setBlock(
                foundation,
                ModBlocks.GRAVESTONE.get().defaultBlockState(),
                2
        );

        BlockState fence = ModBlocks.STONE_FENCE.get()
                .defaultBlockState()
                .setValue(FenceBlock.NORTH, isPerimeter(x, z - 1))
                .setValue(FenceBlock.EAST, isPerimeter(x + 1, z))
                .setValue(FenceBlock.SOUTH, isPerimeter(x, z + 1))
                .setValue(FenceBlock.WEST, isPerimeter(x - 1, z));

        level.setBlock(fencePos, fence, 2);
        level.getChunk(fencePos).markPosForPostprocessing(fencePos);
    }

    private boolean isPerimeter(int x, int z) {
        boolean insideX = x >= minX && x <= maxX;
        boolean insideZ = z >= minZ && z <= maxZ;

        return insideX && insideZ
                && (x == minX || x == maxX || z == minZ || z == maxZ);
    }
}
