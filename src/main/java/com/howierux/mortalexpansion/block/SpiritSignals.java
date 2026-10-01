package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public final class SpiritSignals {
    private SpiritSignals() {}
    public static final DustParticleOptions DUST = new DustParticleOptions(new Vector3f(0.9F, 0.9F, 0.9F), 1.0F);
    public static boolean isTorch(BlockState state) {
        return state.is(ModBlocks.SPIRIT_TORCH.get()) || state.is(ModBlocks.SPIRIT_WALL_TORCH.get());
    }
    private static int torchSignal(BlockState state, Direction towardReceiver) {
        if (!isTorch(state) || !state.getValue(RedstoneTorchBlock.LIT)) return 0;
        if (state.is(ModBlocks.SPIRIT_WALL_TORCH.get())) {
            return towardReceiver == state.getValue(WallTorchBlock.FACING).getOpposite() ? 0 : 15;
        }
        return towardReceiver == Direction.DOWN ? 0 : 15;
    }
    public static int input(Level level, BlockPos receiver) {
        int power = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = receiver.relative(direction);
            if (!level.hasChunkAt(neighbor)) continue;
            BlockState state = level.getBlockState(neighbor);
            power = Math.max(power, torchSignal(state, direction.getOpposite()));
            // Torches strongly power only the solid block above them.
            if (state.isRedstoneConductor(level, neighbor)) {
                BlockState below = level.getBlockState(neighbor.below());
                power = Math.max(power, torchSignal(below, Direction.UP));
            }
        }
        return power;
    }
    public static boolean saltPowersSupport(Level level, BlockPos support) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = support.relative(direction);
            if (!level.hasChunkAt(neighbor)) continue;
            BlockState state = level.getBlockState(neighbor);
            if (state.is(ModBlocks.HOLY_SALT.get()) && state.getValue(HolySaltWireBlock.POWER) > 0
                    && direction != Direction.DOWN) return true;
        }
        return false;
    }
    public static void particles(BlockState state, Level level, BlockPos pos,
            net.minecraft.util.RandomSource random) {
        if (!state.getValue(RedstoneTorchBlock.LIT)) return;
        double x = pos.getX() + 0.5, y = pos.getY() + 0.7, z = pos.getZ() + 0.5;
        if (state.hasProperty(WallTorchBlock.FACING)) {
            Direction back = state.getValue(WallTorchBlock.FACING).getOpposite();
            x += 0.27 * back.getStepX(); z += 0.27 * back.getStepZ(); y += 0.22;
        }
        level.addParticle(DUST, x + (random.nextDouble() - 0.5) * 0.2,
                y + (random.nextDouble() - 0.5) * 0.2, z + (random.nextDouble() - 0.5) * 0.2, 0, 0, 0);
    }
}
