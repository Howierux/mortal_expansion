package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * Steers horizontal phasing without vanilla's jump-out-of-an-obstructing-block
 * behavior. A genuinely higher destination may still request a terrain jump.
 * Tangible phases retain the ordinary movement controller.
 */
public final class SpectralMoveControl extends MoveControl {
    public SpectralMoveControl(Mob mob) {
        super(mob);
    }

    @Override
    public void tick() {
        if (!(mob instanceof HorizontalPhasingMob phasing)
                || !phasing.canPhaseHorizontally()
                || operation != Operation.MOVE_TO) {
            super.tick();
            return;
        }

        operation = Operation.WAIT;
        double dx = wantedX - mob.getX();
        double dy = wantedY - mob.getY();
        double dz = wantedZ - mob.getZ();
        if (dx * dx + dy * dy + dz * dz < 2.5000003E-7D) {
            mob.setZza(0.0F);
            return;
        }

        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        mob.setYRot(rotlerp(mob.getYRot(), yaw, 90.0F));
        mob.setSpeed((float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

        // Do not inspect the collision shape at the mob's feet here: occupying
        // an ordinary wall is expected while phasing, not a reason to jump.
        if (dy > mob.maxUpStep()
                && dx * dx + dz * dz < Math.max(1.0F, mob.getBbWidth())) {
            mob.getJumpControl().jump();
            operation = Operation.JUMPING;
        }
    }
}
