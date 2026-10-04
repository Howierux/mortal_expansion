package com.howierux.mortalexpansion.haunt.effects;

import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import java.util.ArrayList;
import java.util.Arrays;

public final class HauntEventController {
    private HauntEventController() {}
    public static boolean ready(ServerPlayer player) {
        var state = HauntProtection.state(player);
        long now = HauntProtection.now(player);
        return state.getLong("CooldownUntil") <= now && state.getLong("ActiveUntil") <= now;
    }
    public static void completed(ServerPlayer player, int activeTicks) {
        var state = HauntProtection.state(player);
        long now = HauntProtection.now(player);
        state.putLong("CooldownUntil", now + 1200);
        state.putLong("ActiveUntil", now + activeTicks);
        HauntProtection.state(player, state);
    }
    public static boolean tryEffect(ServerPlayer player, HauntEffectKind kind) {
        // Personal Haunt alone drives every event, including world effects.
        int tier = PlayerHauntManager.getTier(player).id();
        if (!ready(player) || !kind.eligible(tier, HauntProtection.protectedPlayer(player))
                || !HauntEffects.perform(player, kind)) return false;
        completed(player, kind.activeTicks);
        return true;
    }
    private static boolean eligiblePlayer(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator() && !player.isCreative();
    }
    @SubscribeEvent public static void tick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.players().isEmpty()
                || level.getGameTime() % 20 != 0) return;
        HauntEventData data = HauntEventData.get(level);
        // A stored selection needs no new Haunt roll/tier. Any eligible player
        // in this dimension can receive it when a loaded well is within 100.
        if (data.pendingGhosts() > 0) {
            for (ServerPlayer player : level.players()) {
                if (!eligiblePlayer(player) || !ready(player) || HauntProtection.protectedPlayer(player)) continue;
                var well = HauntEffects.nearestWell(player);
                if (well != null && HauntEffects.spawnGhost(player, well)) {
                    data.deliveredGhost();
                    completed(player, 0);
                    break; // No backlog burst; at most one delivery per second/dimension.
                }
            }
        }
        if (level.getGameTime() % 600 != 0) return;
        data.prune(level.getServer().overworld().getGameTime());
        for (ServerPlayer player : level.players()) {
            if (!eligiblePlayer(player) || !ready(player)) continue;
            int tier = PlayerHauntManager.getTier(player).id();
            if (player.getRandom().nextDouble() >= HauntEffectKind.chance(tier)) continue;
            boolean protectedPlayer = HauntProtection.protectedPlayer(player);
            var choices = new ArrayList<>(Arrays.stream(HauntEffectKind.values())
                    .filter(kind -> kind.eligible(tier, protectedPlayer)).toList());
            while (!choices.isEmpty()) {
                int total = choices.stream().mapToInt(kind -> kind.weight).sum();
                int roll = player.getRandom().nextInt(total);
                HauntEffectKind chosen = choices.get(0);
                for (HauntEffectKind kind : choices) {
                    roll -= kind.weight;
                    if (roll < 0) { chosen = kind; break; }
                }
                choices.remove(chosen);
                if (tryEffect(player, chosen)) break;
            }
        }
    }
}
