package com.howierux.mortalexpansion.discovery;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.LinkedHashMap;
import java.util.List;

/** Per-dimension cache of sites observed in generated/loaded terrain. Never locates by generating chunks. */
public final class SpectralSiteIndex extends SavedData {
    private final LinkedHashMap<String,SpectralSite> sites = new LinkedHashMap<>();
    public static SpectralSiteIndex get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(SpectralSiteIndex::new,SpectralSiteIndex::load,null),"mortal_expansion_spectral_sites");
    }
    public static SpectralSiteIndex load(CompoundTag tag, HolderLookup.Provider registries) {
        var data=new SpectralSiteIndex();
        if(tag.contains("Sites")) for(var site:SpectralSite.CODEC.listOf().parse(NbtOps.INSTANCE,tag.get("Sites")).getOrThrow()) data.sites.put(site.id(),site);
        return data;
    }
    public List<SpectralSite> sites() { return List.copyOf(sites.values()); }
    public SpectralSite find(String id) { return sites.get(id); }
    public void add(SpectralSite site) { if(!site.equals(sites.put(site.id(),site))) setDirty(); }
    public void remove(String id) { if(sites.remove(id)!=null)setDirty(); }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Sites",SpectralSite.CODEC.listOf().encodeStart(NbtOps.INSTANCE,sites()).getOrThrow());return tag;
    }
}
