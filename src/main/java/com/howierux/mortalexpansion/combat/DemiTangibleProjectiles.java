package com.howierux.mortalexpansion.combat;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import java.util.function.Supplier;
/** Capture at launch, never infer permission from the shooter's inventory at impact/load. */
@EventBusSubscriber(modid="mortal_expansion")
public final class DemiTangibleProjectiles {
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
    @SubscribeEvent public static void spawned(EntityJoinLevelEvent event){
        if(event.getLevel().isClientSide || event.loadedFromDisk() || !(event.getEntity() instanceof Projectile projectile))return;
        boolean demi=DemiTangibleDamage.isDemiTangibleWeapon(projectile.getWeaponItem());
        if(projectile instanceof AbstractArrow arrow)demi|=DemiTangibleDamage.isDemiTangibleWeapon(arrow.getPickupItemStackOrigin());
        if(projectile instanceof ThrowableItemProjectile thrown)demi|=DemiTangibleDamage.isDemiTangibleWeapon(thrown.getItem());
        Use use=USE.get();Entity owner=projectile.getOwner();
        if(use!=null && (owner==null || owner==use.actor))demi|=use.demi;
        else if((projectile.getWeaponItem()==null || projectile.getWeaponItem().isEmpty()) && owner instanceof LivingEntity living && living.isUsingItem())
            demi|=DemiTangibleDamage.isDemiTangibleWeapon(living.getUseItem());
        projectile.setData(ModAttachments.DEMI_TANGIBLE_PROJECTILE,demi);
    }
    public static boolean damage(net.minecraft.world.damagesource.DamageSource source){
        Entity direct=source.getDirectEntity();
        if(!(direct instanceof Projectile projectile))return false;
        if(projectile.hasData(ModAttachments.DEMI_TANGIBLE_PROJECTILE))return projectile.getData(ModAttachments.DEMI_TANGIBLE_PROJECTILE);
        if(DemiTangibleDamage.isDemiTangibleWeapon(projectile.getWeaponItem()))return true;
        if(projectile instanceof AbstractArrow arrow && DemiTangibleDamage.isDemiTangibleWeapon(arrow.getPickupItemStackOrigin()))return true;
        return projectile instanceof ThrowableItemProjectile thrown && DemiTangibleDamage.isDemiTangibleWeapon(thrown.getItem());
    }
    public static boolean marked(Entity entity){
        return entity instanceof Projectile && entity.hasData(ModAttachments.DEMI_TANGIBLE_PROJECTILE) && entity.getData(ModAttachments.DEMI_TANGIBLE_PROJECTILE);
    }
}
