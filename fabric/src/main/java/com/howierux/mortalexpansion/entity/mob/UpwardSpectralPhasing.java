package com.howierux.mortalexpansion.entity.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/** Upward swept-volume checks shared by intangible mobs and spectral-garb players. */
public final class UpwardSpectralPhasing {
    private UpwardSpectralPhasing() {}
    public static boolean active(Entity entity,Vec3 motion) {
        if(!(entity instanceof Mob) || motion.y<=0)return false;
        if(entity instanceof com.howierux.mortalexpansion.entity.DynamicIntangible dynamic)return dynamic.isCurrentlyIntangible();
        return com.howierux.mortalexpansion.entity.IntangibleEntities.isIntangible(entity);
    }
    public static double rise(Entity entity,double requested) {
        int steps=Math.max(1,(int)Math.ceil(requested*4));double rise=0;
        for(int i=1;i<=steps;i++) {
            double next=requested*i/steps;
            var swept=entity.getBoundingBox().expandTowards(0,next,0);
            if(net.minecraft.core.BlockPos.betweenClosedStream(swept.deflate(1.0E-4))
                    .anyMatch(pos->!entity.level().hasChunkAt(pos)))break;
            if(!SpectralPhasingRules.canEnterHorizontally(entity,swept))break;
            rise=next;
        }
        return rise;
    }
    public static Vec3 collide(Entity entity,Vec3 motion) {
        double rise=rise(entity,motion.y);
        var box=entity.getBoundingBox().move(0,rise,0);
        var horizontal=Entity.collideBoundingBox(entity,new Vec3(motion.x,0,motion.z),box,entity.level(),
                entity.level().getEntityCollisions(entity,box.expandTowards(motion.x,0,motion.z)));
        return new Vec3(horizontal.x,rise,horizontal.z);
    }
}
