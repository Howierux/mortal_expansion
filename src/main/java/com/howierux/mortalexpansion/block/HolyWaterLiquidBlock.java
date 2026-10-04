package com.howierux.mortalexpansion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public final class HolyWaterLiquidBlock extends LiquidBlock {
    public HolyWaterLiquidBlock(
            FlowingFluid fluid,
            Properties properties
    ) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof net.minecraft.server.level.ServerPlayer player)
            com.howierux.mortalexpansion.haunt.effects.HauntProtection.bless(player);
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        super.animateTick(state, level, pos, random);

        if (random.nextInt(12) == 0) {
            level.addParticle(
                    ParticleTypes.END_ROD,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 0.85D + random.nextDouble() * 0.15D,
                    pos.getZ() + random.nextDouble(),
                    0.0D,
                    0.01D,
                    0.0D
            );
        }
    }
}
