package com.howierux.mortalexpansion.entity.mob;

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
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSpiritMob extends PathfinderMob
        implements HorizontalPhasingMob {
    protected AbstractSpiritMob(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
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
        // Spirits phase only horizontally. Normal vertical collision is retained
        // so they still stand on terrain instead of falling through the world.
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
        return false;
    }
}
