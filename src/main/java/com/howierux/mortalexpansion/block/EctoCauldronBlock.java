package com.howierux.mortalexpansion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Full cauldron state for Mortal Expansion fluids.
 * Interaction is handled by NaturalPurificationHandler.
 */
public final class EctoCauldronBlock extends Block {
    private final boolean sparkles;

    public EctoCauldronBlock(Properties properties, boolean sparkles) {
        super(properties);
        this.sparkles = sparkles;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (sparkles && random.nextInt(10) == 0) {
            level.addParticle(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.25D + random.nextDouble() * 0.5D,
                    pos.getY() + 0.75D,
                    pos.getZ() + 0.25D + random.nextDouble() * 0.5D,
                    0.0D,
                    0.01D,
                    0.0D
            );
        }
    }
}
