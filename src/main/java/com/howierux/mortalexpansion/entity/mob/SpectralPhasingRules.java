package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/**
 * Shared rules for spectral wall tracking and horizontal phasing.
 */
public final class SpectralPhasingRules {
    public static final int MAX_TRACKING_BLOCK_LAYERS = 4;

    public static final TagKey<Block> INTANGIBLE_SOLID = TagKey.create(
            net.minecraft.core.registries.Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(
                    MortalExpansion.MOD_ID,
                    "intangible_solid"
            )
    );

    private SpectralPhasingRules() {}

    /**
     * Returns true if an intangible mob may track the target through the
     * intervening blocks. Up to four occupied block cells are allowed.
     * Spectral-solid materials always block tracking.
     */
    public static boolean canTrackThroughBlocks(Entity mob, Entity target) {
        Level level = mob.level();
        Vec3 start = mob.getEyePosition();
        Vec3 end = target.getEyePosition();
        Vec3 delta = end.subtract(start);
        double distance = delta.length();

        if (distance <= 1.0E-6D) {
            return true;
        }

        int steps = Math.max(1, (int)Math.ceil(distance * 4.0D));
        Set<BlockPos> blockingCells = new HashSet<>();

        for (int i = 1; i < steps; i++) {
            double t = (double)i / (double)steps;
            Vec3 sample = start.add(delta.scale(t));
            BlockPos pos = BlockPos.containing(sample);
            if (HolySaltBarrierTracker.hasSaltColumn(
                    level,
                    pos.getX(),
                    pos.getZ()
            ) || SpiritTorchBarrierTracker.hasBarrierColumn(level, pos.getX(), pos.getZ())) {
                return false;
            }

            BlockState state = level.getBlockState(pos);

            if (state.is(INTANGIBLE_SOLID)) {
                return false;
            }

            if (!state.getCollisionShape(level, pos).isEmpty()) {
                blockingCells.add(pos.immutable());
                if (blockingCells.size() > MAX_TRACKING_BLOCK_LAYERS) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Intangible mobs may pass ordinary horizontal collision, but never enter
     * blocks tagged as spectral-solid.
     */
    public static boolean canEnterHorizontally(Entity mob, AABB destination) {
        Level level = mob.level();
        return BlockPos.betweenClosedStream(destination.deflate(1.0E-4D))
                .noneMatch(pos ->
                        HolySaltBarrierTracker.hasSaltColumn(
                                level,
                                pos.getX(),
                                pos.getZ()
                        )
                                || SpiritTorchBarrierTracker.hasBarrierColumn(level, pos.getX(), pos.getZ())
                                || level.getBlockState(pos).is(INTANGIBLE_SOLID)
                );
    }
}
