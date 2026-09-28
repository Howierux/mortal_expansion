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

        // Require the center plus an inner ring to be Graveyard. We deliberately
        // do not require every corner: the structure is allowed to occupy most
        // of a small Graveyard patch without needing the biome to be huge.
        int[] offsets = {0, -18, 18};

        for (int dx : offsets) {
            for (int dz : offsets) {
                if (Math.abs(dx) == 18 && Math.abs(dz) == 18) {
                    continue;
                }

                if (!context.biomeSource()
                        .getNoiseBiome(
                                QuartPos.fromBlock(centerX + dx),
                                QuartPos.fromBlock(64),
                                QuartPos.fromBlock(centerZ + dz),
                                sampler
                        )
                        .is(ModBiomes.GRAVEYARD)) {
                    return Optional.empty();
                }
            }
        }

        int minX = centerX - HALF;
        int minZ = centerZ - HALF;

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
        return ModStructures.GRAVEYARD_CEMETERY.get();
    }
}
