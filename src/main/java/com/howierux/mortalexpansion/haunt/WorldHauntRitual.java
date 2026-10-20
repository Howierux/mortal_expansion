package com.howierux.mortalexpansion.haunt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.level.ChunkEvent;

/** Only loaded Overworld chunks are visited; untouched chunks reconcile on load/read. */
public final class WorldHauntRitual {
    private static final java.util.Map<ServerLevel,java.util.Set<Long>> LOADED=new java.util.WeakHashMap<>();
    private WorldHauntRitual(){}
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void load(ChunkEvent.Load event){
        if(!(event.getLevel() instanceof ServerLevel level) || !level.dimension().equals(Level.OVERWORLD)
                || !(event.getChunk() instanceof LevelChunk chunk))return;
        LOADED.computeIfAbsent(level,k->new java.util.HashSet<>()).add(chunk.getPos().toLong());
        reconcile(level,chunk);
    }
    @SubscribeEvent public static void unload(ChunkEvent.Unload event){
        if(event.getLevel() instanceof ServerLevel level){var loaded=LOADED.get(level);if(loaded!=null)loaded.remove(event.getChunk().getPos().toLong());}
    }
    public static void reconcile(ServerLevel level,LevelChunk chunk){
        if(level.dimension().equals(Level.OVERWORLD))WorldHauntRitualData.get(level).apply(chunk);
    }
    public static java.util.List<LevelChunk> loadedChunks(ServerLevel level){
        var result=new java.util.ArrayList<LevelChunk>();
        for(long key:LOADED.getOrDefault(level,java.util.Set.of())){
            var pos=new net.minecraft.world.level.ChunkPos(key);var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);
            if(chunk!=null)result.add(chunk);
        }
        return result;
    }
    public static boolean activate(ServerLevel level,BlockPos altar,boolean impure){
        if(!level.dimension().equals(Level.OVERWORLD))return false;
        var data=WorldHauntRitualData.get(level);data.record(impure);
        for(var chunk:loadedChunks(level))data.apply(chunk);
        // The altar's own chunk is authoritative even during unusual load order.
        var chunk=level.getChunkSource().getChunkNow(altar.getX()>>4,altar.getZ()>>4);if(chunk!=null)data.apply(chunk);
        level.sendParticles(impure?ParticleTypes.SOUL:ParticleTypes.END_ROD,altar.getX()+0.5,altar.getY()+1.2,altar.getZ()+0.5,40,0.7,0.6,0.7,0.03);
        for(var player:level.players())player.sendSystemMessage(Component.translatable(impure?"message.mortal_expansion.ritual.rise":"message.mortal_expansion.ritual.fall"));
        return true;
    }
}
