package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public final class HaintEntity extends AbstractMortalMonster {
    public HaintEntity(EntityType<? extends HaintEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(120.0D, 0.32D, 12.0D);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && level() instanceof ServerLevel server) {
            for (int i = 0; i < 3; i++) {
                HauntEntity haunt = ModEntities.HAUNT.get().create(server);
                if (haunt != null) {
                    double angle = (Math.PI * 2.0D * i) / 3.0D;
                    haunt.moveTo(
                            getX() + Math.cos(angle),
                            getY(),
                            getZ() + Math.sin(angle),
                            getYRot(),
                            getXRot()
                    );
                    server.addFreshEntity(haunt);
                }
            }
        }
        super.die(source);
    }
}
