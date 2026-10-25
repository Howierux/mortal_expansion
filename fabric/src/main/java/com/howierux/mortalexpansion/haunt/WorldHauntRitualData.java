package com.howierux.mortalexpansion.haunt;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.*;

/** Preserve ordered clamping for old, unloaded and newly generated chunks; never shift biome baselines. */
public final class WorldHauntRitualData extends SavedData {
 public static final String KEY="mortal_expansion_world_haunt_rituals";
 private static final Factory<WorldHauntRitualData> FACTORY=new Factory<>(WorldHauntRitualData::new,WorldHauntRitualData::load,null);
 private final List<Integer> shifts=new ArrayList<>();
 public static WorldHauntRitualData get(ServerLevel level){return level.getDataStorage().computeIfAbsent(FACTORY,KEY);}
 public int revision(){return shifts.size();}
 public void record(boolean impure){shifts.add(impure?10:-10);setDirty();}
 public static WorldHauntRitualData load(CompoundTag tag,HolderLookup.Provider lookup){var data=new WorldHauntRitualData();for(int shift:tag.getIntArray("Shifts"))if(shift==10||shift==-10)data.shifts.add(shift);return data;}
 @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){tag.putIntArray("Shifts",shifts.stream().mapToInt(Integer::intValue).toArray());return tag;}
 public void apply(LevelChunk chunk){
  if(shifts.isEmpty())return;
  var target=(AttachmentTarget)chunk;int applied=Math.min(target.getAttachedOrCreate(FabricHauntData.RITUAL_REVISION),revision());if(applied==revision())return;
  int before=target.getAttachedOrThrow(FabricHauntData.CHUNK),next=before;
  for(int i=applied;i<revision();i++)next=FractionalHaunt.add(next,shifts.get(i)*100L);
  target.setAttached(FabricHauntData.CHUNK,next);target.setAttached(FabricHauntData.RITUAL_REVISION,revision());HauntManager.notifyTier(chunk,before,next);
 }
}
