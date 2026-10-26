package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.entity.DynamicIntangible;
import com.howierux.mortalexpansion.entity.CombatEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public final class PharaohlEntity extends AbstractMortalMonster implements DynamicIntangible, HorizontalPhasingMob {
    private static final int PHASE_TICKS = 160;
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> PHASE=net.minecraft.network.syncher.SynchedEntityData.defineId(PharaohlEntity.class,net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    @Override protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder b){super.defineSynchedData(b);b.define(PHASE,false);}
    private static final int SUMMON_INTERVAL = 240;
    private static final String PYRAMID_KEY_TAG = "MortalExpansionPyramidKey";

    @Nullable
    private String pyramidKey;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.WHITE,
            BossEvent.BossBarOverlay.NOTCHED_20
    );

    public PharaohlEntity(EntityType<? extends PharaohlEntity> type, Level level) {
        super(type, level);
        xpReward = 50;
    }

    public void setPyramidKey(String pyramidKey) {
        this.pyramidKey = pyramidKey;
    }

    @Nullable
    public String getPyramidKey() {
        return pyramidKey;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);tag.putInt("PhaseTime",Math.floorMod(tickCount,320));
        if (pyramidKey != null && !pyramidKey.isEmpty()) {
            tag.putString(PYRAMID_KEY_TAG, pyramidKey);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);tickCount=Math.floorMod(tag.getInt("PhaseTime"),320);
        pyramidKey = tag.contains(PYRAMID_KEY_TAG)
                ? tag.getString(PYRAMID_KEY_TAG)
                : null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes(220.0D, 0.26D, 13.0D);
    }

    @Override
    public boolean isCurrentlyIntangible() {
        return level().isClientSide?entityData.get(PHASE):(tickCount / PHASE_TICKS) % 2 == 1;
    }

    @Override
    public boolean canPhaseHorizontally() {
        return isCurrentlyIntangible();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public void aiStep() {
        if(!level().isClientSide)entityData.set(PHASE,isCurrentlyIntangible());
        super.aiStep();

        if (!level().isClientSide
                && tickCount % SUMMON_INTERVAL == 0
                && getTarget() != null
                && level() instanceof ServerLevel server) {
            for (int i = 0; i < 2; i++) {
                MummyEntity mummy = CombatEntities.MUMMY.create(server);
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
