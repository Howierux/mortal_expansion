package com.howierux.mortalexpansion.loot;

import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public final class MobDropHandler {
    private MobDropHandler() {}

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType<?> type = entity.getType();

        if (isEctoplasmDrop(type)) {
            addDrop(event, new ItemStack(ModItems.RAW_ECTOPLASM.get()));
        }

        if (isRottenFleshDrop(type)) {
            addDrop(event, new ItemStack(Items.ROTTEN_FLESH));
        }

        if (type == ModEntities.PHARAOHL.get()) {
            addDrop(event, new ItemStack(ModItems.PHARAOHL_SCEPTER.get()));
        }
    }

    private static boolean isEctoplasmDrop(EntityType<?> type) {
        return type == EntityType.GHAST
                || type == ModEntities.GHOST.get()
                || type == ModEntities.SPIRIT.get()
                || type == ModEntities.ELEMENTAL.get()
                || type == ModEntities.HAUNT.get()
                || type == ModEntities.HAINT.get();
    }

    private static boolean isRottenFleshDrop(EntityType<?> type) {
        return type == ModEntities.PHANTOM.get()
                || type == ModEntities.MUMMY.get()
                || type == ModEntities.PHARAOHL.get();
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        LivingEntity entity = event.getEntity();
        event.getDrops().add(new ItemEntity(
                entity.level(),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                stack
        ));
    }
}
