package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;

/** Ordered shifts preserve cap/cleansing behavior for chunks loaded after several rituals. */
public final class WorldHauntRitualData extends SavedData {
    public static final String KEY="mortal_expansion_world_haunt_rituals";
    private static final Factory<WorldHauntRitualData> FACTORY=new Factory<>(WorldHauntRitualData::new,WorldHauntRitualData::load,null);
    private final java.util.List<Integer> shifts=new java.util.ArrayList<>();
    public static WorldHauntRitualData get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(FACTORY,KEY);
    }
    public int revision(){return shifts.size();}
    public void record(boolean impure){shifts.add(impure?10:-10);setDirty();}
    public static WorldHauntRitualData load(CompoundTag tag,HolderLookup.Provider lookup){
        var data=new WorldHauntRitualData();
        for(int shift:tag.getIntArray("Shifts"))if(shift==10 || shift==-10)data.shifts.add(shift);
        return data;
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){
        tag.putIntArray("Shifts",shifts.stream().mapToInt(Integer::intValue).toArray());return tag;
    }
    public void apply(LevelChunk chunk){
        if(shifts.isEmpty())return;
        int applied=Math.min(chunk.getData(ModAttachments.HAUNT_RITUAL_REVISION),revision());
        if(applied==revision())return;
        var oldTier=HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER));
        int progress=chunk.getData(ModAttachments.HAUNT_PROGRESS);
        if(progress<0)progress=oldTier==HauntTier.VOID?0:HauntTier.DEFAULT_PROGRESS;
        int units=(oldTier.id()*10+progress)*100+chunk.getData(ModAttachments.HAUNT_FRACTION);
        for(int i=applied;i<shifts.size();i++)units=FractionalHaunt.add(units,shifts.get(i)*100L);
        var tier=HauntTier.fromValue(units/100);
        chunk.setData(ModAttachments.HAUNT_TIER,tier.id());
        chunk.setData(ModAttachments.HAUNT_PROGRESS,HauntTier.progressFromValue(units/100));
        chunk.setData(ModAttachments.HAUNT_FRACTION,units%100);
        chunk.setData(ModAttachments.HAUNT_RITUAL_REVISION,revision());
        if(oldTier!=tier)com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity.onChunkTierChanged(chunk,tier);
    }
}
