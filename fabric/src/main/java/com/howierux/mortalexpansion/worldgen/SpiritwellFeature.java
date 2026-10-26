package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.catalog.CatalogBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** One intangible anchor two blocks above exposed dry land; never clears terrain. */
public final class SpiritwellFeature extends Feature<NoneFeatureConfiguration> {
    public SpiritwellFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        if (!level.getLevel().dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) return false;
        BlockPos origin = context.origin();
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        BlockPos surface = new BlockPos(origin.getX(), surfaceY, origin.getZ());
        BlockState ground = level.getBlockState(surface.below());
        if (!ground.isSolidRender(level, surface.below()) || !ground.getFluidState().isEmpty()
                || ground.is(BlockTags.LOGS) || ground.is(BlockTags.LEAVES)) return false;
        BlockPos anchor = surface.above(2);
        int radius = 2;
        if (anchor.getY() + radius >= level.getMaxBuildHeight()) return false;
        for (BlockPos pos : BlockPos.betweenClosed(anchor.offset(-radius, -radius, -radius),
                anchor.offset(radius, radius, radius))) {
            if (pos.distSqr(anchor) <= radius * radius && !level.isEmptyBlock(pos)) return false;
        }
        return level.setBlock(anchor, CatalogBlocks.SPIRITWELL.defaultBlockState(), 2);
    }
}
