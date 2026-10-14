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
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;

/** Persistent construct which hunts entities in the native Smite tag. */
public final class MortalGolemEntity extends IronGolem {
    private static final EntityDataAccessor<Integer> FLESH=SynchedEntityData.defineId(MortalGolemEntity.class,EntityDataSerializers.INT);
    public MortalGolemEntity(EntityType<? extends IronGolem> type,Level level) {super(type,level);setPlayerCreated(true);setPersistenceRequired();setHealth(getMaxHealth());}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {super.defineSynchedData(builder);builder.define(FLESH,16);}
    public int flesh(){return entityData.get(FLESH);}
    public int fleshTier(){return Math.clamp(flesh()/16,1,4);}
    public void setFlesh(int amount){
        float previousMax=getMaxHealth();
        entityData.set(FLESH,Math.clamp(amount,16,64));
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(25.0*fleshTier());
        // New tissue supplies its added health; building before or after awakening gives the same result.
        setHealth(Math.min(getMaxHealth(),getHealth()+getMaxHealth()-previousMax));
    }
    public float repairAmount(){return getMaxHealth()/16.0F;}
    public void repairFromHaunt(){
        if(level() instanceof ServerLevel server && isAlive() && getHealth()<getMaxHealth()
                && HauntManager.getTier(server,blockPosition()).id()>=HauntTier.DEAD_RISING.id()) {
            heal(getMaxHealth()/100.0F);
        }
    }
    @Override public void aiStep(){
        super.aiStep();
        if(!level().isClientSide && tickCount%100==0)repairFromHaunt();
    }
    public static AttributeSupplier.Builder createAttributes(){return IronGolem.createAttributes().add(Attributes.MAX_HEALTH,25);}
    @Override protected void registerGoals() {
        goalSelector.addGoal(0,new FloatGoal(this));
        goalSelector.addGoal(1,new MeleeAttackGoal(this,1.0,true));
        goalSelector.addGoal(3,new WaterAvoidingRandomStrollGoal(this,0.6));
        targetSelector.addGoal(1,new NearestAttackableTargetGoal<>(this,LivingEntity.class,10,true,false,
                target -> target.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)));
        goalSelector.addGoal(4,new LookAtPlayerGoal(this,Player.class,8));
        goalSelector.addGoal(5,new RandomLookAroundGoal(this));
    }
    @Override public boolean canAttackType(EntityType<?> type) {
        return type.is(EntityTypeTags.SENSITIVE_TO_SMITE) && super.canAttackType(type);
    }
    @Override public void setTarget(LivingEntity target){
        super.setTarget(target != null && canAttackType(target.getType()) ? target : null);
    }
    @Override public boolean doHurtTarget(Entity target){
        return target instanceof LivingEntity && canAttackType(target.getType()) && super.doHurtTarget(target);
    }
    @Override public InteractionResult mobInteract(Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(!stack.is(net.minecraft.world.item.Items.ROTTEN_FLESH) || !isAlive()
                || (flesh()==64 && getHealth()>=getMaxHealth()))return InteractionResult.PASS;
        if(!level().isClientSide){
            if(flesh()<64)setFlesh(flesh()+1);
            heal(repairAmount());
            stack.consume(1,player);
            playSound(net.minecraft.sounds.SoundEvents.IRON_GOLEM_REPAIR,1.0F,1.0F);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("Flesh",flesh());}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);float savedHealth=getHealth();setFlesh(tag.contains("Flesh")?tag.getInt("Flesh"):16);setHealth(Math.min(savedHealth,getMaxHealth()));setPlayerCreated(true);setPersistenceRequired();}
}
