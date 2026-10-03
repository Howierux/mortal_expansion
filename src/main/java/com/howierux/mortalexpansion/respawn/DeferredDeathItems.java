package com.howierux.mortalexpansion.respawn;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Persistent server-side escrow: Pending choice, Held journey, or Release requested. */
public final class DeferredDeathItems {
    private DeferredDeathItems() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getDrops().isEmpty()
                || !SpiritJourneyHandler.canChoose(player)) return;

        ListTag drops = new ListTag();
        for (ItemEntity item : event.getDrops()) {
            if (item.getItem().isEmpty()) continue;
            drops.add(item.saveWithoutId(new CompoundTag()));
        }
        if (drops.isEmpty()) return;
        CompoundTag batch = new CompoundTag();
        batch.putString("Dimension", player.level().dimension().location().toString());
        batch.put("Drops", drops);
        CompoundTag storage = player.getData(ModAttachments.DEFERRED_DEATH_ITEMS).copy();
        ListTag pending = storage.getList("Pending", Tag.TAG_COMPOUND);
        pending.add(batch);
        storage.put("Pending", pending);
        // Save the actual vanilla drops before removing them from the spawn collection.
        // Vanishing already ran, and keepInventory produces no inventory drops here.
        player.setData(ModAttachments.DEFERRED_DEATH_ITEMS, storage);
        event.getDrops().clear();
    }

    public static void resolveChoice(ServerPlayer player, boolean journey) {
        movePending(player, journey ? "Held" : "Release");
        releaseRequested(player);
    }

    private static void movePending(ServerPlayer player, String destination) {
        CompoundTag storage = player.getData(ModAttachments.DEFERRED_DEATH_ITEMS).copy();
        ListTag pending = storage.getList("Pending", Tag.TAG_COMPOUND);
        if (pending.isEmpty()) return;
        ListTag target = storage.getList(destination, Tag.TAG_COMPOUND);
        for (Tag batch : pending) target.add(batch.copy());
        storage.remove("Pending");
        storage.put(destination, target);
        player.setData(ModAttachments.DEFERRED_DEATH_ITEMS, storage);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.setData(ModAttachments.SPIRIT_JOURNEY_RESPAWN, false);
        // A still-dead player must choose first. Held journey items never auto-release.
        if (!player.isDeadOrDying()) movePending(player, "Release");
        releaseRequested(player);
    }

    private static void releaseRequested(ServerPlayer player) {
        CompoundTag storage = player.getData(ModAttachments.DEFERRED_DEATH_ITEMS).copy();
        ListTag batches = storage.getList("Release", Tag.TAG_COMPOUND);
        if (batches.isEmpty()) return;
        // Detach before emitting entities, preventing a reentrant callback from
        // emitting the same saved inventory twice. Failed emissions stay queued.
        storage.remove("Release");
        player.setData(ModAttachments.DEFERRED_DEATH_ITEMS, storage);
        ListTag retry = new ListTag();
        for (int i = 0; i < batches.size(); i++) {
            CompoundTag batch = batches.getCompound(i);
            ServerLevel level = deathLevel(player, batch.getString("Dimension"));
            if (level == null) { retry.add(batch.copy()); continue; }
            ListTag failures = new ListTag();
            ListTag drops = batch.getList("Drops", Tag.TAG_COMPOUND);
            for (int j = 0; j < drops.size(); j++) {
                CompoundTag saved = drops.getCompound(j);
                ItemStack stack = ItemStack.parseOptional(level.registryAccess(), saved.getCompound("Item"));
                if (stack.isEmpty()) continue;
                ItemEntity item = new ItemEntity(level, 0, 0, 0, stack);
                item.load(saved.copy());
                level.getChunk(BlockPos.containing(item.position()));
                // An already-loaded matching UUID means this drop was emitted before.
                if (level.getEntity(item.getUUID()) != null) continue;
                if (!level.addFreshEntity(item)) failures.add(saved.copy());
            }
            if (!failures.isEmpty()) {
                CompoundTag remainder = batch.copy();
                remainder.put("Drops", failures);
                retry.add(remainder);
            }
        }
        if (!retry.isEmpty()) {
            CompoundTag current = player.getData(ModAttachments.DEFERRED_DEATH_ITEMS).copy();
            ListTag queued = current.getList("Release", Tag.TAG_COMPOUND);
            for (Tag batch : retry) queued.add(batch.copy());
            current.put("Release", queued);
            player.setData(ModAttachments.DEFERRED_DEATH_ITEMS, current);
        }
    }

    private static ServerLevel deathLevel(ServerPlayer player, String dimension) {
        ResourceLocation id = ResourceLocation.tryParse(dimension);
        return id == null ? null : player.server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
    }
}
