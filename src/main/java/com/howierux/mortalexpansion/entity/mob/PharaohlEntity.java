package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.entity.DynamicIntangible;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class PharaohlEntity extends AbstractMortalMonster implements DynamicIntangible, HorizontalPhasingMob {
    private static final int PHASE_TICKS = 160;
    private static final int SUMMON_INTERVAL = 240;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.WHITE,
            BossEvent.BossBarOverlay.NOTCHED_20
    );

    public PharaohlEntity(EntityType<? extends PharaohlEntity> type, Level level) {
        super(type, level);
        xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(220.0D, 0.26D, 13.0D);
    }

    @Override
    public boolean isCurrentlyIntangible() {
        return (tickCount / PHASE_TICKS) % 2 == 1;
    }

    @Override
    public boolean canPhaseHorizontally() {
        return isCurrentlyIntangible();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!level().isClientSide
                && tickCount % SUMMON_INTERVAL == 0
                && getTarget() != null
                && level() instanceof ServerLevel server) {
            for (int i = 0; i < 2; i++) {
                MummyEntity mummy = ModEntities.MUMMY.get().create(server);
                if (mummy != null) {
                    mummy.moveTo(
                            getX() + (i == 0 ? 2.0D : -2.0D),
                            getY(),
                            getZ(),
                            getYRot(),
                            getXRot()
                    );
                    server.addFreshEntity(mummy);
                }
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
        bossEvent.setName(getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }
}
