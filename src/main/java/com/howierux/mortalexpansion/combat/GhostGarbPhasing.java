package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.entity.mob.SpectralPhasingRules;
import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Shared client/server garb movement: phase sideways/upward while retaining floors and spectral barriers. */
public final class GhostGarbPhasing {
    private GhostGarbPhasing() {}
    public static boolean active(Entity entity) {
        return entity instanceof Player player && !player.isSpectator() && HauntProtection.fullGarb(player);
    }
    public static Vec3 collide(Entity entity, Vec3 motion) {
        double y = motion.y>0 ? com.howierux.mortalexpansion.entity.mob.UpwardSpectralPhasing.rise(entity,motion.y) : Entity.collideBoundingBox(entity, new Vec3(0, motion.y, 0), entity.getBoundingBox(), entity.level(), java.util.List.of()).y;
        var box = entity.getBoundingBox().move(0, y, 0);
        // Check the swept path rather than only the endpoint, preventing barrier tunnelling.
        int steps = Math.max(1, (int)Math.ceil(Math.max(Math.abs(motion.x), Math.abs(motion.z)) * 4));
        double x = 0, z = 0;
        for (int i = 1; i <= steps; i++) {
            double nextX = motion.x * i / steps, nextZ = motion.z * i / steps;
            if (!SpectralPhasingRules.canEnterHorizontally(entity, box.move(nextX, 0, nextZ))) break;
            x = nextX; z = nextZ;
        }
        return new Vec3(x, y, z);
    }
}
