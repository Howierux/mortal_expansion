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
     * intervening blocks. Demons use a bounded long-range ray; other spectral
     * mobs allow up to four occupied cells. Spectral-solid materials block both.
     */
    public static boolean canTrackThroughBlocks(Entity mob, Entity target) {
        if(mob instanceof DemonEntity demon)return canDemonTrack(demon,target);
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

            if (isSpectralSolid(level,pos,state)) {
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

    /** One native voxel traversal; ordinary wall thickness is irrelevant, unloaded terrain is opaque. */
    private static boolean canDemonTrack(DemonEntity demon,Entity target){
        Level level=demon.level();if(target.level()!=level)return false;
        Vec3 start=demon.getEyePosition(),end=target.getEyePosition();
        double range=demon.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE);
        if(start.distanceToSqr(end)>range*range)return false;
        return net.minecraft.world.level.BlockGetter.traverseBlocks(start,end,level,(world,pos)->{
            if(!world.hasChunkAt(pos))return Boolean.FALSE;
            if(HolySaltBarrierTracker.hasSaltColumn(world,pos.getX(),pos.getZ())
                    || SpiritTorchBarrierTracker.hasBarrierColumn(world,pos.getX(),pos.getZ())
                    || isSpectralSolid(world,pos,world.getBlockState(pos)))return Boolean.FALSE;
            return null;
        },world->Boolean.TRUE);
    }

    /**
     * Intangible mobs may pass ordinary horizontal collision, but never enter
     * blocks tagged as spectral-solid. Also used to validate upward swept volumes.
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
                                || isSpectralSolid(level,pos,level.getBlockState(pos))
                );
    }
    private static boolean isSpectralSolid(net.minecraft.world.level.BlockGetter level,BlockPos pos,BlockState state) {
        if (state.is(INTANGIBLE_SOLID)) return true;
        return state.getBlock() instanceof com.howierux.mortalexpansion.block.FancifiedBlock
                && level.getBlockEntity(pos) instanceof com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity be
                && be.original().is(INTANGIBLE_SOLID);
    }

}
