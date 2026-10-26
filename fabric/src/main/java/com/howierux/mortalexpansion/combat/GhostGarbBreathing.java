package com.howierux.mortalexpansion.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;

/** Head collision test shared by the native air tick and client overlay. */
public final class GhostGarbBreathing {
    private GhostGarbBreathing() {}
    public static boolean headInsideWall(LivingEntity entity) {
        if (!GhostGarbPhasing.active(entity)) return false;
        double width = entity.getBbWidth() * 0.8;
        AABB head = AABB.ofSize(entity.getEyePosition(), width, 1.0E-6, width);
        var headShape = Shapes.create(head);
        var context = CollisionContext.of(entity);
        return BlockPos.betweenClosedStream(head).anyMatch(pos -> {
            var state = entity.level().getBlockState(pos);
            return !state.isAir() && Shapes.joinIsNotEmpty(state.getCollisionShape(entity.level(), pos, context)
                    .move(pos.getX(), pos.getY(), pos.getZ()), headShape, BooleanOp.AND);
        });
    }
    public static boolean garbDrowning(LivingEntity target, DamageSource source) {
        // Both ordinary underwater drowning and wall-air exhaustion retain vanilla damage.
        return source.is(DamageTypes.DROWN) && GhostGarbPhasing.active(target);
    }
}
