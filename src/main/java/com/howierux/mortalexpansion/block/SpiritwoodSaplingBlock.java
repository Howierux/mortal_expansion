package com.howierux.mortalexpansion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

/**
 * Spiritwood sapling with a subtle but clearly visible green shimmer while planted.
 */
public class SpiritwoodSaplingBlock extends SaplingBlock {
    private static final DustParticleOptions SPIRIT_SHIMMER =
            new DustParticleOptions(new Vector3f(0.32F, 0.92F, 0.42F), 0.60F);

    public SpiritwoodSaplingBlock(TreeGrower treeGrower, BlockBehaviour.Properties properties) {
        super(treeGrower, properties);
    }

    @Override
    public void advanceTree(net.minecraft.server.level.ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            super.advanceTree(level, pos, state, random);
            return;
        }
        var oldLeaves = new java.util.HashSet<BlockPos>();
        int radius = com.howierux.mortalexpansion.worldgen.SpiritwoodSaplingMoss.RADIUS;
        int height = com.howierux.mortalexpansion.worldgen.SpiritwoodSaplingMoss.HEIGHT;
        for (var at : BlockPos.betweenClosed(pos.offset(-radius, 0, -radius), pos.offset(radius, height, radius)))
            if (level.getBlockState(at).is(com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LEAVES.get())) oldLeaves.add(at.immutable());
        super.advanceTree(level, pos, state, random);
        // Failed growth or stage advancement must not decorate neighboring trees.
        if (!level.getBlockState(pos).is(com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LOG.get())) return;
        var newLeaves = new java.util.ArrayList<BlockPos>();
        for (var at : BlockPos.betweenClosed(pos.offset(-radius, 0, -radius), pos.offset(radius, height, radius)))
            if (!oldLeaves.contains(at) && level.getBlockState(at).is(com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LEAVES.get())) newLeaves.add(at.immutable());
        com.howierux.mortalexpansion.worldgen.SpiritwoodSaplingMoss.decorate(level, newLeaves, random);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextInt(8) == 0) {
            double x = pos.getX() + 0.25D + random.nextDouble() * 0.5D;
            double y = pos.getY() + 0.15D + random.nextDouble() * 0.65D;
            double z = pos.getZ() + 0.25D + random.nextDouble() * 0.5D;

            level.addParticle(SPIRIT_SHIMMER, x, y, z, 0.0D, 0.002D, 0.0D);
        }
    }
}
