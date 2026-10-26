package com.howierux.mortalexpansion.worldgen.structure;

import com.howierux.mortalexpansion.worldgen.TerrainRuntime;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

/**
 * One coordinated cemetery structure for the Graveyard biome.
 *
 * The biome supplies atmosphere/haunt. This structure supplies all cemetery
 * blocks: perimeter, grave rows, and mausoleums.
 */
public final class GraveyardCemeteryStructure extends Structure {
    public static final MapCodec<GraveyardCemeteryStructure> CODEC =
            simpleCodec(GraveyardCemeteryStructure::new);

    public static final int SIZE = 56;
    private static final int HALF = SIZE / 2;

    public GraveyardCemeteryStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int centerX = context.chunkPos().getMiddleBlockX();
        int centerZ = context.chunkPos().getMiddleBlockZ();
        Climate.Sampler sampler = context.randomState().sampler();

        int minX = centerX - HALF;
        int minZ = centerZ - HALF;
        int maxX = minX + SIZE - 1;
        int maxZ = minZ + SIZE - 1;

        // The cemetery perimeter is the maximum allowed edge of the structure.
        // Every quart cell touched by the 56x56 footprint must still belong to
        // the Graveyard biome. This prevents the rectangular cemetery/fence
        // from spilling into oceans or neighboring biomes.
        int minQuartX = QuartPos.fromBlock(minX);
        int maxQuartX = QuartPos.fromBlock(maxX);
        int minQuartZ = QuartPos.fromBlock(minZ);
        int maxQuartZ = QuartPos.fromBlock(maxZ);
        int quartY = QuartPos.fromBlock(64);

        for (int quartX = minQuartX; quartX <= maxQuartX; quartX++) {
            for (int quartZ = minQuartZ; quartZ <= maxQuartZ; quartZ++) {
                if (!context.biomeSource()
                        .getNoiseBiome(
                                quartX,
                                quartY,
                                quartZ,
                                sampler
                        )
                        .is(ModBiomes.GRAVEYARD)) {
                    return Optional.empty();
                }
            }
        }

        BlockPos locator = new BlockPos(centerX, 64, centerZ);

        return Optional.of(new GenerationStub(
                locator,
                builder -> builder.addPiece(
                        new GraveyardCemeteryPiece(minX, minZ)
                )
        ));
    }

    @Override
    public StructureType<?> type() {
        return TerrainRuntime.CEMETERY_TYPE;
    }
}
