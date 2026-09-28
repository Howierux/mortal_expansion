package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EmpoweredPhantomEntity extends AbstractMortalMonster {
    private static final double OBSERVED_SPEED = 0.28D;
    private static final double UNOBSERVED_SPEED = 0.52D;

    public EmpoweredPhantomEntity(EntityType<? extends EmpoweredPhantomEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(34.0D, OBSERVED_SPEED, 7.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!level().isClientSide) {
            AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.setBaseValue(isObserved() ? OBSERVED_SPEED : UNOBSERVED_SPEED);
            }
        }
    }

    private boolean isObserved() {
        Player player = level().getNearestPlayer(this, 32.0D);
        if (player == null || !player.hasLineOfSight(this)) {
            return false;
        }

        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 toward = position().add(0.0D, getBbHeight() * 0.5D, 0.0D)
                .subtract(player.getEyePosition())
                .normalize();

        return look.dot(toward) > 0.92D;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && level() instanceof ServerLevel server) {
            GhostEntity ghost = ModEntities.GHOST.get().create(server);
            if (ghost != null) {
                ghost.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
                server.addFreshEntity(ghost);
            }
        }
        super.die(source);
    }
}
