package com.howierux.mortalexpansion.entity;

import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.entity.CombatEntities;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** Native projectile physics/networking with owner-attributed magic damage. */
public final class SpectralBoltEntity extends ThrowableItemProjectile {
    public SpectralBoltEntity(EntityType<? extends SpectralBoltEntity> type,Level level){super(type,level);setNoGravity(true);}
    @Override protected Item getDefaultItem(){return CatalogItems.RAW_ECTOPLASM;}
    @Override protected void onHitEntity(EntityHitResult hit){
        super.onHitEntity(hit);
        if(!level().isClientSide)hit.getEntity().hurt(damageSources().indirectMagic(this,getOwner()),4.0F);
    }
    @Override protected void onHit(HitResult hit){super.onHit(hit);if(!level().isClientSide)discard();}
    @Override public void tick(){super.tick();if(!level().isClientSide && tickCount>=80)discard();}
    public static void fire(Mob shooter,LivingEntity target){
        if(shooter.level().isClientSide || !target.isAlive())return;
        var bolt=new SpectralBoltEntity(CombatEntities.SPECTRAL_BOLT,shooter.level());bolt.setOwner(shooter);
        bolt.setPos(shooter.getX(),shooter.getEyeY()-0.1,shooter.getZ());
        var aim=target.getEyePosition().subtract(bolt.position());bolt.shoot(aim.x,aim.y,aim.z,1.1F,1.0F);
        shooter.level().addFreshEntity(bolt);
        shooter.playSound(net.minecraft.sounds.SoundEvents.SOUL_ESCAPE.value(),0.6F,1.2F);
    }
}
