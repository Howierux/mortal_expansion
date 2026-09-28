package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMortalMonster extends Monster {
    protected AbstractMortalMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    protected static AttributeSupplier.Builder baseAttributes(double health, double speed, double damage) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, damage)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new SpectralMeleeAttackGoal(this, 1.0D));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(
                2,
                new NearestAttackableTargetGoal<>(
                        this,
                        Player.class,
                        10,
                        false,
                        false,
                        target -> this instanceof HorizontalPhasingMob phasing
                                && phasing.canPhaseHorizontally()
                )
        );
        targetSelector.addGoal(
                3,
                new NearestAttackableTargetGoal<>(this, Player.class, true)
        );
    }

    @Override
    public void move(MoverType moverType, Vec3 movement) {
        if (!canPhaseHorizontally()) {
            super.move(moverType, movement);
            return;
        }

        // Keep Minecraft's ordinary Y-axis collision so spectral mobs stand on
        // floors, but apply X/Z motion directly so walls do not block them.
        super.move(moverType, new Vec3(0.0D, movement.y, 0.0D));

        if (movement.x != 0.0D || movement.z != 0.0D) {
            setPos(
                    getX() + movement.x,
                    getY(),
                    getZ() + movement.z
            );
        }
    }

    @Override
    public boolean isInWall() {
        return canPhaseHorizontally() ? false : super.isInWall();
    }

    protected boolean canPhaseHorizontally() {
        return this instanceof HorizontalPhasingMob phasing
                && phasing.canPhaseHorizontally();
    }
}
