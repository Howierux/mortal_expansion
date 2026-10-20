package com.howierux.mortalexpansion.guide;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
/** First Darkhold permanently opens the world to Demons, across dimensions. */
public final class DarkholdWorldData extends SavedData {
    public static final String KEY="mortal_expansion_darkhold";
    private boolean awakened;
    public static DarkholdWorldData get(ServerLevel level){return level.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(DarkholdWorldData::new,DarkholdWorldData::load,null),KEY);}
    public boolean awakened(){return awakened;}
    public void awaken(){if(!awakened){awakened=true;setDirty();}}
    public static DarkholdWorldData load(CompoundTag tag,HolderLookup.Provider lookup){var data=new DarkholdWorldData();data.awakened=tag.getBoolean("Awakened");return data;}
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){tag.putBoolean("Awakened",awakened);return tag;}
}
