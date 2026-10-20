package com.howierux.mortalexpansion.entity.mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.*;
import net.minecraft.nbt.CompoundTag;
/** Stronger spectral hunter; color is synchronized and survives saving. */
public final class DemonEntity extends AbstractMortalMonster implements HorizontalPhasingMob {
    private static final EntityDataAccessor<Integer> VARIANT=SynchedEntityData.defineId(DemonEntity.class,EntityDataSerializers.INT);
    private static final int[] COLORS={0xFFFF7777,0xFFBD80FF,0xFF77AFFF,0xFFFFB566};
    public DemonEntity(EntityType<? extends DemonEntity> type,Level level){super(type,level);entityData.set(VARIANT,random.nextInt(COLORS.length));}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(VARIANT,0);}
    public int variant(){return entityData.get(VARIANT);}
    public int tint(){return COLORS[variant()];}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("DemonVariant",variant());}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(VARIANT,Math.clamp(tag.getInt("DemonVariant"),0,COLORS.length-1));}
    public static AttributeSupplier.Builder createAttributes(){return baseAttributes(40,0.30,8);}
}
