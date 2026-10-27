package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.command.HauntCommands;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;

public final class HauntRuntime {
    private HauntRuntime(){}
    public static void initialize(){
        FabricHauntData.initialize();HauntCommands.register();
        ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->PlayerHauntManager.getHundredths(handler.player));
        ServerChunkEvents.CHUNK_LOAD.register((level,chunk)->{HauntManager.initialize(level,chunk);HauntSimulation.track(level,chunk.getPos());WorldHauntRituals.track(level,chunk.getPos());});
        ServerChunkEvents.CHUNK_UNLOAD.register((level,chunk)->{HauntSimulation.untrack(level,chunk.getPos());WorldHauntRituals.untrack(level,chunk.getPos());});
        ServerTickEvents.END_WORLD_TICK.register(HauntSimulation::tick);
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{HauntSimulation.clear();WorldHauntRituals.clear();});
        ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{
            if(entity.getType()==net.minecraft.world.entity.EntityType.GHAST)entity.spawnAtLocation(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM);
            if(entity instanceof Mob mob && !mob.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)
                    && source.getEntity() instanceof ServerPlayer p)PlayerHauntManager.addHundredths(p,FractionalHaunt.MOB_KILL_INCREASE);
        });
    }
}
