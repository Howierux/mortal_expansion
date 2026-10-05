package com.howierux.mortalexpansion.haunt.effects;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.haunt.PurificationWardTracker;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public final class HauntProtection {
    public static final TagKey<Item> GARB = TagKey.create(net.minecraft.core.registries.Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectral_garb"));
    private static final ThreadLocal<Boolean> APPLYING = ThreadLocal.withInitial(() -> false);
    private HauntProtection() {}
    public static long now(ServerPlayer player) { return player.server.overworld().getGameTime(); }
    public static CompoundTag state(ServerPlayer player) { return player.getData(ModAttachments.AMBIENT_HAUNT_STATE).copy(); }
    public static void state(ServerPlayer player, CompoundTag state) { player.setData(ModAttachments.AMBIENT_HAUNT_STATE, state); }
    public static boolean fullGarb(net.minecraft.world.entity.LivingEntity player) {
        for (ItemStack stack : player.getArmorSlots()) if (stack.isEmpty() || !stack.is(GARB)) return false;
        return true;
    }
    public static boolean protectedPlayer(ServerPlayer player) {
        return fullGarb(player) || state(player).getLong("BlessedUntil") > now(player)
                || PurificationWardTracker.isChunkProtected(player.serverLevel(), new ChunkPos(player.blockPosition()));
    }
    public static void bless(ServerPlayer player) {
        long now = now(player);
        CompoundTag state = state(player);
        if (state.getLong("ContactAfter") > now) return;
        state.putLong("ContactAfter", now + 20);
        boolean first = state.getLong("BlessedUntil") <= now;
        state.putLong("BlessedUntil", now + 1200);
        if (state.getLong("CleanseAfter") <= now) {
            PlayerHauntManager.addPoints(player, -1);
            state.putLong("CleanseAfter", now + 1200);
        }
        state(player, state);
        clearNegative(player);
        if (first) player.displayClientMessage(Component.translatable("message.mortal_expansion.holy_water_blessing"), true);
    }
    /** Equipment protection clears only haunting-owned effects, without granting a water blessing. */
    @SubscribeEvent public static void equipmentChanged(net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && fullGarb(player)) clearNegative(player);
    }
    public static void clearNegative(ServerPlayer player) {
        CompoundTag state = state(player);
        // Only remove unchanged effects owned by haunting. External applications
        // relinquish ownership, including hidden/merged potion effects.
        CompoundTag owned = state.getCompound("Owned");
        for (String id : owned.getAllKeys()) {
            var holder = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(id));
            if (holder.isEmpty()) continue;
            var current = player.getEffect(holder.get());
            CompoundTag record = owned.getCompound(id);
            if (current != null && current.getAmplifier() == record.getInt("Amplifier")
                    && current.getDuration() <= record.getInt("Duration")) player.removeEffect(holder.get());
        }
        state.remove("Owned");
        state.remove("CompanyUntil"); state.remove("MarkedUntil"); state.remove("BreachAt");
        state(player, state);
        com.howierux.mortalexpansion.network.HauntEffectPayloads.clearShadow(player);
    }
    public static boolean apply(ServerPlayer player, Holder<MobEffect> effect, int duration, int amplifier, boolean negative) {
        if ((negative && protectedPlayer(player)) || player.hasEffect(effect)) return false;
        boolean applied;
        APPLYING.set(true);
        try { applied = player.addEffect(new MobEffectInstance(effect, duration, amplifier)); }
        finally { APPLYING.remove(); }
        if (applied && negative) {
            CompoundTag state = state(player), owned = state.getCompound("Owned"), record = new CompoundTag();
            record.putInt("Amplifier", amplifier);
            record.putInt("Duration", duration);
            owned.put(BuiltInRegistries.MOB_EFFECT.getKey(effect.value()).toString(), record);
            state.put("Owned", owned);
            state(player, state);
        }
        return applied;
    }
    @SubscribeEvent public static void externalEffect(MobEffectEvent.Added event) {
        if (APPLYING.get() || !(event.getEntity() instanceof ServerPlayer player)) return;
        var instance = event.getEffectInstance();
        if (instance == null) return;
        CompoundTag state = state(player), owned = state.getCompound("Owned");
        owned.remove(BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value()).toString());
        state.put("Owned", owned);
        state(player, state);
    }
    public static void repairGarb(ServerPlayer player) {
        if (PlayerHauntManager.getTier(player).id()
                < com.howierux.mortalexpansion.haunt.HauntTier.DEAD_RISING.id()) return;
        boolean changed = false;
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.is(GARB) && stack.isDamaged()) { stack.setDamageValue(stack.getDamageValue() - 1); changed = true; }
        }
        if (changed) player.containerMenu.broadcastChanges();
    }
}
