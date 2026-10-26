package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * Steers spectral movement without vanilla's jump-out-of-an-obstructing-block
 * behavior. Higher destinations request a controlled upward rise.
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

        if(dx*dx+dz*dz < 2.5000003E-7D)mob.setZza(0.0F);

        // Rise deliberately toward higher destinations rather than repeatedly jumping into a ceiling.
        // Only upward velocity is added: gravity and normal floor collision handle descent.
        if (dy > 0.1D) {
            var velocity=mob.getDeltaMovement();
            mob.setDeltaMovement(velocity.x,Math.min(0.25D,dy),velocity.z);
            mob.setOnGround(false);
        }
    }
}
