package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.MultifaceBlock;
import java.util.ArrayList;
import java.util.List;

/** Sapling-only decoration on newly grown leaves, without altering natural tree generation. */
public final class SpiritwoodSaplingMoss {
    // Bounds cover the current 4-6-block trunk and radius-two blob foliage configuration.
    public static final int RADIUS = 4;
    public static final int HEIGHT = 10;
    private SpiritwoodSaplingMoss() {}
    private record Face(BlockPos pos, Direction support) {}
    public static void decorate(ServerLevel level, Iterable<BlockPos> newLeaves, RandomSource random) {
        List<Face> faces = new ArrayList<>();
        for (var leaf : newLeaves) {
            if (!level.getBlockState(leaf).is(CatalogBlocks.SPIRITWOOD_LEAVES)) continue;
            for (var outward : Direction.values()) {
                var at = leaf.relative(outward);
                var moss = CatalogBlocks.GRAVE_MOSS.defaultBlockState()
                        .setValue(MultifaceBlock.getFaceProperty(outward.getOpposite()), true);
                if (level.isEmptyBlock(at) && moss.canSurvive(level, at)) faces.add(new Face(at, outward.getOpposite()));
            }
        }
        boolean placed = false;
        for (var face : faces) if (random.nextFloat() < .25F) placed |= place(level, face);
        // Every successfully grown tree with an exposed suitable face gets at least one patch.
        if (!placed && !faces.isEmpty()) place(level, faces.get(random.nextInt(faces.size())));
    }
    private static boolean place(ServerLevel level, Face face) {
        var current = level.getBlockState(face.pos());
        if (!current.isAir() && !current.is(CatalogBlocks.GRAVE_MOSS)) return false;
        var moss = (current.isAir() ? CatalogBlocks.GRAVE_MOSS.defaultBlockState() : current)
                .setValue(MultifaceBlock.getFaceProperty(face.support()), true);
        if (!moss.canSurvive(level, face.pos())) return false;
        return level.setBlock(face.pos(), moss, 3);
    }
}
