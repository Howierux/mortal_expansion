package com.howierux.mortalexpansion.machine;

import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import com.howierux.mortalexpansion.haunt.PurificationWardTracker;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.*;

/** Native region tickets with persistent owners. Never uses or removes vanilla /forceload tickets.
 * Keep ticket/version-specific calls here; each overlapping Ward owns its own tickets. */
public final class PurificationWards {
 private static final TicketType<BlockPos> TYPE=TicketType.create("mortal_expansion_purification_wards",Comparator.comparingLong(BlockPos::asLong));
 private static final Map<ServerLevel,Set<BlockPos>> TICKETS=new WeakHashMap<>(),PENDING=new WeakHashMap<>();
 public static final class Owners extends SavedData {
  public final Set<BlockPos> positions=new HashSet<>();
  public static Owners load(CompoundTag tag,HolderLookup.Provider lookup){var data=new Owners();for(long p:tag.getLongArray("Wards"))data.positions.add(BlockPos.of(p));return data;}
  @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){tag.putLongArray("Wards",positions.stream().mapToLong(BlockPos::asLong).toArray());return tag;}
 }
 public static Owners owners(ServerLevel level){return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(Owners::new,Owners::load,null),"mortal_expansion_purification_wards");}
 public static void restore(ServerLevel level){PENDING.computeIfAbsent(level,k->new HashSet<>()).addAll(owners(level).positions);}
 public static void add(ServerLevel level,BlockPos pos){
  pos=pos.immutable();var data=owners(level);if(data.positions.add(pos))data.setDirty();
  PurificationWardTracker.add(level,pos);PENDING.computeIfAbsent(level,k->new HashSet<>()).add(pos);
 }
 private static void tickets(ServerLevel level,BlockPos pos,boolean add){
  var center=new ChunkPos(pos);
  for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){
   var chunk=new ChunkPos(center.x+dx,center.z+dz);
   if(add)level.getChunkSource().addRegionTicket(TYPE,chunk,2,pos);else level.getChunkSource().removeRegionTicket(TYPE,chunk,2,pos);
  }
 }
 public static void release(ServerLevel level,BlockPos pos){
  var pending=PENDING.get(level);if(pending!=null)pending.remove(pos);
  var active=TICKETS.get(level);if(active!=null&&active.remove(pos))tickets(level,pos,false);
  var data=owners(level);if(data.positions.remove(pos))data.setDirty();PurificationWardTracker.remove(level,pos);
 }
 public static void tick(ServerLevel level){
  for(var pos:List.copyOf(PENDING.getOrDefault(level,Set.of()))){
   if(TICKETS.computeIfAbsent(level,k->new HashSet<>()).add(pos))tickets(level,pos,true);
   var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
   if(chunk==null)continue; // Ticket loading completes asynchronously; no synchronous world scans.
   if(!chunk.getBlockState(pos).is(CatalogBlocks.PURIFICATION_WARD))release(level,pos);
   else {PENDING.get(level).remove(pos);PurificationWardTracker.add(level,pos);}
  }
 }
 public static void clear(){TICKETS.clear();PENDING.clear();PurificationWardTracker.clear();}
}
