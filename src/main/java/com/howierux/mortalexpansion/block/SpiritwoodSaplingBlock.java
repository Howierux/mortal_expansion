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
