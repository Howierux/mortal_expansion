package com.howierux.mortalexpansion.haunt.effects;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.Map;

/** Per-dimension durable deferred encounters and shared world-event cooldowns. */
public final class HauntEventData extends SavedData {
    private long pendingGhosts;
    private final Map<Long, Long> chunkCooldowns = new HashMap<>();
    public static HauntEventData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(HauntEventData::new, HauntEventData::load, null),
                "mortal_expansion_haunt_events");
    }
    public static HauntEventData load(CompoundTag tag, HolderLookup.Provider registries) {
        HauntEventData data = new HauntEventData();
        data.pendingGhosts = Math.max(0, tag.getLong("PendingGhosts"));
        CompoundTag cooldowns = tag.getCompound("Chunks");
        for (String key : cooldowns.getAllKeys()) {
            try { data.chunkCooldowns.put(Long.parseLong(key), cooldowns.getLong(key)); }
            catch (NumberFormatException ignored) {}
        }
        return data;
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("PendingGhosts", pendingGhosts);
        CompoundTag chunks = new CompoundTag();
        chunkCooldowns.forEach((pos, until) -> chunks.putLong(Long.toString(pos), until));
        tag.put("Chunks", chunks);
        return tag;
    }
    public long pendingGhosts() { return pendingGhosts; }
    public void deferGhost() {
        if (pendingGhosts == Long.MAX_VALUE) throw new IllegalStateException("Spiritwell queue overflow");
        pendingGhosts++;
        setDirty();
    }
    public void deliveredGhost() {
        if (pendingGhosts > 0) { pendingGhosts--; setDirty(); }
    }
    public boolean chunkReady(long chunk, long now) { return chunkCooldowns.getOrDefault(chunk, 0L) <= now; }
    public void coolChunk(long chunk, long now) { chunkCooldowns.put(chunk, now + 1200); setDirty(); }
    public void prune(long now) {
        if (chunkCooldowns.values().removeIf(until -> until <= now)) setDirty();
    }
}
