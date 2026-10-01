package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractMortalMonster extends Monster {
    @Nullable
    private UUID alliedOwner;
    protected AbstractMortalMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        moveControl = new SpectralMoveControl(this);
        setPathfindingMalus(PathType.WATER, -1.0F);
        setPathfindingMalus(PathType.WATER_BORDER, -1.0F);
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
                        target -> !isAllied()
                                && this instanceof HorizontalPhasingMob phasing
                                && phasing.canPhaseHorizontally()
                                && SpectralPhasingRules.canTrackThroughBlocks(this, target)
                )
        );
        targetSelector.addGoal(
                3,
                new NearestAttackableTargetGoal<>(
                        this,
                        Player.class,
                        10,
                        true,
                        false,
                        target -> !isAllied()
                )
        );
        targetSelector.addGoal(
                4,
                new NearestAttackableTargetGoal<>(
                        this,
                        Monster.class,
                        10,
                        true,
                        false,
                        target -> isAllied()
                                && target != this
                                && (!(target instanceof AbstractMortalMonster mortal)
                                || !mortal.isAllied())
                )
        );
    }

    @Override
    public void move(MoverType moverType, Vec3 movement) {
        Vec3 safeMovement = avoidEnteringWater(movement);

        if (!canPhaseHorizontally()) {
            super.move(moverType, safeMovement);
            return;
        }

        // Keep Minecraft's ordinary Y-axis collision so spectral mobs stand on
        // floors, but apply X/Z motion directly so walls do not block them.
        super.move(moverType, new Vec3(0.0D, safeMovement.y, 0.0D));

        if (safeMovement.x != 0.0D || safeMovement.z != 0.0D) {
            AABB destination = getBoundingBox().move(
                    safeMovement.x,
                    0.0D,
                    safeMovement.z
            );

            if (SpectralPhasingRules.canEnterHorizontally(this, destination)) {
                setPos(
                        getX() + safeMovement.x,
                        getY(),
                        getZ() + safeMovement.z
                );
            }
        }
    }

    private Vec3 avoidEnteringWater(Vec3 movement) {
        if (movement.lengthSqr() == 0.0D || isTouchingWater(getBoundingBox())) {
            return movement;
        }

        AABB destination = getBoundingBox()
                .move(movement.x, movement.y, movement.z)
                .expandTowards(0.0D, -0.15D, 0.0D);

        if (!isTouchingWater(destination)) {
            return movement;
        }

        // Reject horizontal/downward motion that would carry the mob onto or
        // into water. Upward movement remains available for escaping ledges.
        return new Vec3(0.0D, Math.max(0.0D, movement.y), 0.0D);
    }

    private boolean isTouchingWater(AABB box) {
        return BlockPos.betweenClosedStream(box.deflate(1.0E-4D))
                .anyMatch(pos -> level().getFluidState(pos).is(FluidTags.WATER));
    }

    public void setAlliedOwner(UUID owner) {
        this.alliedOwner = owner;
        setTarget(null);
    }

    public boolean isAllied() {
        return alliedOwner != null;
    }

    @Nullable
    public UUID getAlliedOwner() {
        return alliedOwner;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (isAllied()) {
            if (target instanceof Player) {
                super.setTarget(null);
                return;
            }

            if (target instanceof AbstractMortalMonster mortal && mortal.isAllied()) {
                super.setTarget(null);
                return;
            }
        }

        super.setTarget(target);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (alliedOwner != null) {
            tag.putUUID("MortalExpansionAlliedOwner", alliedOwner);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        alliedOwner = tag.hasUUID("MortalExpansionAlliedOwner")
                ? tag.getUUID("MortalExpansionAlliedOwner")
                : null;
    }

    @Override
    public boolean isInWall() {
        return canPhaseHorizontally() ? false : super.isInWall();
    }

    public boolean canPhaseHorizontally() {
        return this instanceof HorizontalPhasingMob;
    }
}
