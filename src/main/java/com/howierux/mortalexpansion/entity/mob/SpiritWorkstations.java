package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.phys.AABB;

/** Inscription-dependent workstations cannot be expressed by block-state-only vanilla POIs. */
public final class SpiritWorkstations {
    private SpiritWorkstations() {}
    public static boolean isWorkstation(GraveBlockEntity grave) {
        return grave.getInscription().strip().equalsIgnoreCase("Villager");
    }
    public static void tick(ServerLevel level, GraveBlockEntity grave) {
        if (!isWorkstation(grave) || grave.getSpiritWorker() != null) return;
        for (Villager villager : level.getEntitiesOfClass(Villager.class, new AABB(grave.getBlockPos()).inflate(4),
                v -> v.getType() == EntityType.VILLAGER && v.isAlive() && !v.isBaby() && !v.isTrading()
                        && v.getVillagerData().getProfession() == VillagerProfession.NONE)) {
            convert(level, grave, villager); break;
        }
    }
    public static SpiritEntity convert(ServerLevel level, GraveBlockEntity grave, Villager villager) {
        if (!isWorkstation(grave) || grave.getSpiritWorker() != null || villager.isBaby()
                || villager.getVillagerData().getProfession() != VillagerProfession.NONE) return null;
        CompoundTag original = villager.saveWithoutId(new CompoundTag());
        SpiritEntity spirit = villager.convertTo(ModEntities.SPIRIT.get(), true);
        if (spirit == null) return null;
        spirit.setAge(villager.getAge());
        spirit.setHealth(Math.min(villager.getHealth(), spirit.getMaxHealth()));
        copyInventory(villager, spirit);
        spirit.bindGrave(grave.getBlockPos(), original);
        grave.setSpiritWorker(spirit.getUUID());
        return spirit;
    }
    public static boolean validate(ServerLevel level, SpiritEntity spirit) {
        BlockPos pos = spirit.workstation();
        // An unloaded workstation is unknown, not destroyed. Never load it for validation.
        ServerLevel home = spirit.workstationLevel(level);
        if (home == null || !home.hasChunkAt(pos)) return true;
        if (home.getBlockEntity(pos) instanceof GraveBlockEntity grave && isWorkstation(grave)
                && spirit.getUUID().equals(grave.getSpiritWorker())) return true;
        revert(level, spirit); return false;
    }
    public static Villager revert(ServerLevel level, SpiritEntity spirit) {
        if (!spirit.graveCreated()) return null;
        if (spirit.getTradingPlayer() != null) spirit.getTradingPlayer().closeContainer();
        spirit.setTradingPlayer(null);
        CompoundTag saved = spirit.originalVillager();
        CompoundTag original = new CompoundTag();
        for (String key : new String[]{"VillagerData", "Gossips", "FoodLevel"}) {
            if (saved.contains(key)) original.put(key, saved.get(key).copy());
        }
        Villager villager = spirit.convertTo(EntityType.VILLAGER, true);
        if (villager == null) return null;
        release(spirit);
        villager.readAdditionalSaveData(original);
        villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.NONE).setLevel(1));
        villager.setVillagerXp(0);
        villager.setAge(spirit.getAge());
        villager.setHealth(Math.min(spirit.getHealth(), villager.getMaxHealth()));
        copyInventory(spirit, villager);
        villager.refreshBrain(level);
        return villager;
    }
    private static void copyInventory(AbstractVillager from, AbstractVillager to) {
        for (int i = 0; i < from.getInventory().getContainerSize(); i++) to.getInventory().setItem(i, from.getInventory().getItem(i).copy());
    }
    public static void release(SpiritEntity spirit) {
        if (!spirit.graveCreated() || !(spirit.level() instanceof ServerLevel current)) return;
        ServerLevel home = spirit.workstationLevel(current);
        if (home != null && home.hasChunkAt(spirit.workstation())
                && home.getBlockEntity(spirit.workstation()) instanceof GraveBlockEntity grave
                && spirit.getUUID().equals(grave.getSpiritWorker())) grave.setSpiritWorker(null);
    }
    public static void removed(ServerLevel level, GraveBlockEntity grave) {
        if (grave.getSpiritWorker() == null) return;
        for (ServerLevel loaded : level.getServer().getAllLevels()) {
            if (loaded.getEntity(grave.getSpiritWorker()) instanceof SpiritEntity spirit
                    && spirit.workstationLevel(loaded) == level && grave.getBlockPos().equals(spirit.workstation())) {
                revert(loaded, spirit); break;
            }
        }
    }
}
