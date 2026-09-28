package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Direct same-level pursuit used by intangible hostile mobs.
 *
 * This deliberately bypasses ordinary path navigation so a wall between the
 * mob and its target is not treated as an obstacle. The entity's movement
 * override still preserves normal vertical collision with floors.
 */
public final class SpectralMeleeAttackGoal extends Goal {
    private static final int ATTACK_INTERVAL = 20;

    private final AbstractMortalMonster mob;
    private final double speedModifier;
    private int attackCooldown;

    public SpectralMeleeAttackGoal(AbstractMortalMonster mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return canPhase()
                && target != null
                && target.isAlive()
                && isSameLevel(target);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        attackCooldown = 0;
    }

    @Override
    public void stop() {
        mob.setZza(0.0F);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return;
        }

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double dx = target.getX() - mob.getX();
        double dz = target.getZ() - mob.getZ();
        if (dx * dx + dz * dz > 1.0E-6D) {
            float wantedYaw = (float)(Mth.atan2(dz, dx)
                    * (180.0D / Math.PI)) - 90.0F;
            mob.setYRot(Mth.rotLerp(0.35F, mob.getYRot(), wantedYaw));
            mob.yBodyRot = mob.getYRot();
            mob.setSpeed((float)(
                    speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)
            ));
            mob.setZza(1.0F);
        } else {
            mob.setZza(0.0F);
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        double reach = mob.getBbWidth() * 2.0F + target.getBbWidth();
        if (mob.distanceToSqr(target) <= reach * reach && attackCooldown <= 0) {
            attackCooldown = ATTACK_INTERVAL;
            mob.swing(mob.getUsedItemHand());
            mob.doHurtTarget(target);
        }
    }

    private boolean canPhase() {
        return mob instanceof HorizontalPhasingMob phasing
                && phasing.canPhaseHorizontally();
    }

    private boolean isSameLevel(LivingEntity target) {
        return Math.abs(target.getY() - mob.getY()) <= 1.5D;
    }
}
