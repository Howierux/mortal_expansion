package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public final class HauntDaylightRules {
    private HauntDaylightRules() {}

    /** These peaceful spectral creatures are sustained in daylight at every Haunt tier. */
    private static boolean daylightImmune(Mob mob) {
        return mob instanceof com.howierux.mortalexpansion.entity.mob.SpiritEntity
                || mob instanceof com.howierux.mortalexpansion.entity.mob.ElementalEntity;
    }

    public static boolean applies(Mob mob) {
        if (daylightImmune(mob)) return false;
        return mob instanceof Zombie || mob instanceof AbstractSkeleton
                || BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace()
                        .equals(MortalExpansion.MOD_ID);
    }

    public static boolean protectionAt(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        return chunk != null && HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER)).id()
                >= HauntTier.SUPERNATURAL.id();
    }

    public static boolean sunlightProtected(Mob mob) {
        return daylightImmune(mob) || applies(mob) && mob.level() instanceof ServerLevel level
                && protectionAt(level, mob.blockPosition());
    }

    public static HauntTier minimumDaytimeTier(Mob mob) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        return switch (id) {
            case "mortal_expansion:ghost" -> HauntTier.DEAD_RISING;
            case "mortal_expansion:demon", "mortal_expansion:phantom", "mortal_expansion:haunt", "minecraft:wither_skeleton"
                    -> HauntTier.DEMONIC;
            case "mortal_expansion:haint" -> HauntTier.ABOMINABLE;
            default -> HauntTier.SUPERNATURAL;
        };
    }

    public static boolean sustains(ServerLevel level, BlockPos pos, Mob mob) {
        if (daylightImmune(mob)) return true;
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        return chunk != null && HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER)).id()
                >= minimumDaytimeTier(mob).id();
    }

    public static boolean shelterTime(ServerLevel level) {
        return HauntDaylightWindow.active(level.dimensionType().hasSkyLight(),
                level.isDay(), level.getDayTime());
    }

    /** Keep voluntary daytime movement within a sustaining Haunt tier when sky is exposed. */
    public static Vec3 limitMovement(Mob mob, Vec3 movement) {
        if (movement.x == 0 && movement.z == 0 || !applies(mob)
                || !(mob.level() instanceof ServerLevel level) || !shelterTime(level)) return movement;
        BlockPos from = mob.blockPosition();
        BlockPos to = BlockPos.containing(mob.getX() + movement.x,
                mob.getY() + movement.y, mob.getZ() + movement.z);
        if ((from.getX() >> 4) == (to.getX() >> 4) && (from.getZ() >> 4) == (to.getZ() >> 4))
            return movement;
        if (sustains(level, from, mob) && !sustains(level, to, mob)
                && (level.canSeeSky(from) || level.hasChunkAt(to) && level.canSeeSky(to))) {
            return new Vec3(0, movement.y, 0);
        }
        return movement;
    }
}
