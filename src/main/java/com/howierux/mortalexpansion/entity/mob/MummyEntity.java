package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class MummyEntity extends AbstractMortalMonster {
    public MummyEntity(EntityType<? extends MummyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(40.0D, 0.21D, 7.0D);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && level() instanceof ServerLevel server) {
            GhostEntity ghost = ModEntities.GHOST.get().create(server);
            if (ghost != null) {
                ghost.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
                if (isAllied() && getAlliedOwner() != null) {
                    ghost.setAlliedOwner(getAlliedOwner());
                }
                server.addFreshEntity(ghost);
            }
        }
        super.die(source);
    }
}
