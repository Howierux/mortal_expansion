package com.howierux.mortalexpansion.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

/** Uses NeoForge's native air tick, including oxygen bonus/Respiration and the existing HUD. */
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
    @SubscribeEvent public static void onBreathe(LivingBreatheEvent event) {
        if (!(event.getEntity() instanceof Player player) || !headInsideWall(player)) return;
        // Treat an immersed head just like underwater; preserve the native Respiration consumption.
        event.setCanBreathe(player.getAbilities().invulnerable || MobEffectUtil.hasWaterBreathing(player));
        event.setRefillAirAmount(0);
    }
    public static boolean wallDrowning(LivingEntity target, DamageSource source) {
        return source.is(DamageTypes.DROWN) && headInsideWall(target);
    }
}
