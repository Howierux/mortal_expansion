package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SpiritMovement {
    private SpiritMovement() {}
    public static Vec3 prepare(PathfinderMob mob, MoverType type, Vec3 motion) {
        Vec3 safe = type == MoverType.SELF ? com.howierux.mortalexpansion.haunt.HauntDaylightRules.limitMovement(mob, motion) : motion;
        if (safe.lengthSqr() == 0 || water(mob, mob.getBoundingBox())) return safe;
        return water(mob, mob.getBoundingBox().move(safe).expandTowards(0, -0.15, 0))
                ? new Vec3(0, Math.max(0, safe.y), 0) : safe;
    }
    private static boolean water(PathfinderMob mob, AABB box) {
        return BlockPos.betweenClosedStream(box.deflate(1.0E-4))
                .anyMatch(pos -> mob.level().getFluidState(pos).is(FluidTags.WATER));
    }
    public static void horizontal(PathfinderMob mob, Vec3 safe) {
        if ((safe.x != 0 || safe.z != 0) && SpectralPhasingRules.canEnterHorizontally(mob,
                mob.getBoundingBox().move(safe.x, 0, safe.z))) {
            mob.setPos(mob.getX() + safe.x, mob.getY(), mob.getZ() + safe.z);
        }
    }
}
