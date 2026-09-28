package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.core.BlockPos;
import com.howierux.mortalexpansion.entity.IntangibleEntities;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSpiritMob extends PathfinderMob
        implements HorizontalPhasingMob {
    private static final double HOVER_BASE = 0.08D;
    private static final double HOVER_AMPLITUDE = 0.035D;
    private static final double HOVER_SPEED = 0.12D;

    private double appliedHoverOffset;
    protected AbstractSpiritMob(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        setPathfindingMalus(PathType.WATER, -1.0F);
        setPathfindingMalus(PathType.WATER_BORDER, -1.0F);
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide && appliedHoverOffset != 0.0D) {
            setPos(getX(), getY() - appliedHoverOffset, getZ());
            appliedHoverOffset = 0.0D;
        }

        super.aiStep();

        if (!level().isClientSide && IntangibleEntities.isIntangible(this)) {
            appliedHoverOffset = HOVER_BASE
                    + Math.sin(tickCount * HOVER_SPEED) * HOVER_AMPLITUDE;
            setPos(getX(), getY() + appliedHoverOffset, getZ());
        }
    }

    protected static AttributeSupplier.Builder baseAttributes(double health, double speed) {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void move(MoverType moverType, Vec3 movement) {
        Vec3 safeMovement = avoidEnteringWater(movement);

        // Spirits phase only horizontally. Normal vertical collision is retained
        // so they still stand on terrain instead of falling through the world.
        super.move(moverType, new Vec3(0.0D, safeMovement.y, 0.0D));

        if (safeMovement.x != 0.0D || safeMovement.z != 0.0D) {
            setPos(
                    getX() + safeMovement.x,
                    getY(),
                    getZ() + safeMovement.z
            );
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

        return new Vec3(0.0D, Math.max(0.0D, movement.y), 0.0D);
    }

    private boolean isTouchingWater(AABB box) {
        return BlockPos.betweenClosedStream(box.deflate(1.0E-4D))
                .anyMatch(pos -> level().getFluidState(pos).is(FluidTags.WATER));
    }

    @Override
    public boolean isInWall() {
        return false;
    }
}
