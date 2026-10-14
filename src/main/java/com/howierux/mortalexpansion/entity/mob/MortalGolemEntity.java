package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** A persistent, passive construct. Flesh affects appearance only for now. */
public final class MortalGolemEntity extends IronGolem {
    private static final EntityDataAccessor<Integer> FLESH=SynchedEntityData.defineId(MortalGolemEntity.class,EntityDataSerializers.INT);
    public MortalGolemEntity(EntityType<? extends IronGolem> type,Level level) {super(type,level);setPlayerCreated(true);setPersistenceRequired();}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {super.defineSynchedData(builder);builder.define(FLESH,16);}
    public int flesh(){return entityData.get(FLESH);}
    public int fleshTier(){return Math.clamp(flesh()/16,1,4);}
    public void setFlesh(int amount){entityData.set(FLESH,Math.clamp(amount,16,64));}
    public static AttributeSupplier.Builder createAttributes(){return IronGolem.createAttributes().add(Attributes.ATTACK_DAMAGE,0);}
    @Override protected void registerGoals() {
        goalSelector.addGoal(0,new FloatGoal(this));
        goalSelector.addGoal(3,new WaterAvoidingRandomStrollGoal(this,0.6));
        goalSelector.addGoal(4,new LookAtPlayerGoal(this,Player.class,8));
        goalSelector.addGoal(5,new RandomLookAroundGoal(this));
    }
    @Override public void setTarget(LivingEntity target){super.setTarget(null);}
    @Override public boolean doHurtTarget(Entity target){return false;}
    @Override protected InteractionResult mobInteract(Player player,InteractionHand hand){return InteractionResult.PASS;}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("Flesh",flesh());}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);setFlesh(tag.contains("Flesh")?tag.getInt("Flesh"):16);setPlayerCreated(true);setPersistenceRequired();}
}
