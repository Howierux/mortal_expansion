package com.howierux.mortalexpansion.entity.mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.*;
import net.minecraft.nbt.CompoundTag;
/** Stronger spectral hunter; color is synchronized and survives saving. */
public final class DemonEntity extends AbstractMortalMonster implements HorizontalPhasingMob {
    public static final double DETECTION_RANGE=96.0D;
    @Override protected void registerPlayerTargetGoals(){targetSelector.addGoal(2,new DemonPlayerTargetGoal(this));}
    private static final EntityDataAccessor<Integer> VARIANT=SynchedEntityData.defineId(DemonEntity.class,EntityDataSerializers.INT);
    public DemonEntity(EntityType<? extends DemonEntity> type,Level level){super(type,level);entityData.set(VARIANT,random.nextInt(SinAffinity.values().length));}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(VARIANT,0);}
    public SinAffinity sinAffinity(){return SinAffinity.fromIndex(entityData.get(VARIANT));}
    public void setSinAffinity(SinAffinity sin){entityData.set(VARIANT,sin.index());}
    public int variant(){return sinAffinity().index();}
    public int tint(){return sinAffinity().color();}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putString("SinAffinity",sinAffinity().getSerializedName());tag.putInt("DemonPerceptionVersion",1);}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);setSinAffinity(tag.contains("SinAffinity",net.minecraft.nbt.Tag.TAG_STRING)?SinAffinity.fromId(tag.getString("SinAffinity")):SinAffinity.fromLegacyVariant(tag.getInt("DemonVariant")));
        var range=getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE);
        if(tag.getInt("DemonPerceptionVersion")==0 && range!=null && range.getBaseValue()==32.0D)range.setBaseValue(DETECTION_RANGE);
    }
    public static AttributeSupplier.Builder createAttributes(){return baseAttributes(40,0.30,8).add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE,DETECTION_RANGE);}
}
