package com.howierux.mortalexpansion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public final class PurifiedEctoplasmLiquidBlock extends LiquidBlock {
    public PurifiedEctoplasmLiquidBlock(
            FlowingFluid fluid,
            Properties properties
    ) {
        super(fluid, properties);
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        super.animateTick(state, level, pos, random);

        // Small arcs contained inside the liquid volume. This is purely visual
        // lightning and does not summon LightningBolt entities or deal damage.
        if (random.nextInt(4) == 0) {
            int sparks = 1 + random.nextInt(3);
            for (int i = 0; i < sparks; i++) {
                level.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        pos.getX() + 0.08D + random.nextDouble() * 0.84D,
                        pos.getY() + 0.08D + random.nextDouble() * 0.84D,
                        pos.getZ() + 0.08D + random.nextDouble() * 0.84D,
                        (random.nextDouble() - 0.5D) * 0.04D,
                        (random.nextDouble() - 0.5D) * 0.03D,
                        (random.nextDouble() - 0.5D) * 0.04D
                );
            }
        }
    }
}
