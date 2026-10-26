package com.howierux.mortalexpansion.travel;

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

/** Persistent server-side escrow: Pending choice, Held journey, or Release requested. */
public final class DeferredDeathItems {
    private DeferredDeathItems() {}

    private static final ThreadLocal<ServerPlayer> CAPTURING=new ThreadLocal<>();
    public static void captureDeath(ServerPlayer p,Runnable original){if(!JourneyRuntime.canChoose(p)){original.run();return;}var previous=CAPTURING.get();CAPTURING.set(p);try{original.run();}finally{if(previous==null)CAPTURING.remove();else CAPTURING.set(previous);}}
    public static boolean capture(ServerPlayer actor,ItemEntity item){var player=CAPTURING.get();if(player==null||player!=actor||item==null||item.getItem().isEmpty())return false;CompoundTag storage=JourneyRuntime.escrow(player).copy();ListTag pending=storage.getList("Pending",Tag.TAG_COMPOUND);CompoundTag batch=new CompoundTag();ListTag drops=new ListTag();drops.add(item.saveWithoutId(new CompoundTag()));batch.put("Drops",drops);batch.putString("Dimension",player.level().dimension().location().toString());pending.add(batch);storage.put("Pending",pending);JourneyRuntime.escrow(player,storage);item.discard();return true;}
    public static void resolveChoice(ServerPlayer player, boolean journey) {
        movePending(player, journey ? "Held" : "Release");
        releaseRequested(player);
    }

    /** Failed journeys leave their original items at the original death site. */
    public static void abandonJourney(ServerPlayer player) {
        CompoundTag storage = JourneyRuntime.escrow(player).copy();
        ListTag held = storage.getList("Held", Tag.TAG_COMPOUND);
        ListTag release = storage.getList("Release", Tag.TAG_COMPOUND);
        for (Tag batch : held) release.add(batch.copy());
        storage.remove("Held");
        if (!release.isEmpty()) storage.put("Release", release);
        JourneyRuntime.escrow(player,storage);
        releaseRequested(player);
    }

    public static void recoverHeld(ServerPlayer player) {
        CompoundTag storage = JourneyRuntime.escrow(player).copy();
        ListTag held = storage.getList("Held", Tag.TAG_COMPOUND);
        storage.remove("Held");
        JourneyRuntime.escrow(player,storage);
        for (int i = 0; i < held.size(); i++) {
            ListTag drops = held.getCompound(i).getList("Drops", Tag.TAG_COMPOUND);
            for (int j = 0; j < drops.size(); j++) {
                ItemStack stack = ItemStack.parseOptional(player.registryAccess(), drops.getCompound(j).getCompound("Item"));
                if (!stack.isEmpty() && !player.getInventory().add(stack)) player.drop(stack, false);
            }
        }
        player.inventoryMenu.broadcastChanges();
    }

    private static void movePending(ServerPlayer player, String destination) {
        CompoundTag storage = JourneyRuntime.escrow(player).copy();
        ListTag pending = storage.getList("Pending", Tag.TAG_COMPOUND);
        if (pending.isEmpty()) return;
        ListTag target = storage.getList(destination, Tag.TAG_COMPOUND);
        for (Tag batch : pending) target.add(batch.copy());
        storage.remove("Pending");
        storage.put(destination, target);
        JourneyRuntime.escrow(player,storage);
    }

    public static void login(ServerPlayer player){if(!player.isDeadOrDying())movePending(player,"Release");releaseRequested(player);}
    private static void releaseRequested(ServerPlayer player) {
        CompoundTag storage = JourneyRuntime.escrow(player).copy();
        ListTag batches = storage.getList("Release", Tag.TAG_COMPOUND);
        if (batches.isEmpty()) return;
        // Detach before emitting entities, preventing a reentrant callback from
        // emitting the same saved inventory twice. Failed emissions stay queued.
        storage.remove("Release");
        JourneyRuntime.escrow(player,storage);
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
            CompoundTag current = JourneyRuntime.escrow(player).copy();
            ListTag queued = current.getList("Release", Tag.TAG_COMPOUND);
            for (Tag batch : retry) queued.add(batch.copy());
            current.put("Release", queued);
            JourneyRuntime.escrow(player,current);
        }
    }

    private static ServerLevel deathLevel(ServerPlayer player, String dimension) {
        ResourceLocation id = ResourceLocation.tryParse(dimension);
        return id == null ? null : player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, id));
    }
}
