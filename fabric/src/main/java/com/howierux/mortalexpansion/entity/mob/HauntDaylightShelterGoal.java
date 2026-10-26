package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.haunt.HauntDaylightRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/** Shelter beats roaming/combat, but swimming and emergency goals keep priority. */
public final class HauntDaylightShelterGoal extends Goal {
    private final PathfinderMob mob;
    private Path shelterPath;
    private BlockPos destination;
    private long nextSearch;
    private int remainingTicks;
    private record Candidate(BlockPos pos, boolean haunted, double distance) {}

    public HauntDaylightShelterGoal(PathfinderMob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(mob.level() instanceof ServerLevel level) || mob.isNoAi()
                || !HauntDaylightRules.shelterTime(level)
                || HauntDaylightRules.sustains(level, mob.blockPosition(), mob)
                || !level.canSeeSky(mob.blockPosition()) || level.getGameTime() < nextSearch)
            return false;
        nextSearch = level.getGameTime() + 80;
        return findShelter(level);
    }

    private boolean findShelter(ServerLevel level) {
        List<Candidate> candidates = new ArrayList<>();
        BlockPos origin = mob.blockPosition();
        for (int dx = -32; dx <= 32; dx += 4) {
            for (int dz = -32; dz <= 32; dz += 4) {
                int x = origin.getX() + dx, z = origin.getZ() + dz;
                var chunk = level.getChunkSource().getChunkNow(x >> 4, z >> 4);
                if (chunk == null) continue;
                // Check the mob's elevation too, so sheltered tunnels can be used.
                addCandidate(level, new BlockPos(x, origin.getY(), z), candidates);
                int y = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x & 15, z & 15) + 1;
                if (y != origin.getY()) addCandidate(level, new BlockPos(x, y, z), candidates);
            }
        }
        candidates.sort(Comparator.comparing((Candidate c) -> !c.haunted())
                .thenComparingDouble(Candidate::distance));
        // Bound expensive path searches. A shade fallback gets its own search allowance.
        int hauntedAttempts = 0, shadeAttempts = 0;
        for (Candidate candidate : candidates) {
            if (candidate.haunted()) {
                if (hauntedAttempts++ >= 12) continue;
            } else if (shadeAttempts++ >= 12) continue;
            Path path = mob.getNavigation().createPath(candidate.pos(), 0);
            if (path != null && path.canReach()) {
                shelterPath = path;
                destination = candidate.pos();
                return true;
            }
        }
        return false;
    }

    private void addCandidate(ServerLevel level, BlockPos pos, List<Candidate> candidates) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight()) return;
        boolean haunted = HauntDaylightRules.sustains(level, pos, mob);
        if (!haunted && level.canSeeSky(pos)) return;
        if (!level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below())
                || !level.getFluidState(pos).isEmpty()
                || !level.noCollision(mob, mob.getBoundingBox().move(
                        pos.getX() + 0.5 - mob.getX(), pos.getY() - mob.getY(),
                        pos.getZ() + 0.5 - mob.getZ()))) return;
        candidates.add(new Candidate(pos.immutable(), haunted, mob.distanceToSqr(
                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));
    }

    @Override
    public void start() {
        remainingTicks = 200;
        mob.getNavigation().moveTo(shelterPath, 1.15);
    }

    @Override
    public boolean canContinueToUse() {
        if (!(mob.level() instanceof ServerLevel level) || destination == null
                || !HauntDaylightRules.shelterTime(level) || remainingTicks <= 0
                || mob.getNavigation().isDone()) return false;
        if (level.getChunkSource().getChunkNow(destination.getX() >> 4, destination.getZ() >> 4) == null)
            return false;
        return level.canSeeSky(mob.blockPosition())
                && !HauntDaylightRules.sustains(level, mob.blockPosition(), mob)
                && (HauntDaylightRules.sustains(level, destination, mob) || !level.canSeeSky(destination));
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() { remainingTicks--; }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        shelterPath = null;
        destination = null;
    }
}
