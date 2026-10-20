package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

/**
 * Applies Mortal Expansion's dynamic Haunt requirements to natural mob spawns.
 *
 * NATURAL runtime spawns are modified. CHUNK_GENERATION is deliberately
 * excluded because querying persistent chunk Haunt through ServerLevel while
 * that chunk is still in the worldgen pipeline can recursively request chunk
 * generation and stall spawn-region creation. Spawners, commands, structures,
 * summons and other explicit spawn sources keep their normal behavior.
 */
public final class HauntSpawnHandler {
    private HauntSpawnHandler() {}

    public static boolean canSpawnDemon(ServerLevel level,net.minecraft.core.BlockPos pos) {
        return com.howierux.mortalexpansion.guide.DarkholdWorldData.get(level).awakened()
                && HauntManager.getTier(level,pos).id()>=HauntTier.DEMONIC.id();
    }
    private static final float NATURAL_ZOMBIE_ACCEPTANCE = 0.20F;
    private static final float PRE_ELDRITCH_ENDERMAN_ACCEPTANCE = 0.25F;

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (!isWorldSpawn(event.getSpawnType())) {
            return;
        }

        Mob mob = event.getEntity();
        if (!(mob.level() instanceof ServerLevel level))return;
        if(mob.getType()==ModEntities.DEMON.get()){
            if(!canSpawnDemon(level,mob.blockPosition()))event.setSpawnCancelled(true);
            return;
        }
        if(!level.dimension().equals(ServerLevel.OVERWORLD))return;

        HauntTier tier = HauntManager.getTier(level, mob.blockPosition());
        EntityType<?> type = mob.getType();

        if (isZombieFamily(type)) {
            if (tier.id() < HauntTier.NATURAL.id()) {
                event.setSpawnCancelled(true);
                return;
            }

            if (tier == HauntTier.NATURAL
                    && mob.getRandom().nextFloat() >= NATURAL_ZOMBIE_ACCEPTANCE) {
                event.setSpawnCancelled(true);
            }
            return;
        }

        if (isBasicSkeletonFamily(type)) {
            if (tier.id() < HauntTier.SUPERNATURAL.id()) {
                event.setSpawnCancelled(true);
            }
            return;
        }

        if (type == EntityType.WITCH) {
            if (tier.id() < HauntTier.SUPERNATURAL.id()) {
                event.setSpawnCancelled(true);
            }
            return;
        }

        if (type == EntityType.WITHER_SKELETON) {
            if (tier.id() < HauntTier.DEMONIC.id()) {
                event.setSpawnCancelled(true);
                return;
            }

            float acceptance = switch (tier) {
                case DEMONIC -> 0.25F;
                case ABOMINABLE -> 0.50F;
                case ELDRITCH -> 1.00F;
                default -> 0.0F;
            };

            if (mob.getRandom().nextFloat() >= acceptance) {
                event.setSpawnCancelled(true);
            }
            return;
        }

        if (type == EntityType.GHAST) {
            if (tier.id() < HauntTier.ABOMINABLE.id()) {
                event.setSpawnCancelled(true);
            }
            return;
        }

        if (type == EntityType.ENDERMAN) {
            if (tier != HauntTier.ELDRITCH
                    && mob.getRandom().nextFloat() >= PRE_ELDRITCH_ENDERMAN_ACCEPTANCE) {
                event.setSpawnCancelled(true);
            }
            return;
        }

        if (type == ModEntities.SPIRIT.get()) {
            cancelBelow(event, tier, HauntTier.NATURAL);
            return;
        }

        if (type == ModEntities.GHOST.get()) {
            cancelBelow(event, tier, HauntTier.DEAD_RISING);
            return;
        }

        if (type == ModEntities.PHANTOM.get()
                || type == ModEntities.HAUNT.get()) {
            cancelBelow(event, tier, HauntTier.DEMONIC);
        }
    }

    /**
     * At Supernatural tier the design calls for melee skeletons. Equipment is
     * changed after vanilla finalizeSpawn has equipped the skeleton, otherwise
     * vanilla initialization would simply put the bow back.
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)
                || !isWorldSpawn(mob.getSpawnType())
                || !(event.getLevel() instanceof ServerLevel level))return;
        if(mob.getType()==ModEntities.DEMON.get()){
            if(!canSpawnDemon(level,mob.blockPosition()))event.setCanceled(true);
            return;
        }
        if(!level.dimension().equals(ServerLevel.OVERWORLD))return;

        HauntTier tier = HauntManager.getTier(level, mob.blockPosition());
        EntityType<?> type = mob.getType();

        // Safety net for Haunt-controlled Mortal Expansion mobs. This catches
        // world-generation creature spawning even if it bypasses FinalizeSpawn.
        if ((type == ModEntities.SPIRIT.get() && tier.id() < HauntTier.NATURAL.id())
                || (type == ModEntities.GHOST.get() && tier.id() < HauntTier.DEAD_RISING.id())
                || ((type == ModEntities.PHANTOM.get() || type == ModEntities.HAUNT.get())
                && tier.id() < HauntTier.DEMONIC.id())) {
            event.setCanceled(true);
            return;
        }

        if (mob instanceof AbstractSkeleton skeleton
                && !(skeleton instanceof WitherSkeleton)
                && tier == HauntTier.SUPERNATURAL) {
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            skeleton.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
    }

    private static boolean isWorldSpawn(MobSpawnType spawnType) {
        return spawnType == MobSpawnType.NATURAL;
    }

    private static void cancelBelow(
            FinalizeSpawnEvent event,
            HauntTier current,
            HauntTier minimum
    ) {
        if (current.id() < minimum.id()) {
            event.setSpawnCancelled(true);
        }
    }

    private static boolean isZombieFamily(EntityType<?> type) {
        return type == EntityType.ZOMBIE
                || type == EntityType.ZOMBIE_VILLAGER
                || type == EntityType.HUSK
                || type == EntityType.DROWNED;
    }

    private static boolean isBasicSkeletonFamily(EntityType<?> type) {
        return type == EntityType.SKELETON
                || type == EntityType.STRAY
                || type == EntityType.BOGGED;
    }
}
