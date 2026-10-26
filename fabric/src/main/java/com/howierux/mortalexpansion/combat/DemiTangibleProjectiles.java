package com.howierux.mortalexpansion.combat;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import java.util.function.Supplier;
/** Capture at launch, never infer permission from the shooter's inventory at impact/load. */

public final class DemiTangibleProjectiles {
    private static ItemStack weapon(Projectile p){return p instanceof AbstractArrow arrow?arrow.getWeaponItem():ItemStack.EMPTY;}
    private record Use(Entity actor,boolean demi){}
    private static final ThreadLocal<Use> USE=new ThreadLocal<>();
    private DemiTangibleProjectiles(){}
    public static <T> T using(ItemStack item,LivingEntity actor,Supplier<T> action){
        if(actor.level().isClientSide)return action.get();
        boolean demi=DemiTangibleDamage.isDemiTangibleWeapon(item);
        // Snapshot selected ammunition before a launcher consumes or normalizes it.
        // Loaded crossbows use their own ammunition, not unrelated reserve arrows.
        var charged=item.get(net.minecraft.core.component.DataComponents.CHARGED_PROJECTILES);
        if(item.getItem() instanceof ProjectileWeaponItem && (charged==null || charged.isEmpty()))
            demi|=DemiTangibleDamage.isDemiTangibleWeapon(actor.getProjectile(item));
        Use previous=USE.get();USE.set(new Use(actor,demi));
        try{return action.get();}finally{if(previous==null)USE.remove();else USE.set(previous);}
    }
    public static void spawned(Entity entity,net.minecraft.server.level.ServerLevel level){
        if(!(entity instanceof Projectile projectile)||((AttachmentTarget)projectile).hasAttached(CombatRuntime.PROJECTILE))return;
        boolean demi=DemiTangibleDamage.isDemiTangibleWeapon(weapon(projectile));
        if(projectile instanceof AbstractArrow arrow)demi|=DemiTangibleDamage.isDemiTangibleWeapon(arrow.getPickupItemStackOrigin());
        if(projectile instanceof ThrowableItemProjectile thrown)demi|=DemiTangibleDamage.isDemiTangibleWeapon(thrown.getItem());
        Use use=USE.get();Entity owner=projectile.getOwner();
        if(use!=null && (owner==null || owner==use.actor))demi|=use.demi;
        ((AttachmentTarget)projectile).setAttached(CombatRuntime.PROJECTILE,demi);
    }
    public static boolean damage(net.minecraft.world.damagesource.DamageSource source){
        Entity direct=source.getDirectEntity();
        if(!(direct instanceof Projectile projectile))return false;
        if(((AttachmentTarget)projectile).hasAttached(CombatRuntime.PROJECTILE))return ((AttachmentTarget)projectile).getAttached(CombatRuntime.PROJECTILE);
        if(DemiTangibleDamage.isDemiTangibleWeapon(weapon(projectile)))return true;
        if(projectile instanceof AbstractArrow arrow && DemiTangibleDamage.isDemiTangibleWeapon(arrow.getPickupItemStackOrigin()))return true;
        return projectile instanceof ThrowableItemProjectile thrown && DemiTangibleDamage.isDemiTangibleWeapon(thrown.getItem());
    }
    public static boolean marked(Entity entity){
        return entity instanceof Projectile && ((AttachmentTarget)entity).hasAttached(CombatRuntime.PROJECTILE) && ((AttachmentTarget)entity).getAttached(CombatRuntime.PROJECTILE);
    }
}
