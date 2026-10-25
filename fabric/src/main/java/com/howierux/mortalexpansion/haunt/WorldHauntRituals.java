package com.howierux.mortalexpansion.haunt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.*;
/** Loaded-only mutation with lazy reconciliation at the authoritative read/load boundary. */
public final class WorldHauntRituals {
 private static final Map<ServerLevel,Set<Long>> LOADED=new WeakHashMap<>();
 public static void track(ServerLevel level,ChunkPos pos){if(level.dimension().equals(Level.OVERWORLD))LOADED.computeIfAbsent(level,k->new HashSet<>()).add(pos.toLong());}
 public static void untrack(ServerLevel level,ChunkPos pos){var keys=LOADED.get(level);if(keys!=null)keys.remove(pos.toLong());}
 public static void clear(){LOADED.clear();}
 public static void reconcile(ServerLevel level,LevelChunk chunk){if(level.dimension().equals(Level.OVERWORLD))WorldHauntRitualData.get(level).apply(chunk);}
 public static List<LevelChunk> loadedChunks(ServerLevel level){
  var chunks=new ArrayList<LevelChunk>();for(long key:List.copyOf(LOADED.getOrDefault(level,Set.of()))){var pos=new ChunkPos(key);var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);if(chunk!=null)chunks.add(chunk);}return chunks;
 }
 public static boolean activate(ServerLevel level,BlockPos altar,boolean impure){
  if(!level.dimension().equals(Level.OVERWORLD))return false;
  var data=WorldHauntRitualData.get(level);data.record(impure);
  for(var chunk:loadedChunks(level))HauntManager.initialize(level,chunk);
  var own=level.getChunkSource().getChunkNow(altar.getX()>>4,altar.getZ()>>4);if(own!=null)HauntManager.initialize(level,own);
  level.sendParticles(impure?ParticleTypes.SOUL:ParticleTypes.END_ROD,altar.getX()+.5,altar.getY()+1.2,altar.getZ()+.5,40,.7,.6,.7,.03);
  for(var player:level.players())player.sendSystemMessage(Component.translatable(impure?"message.mortal_expansion.ritual.rise":"message.mortal_expansion.ritual.fall"));
  return true;
 }
}
