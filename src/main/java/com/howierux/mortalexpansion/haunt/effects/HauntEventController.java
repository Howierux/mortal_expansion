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
    public static boolean ready(ServerPlayer player) { return ready(player, true); }
    public static boolean ready(ServerPlayer player, boolean negative) {
        var state = HauntProtection.state(player);
        long now = HauntProtection.now(player);
        return state.getLong(negative ? "CooldownUntil" : "BenefitCooldownUntil") <= now
                && state.getLong(negative ? "ActiveUntil" : "BenefitActiveUntil") <= now;
    }
    public static void completed(ServerPlayer player, int activeTicks) { completed(player, activeTicks, true); }
    private static void completed(ServerPlayer player, int activeTicks, boolean negative) {
        var state = HauntProtection.state(player);
        long now = HauntProtection.now(player);
        state.putLong(negative ? "CooldownUntil" : "BenefitCooldownUntil", now + 1200);
        state.putLong(negative ? "ActiveUntil" : "BenefitActiveUntil", now + activeTicks);
        HauntProtection.state(player, state);
    }
    public static boolean tryEffect(ServerPlayer player, HauntEffectKind kind) {
        // Personal Haunt alone drives every event, including world effects.
        int tier = PlayerHauntManager.getTier(player).id();
        if (!ready(player, kind.negative) || !kind.eligible(tier, HauntProtection.protectedPlayer(player))) return false;
        long pendingBefore = HauntEventData.get(player.serverLevel()).pendingGhosts();
        if (!HauntEffects.perform(player, kind)) return false;
        boolean held = kind == HauntEffectKind.GHOST
                && HauntEventData.get(player.serverLevel()).pendingGhosts() > pendingBefore;
        // A held selection is not an active/completed event. Start its cooldown
        // on delivery, so entering range can release it immediately.
        if (!held) {
            completed(player, kind.activeTicks, kind.negative);
            hint(player, kind);
        }
        return true;
    }
    /** Research buys an immediate Insight; preserve eligibility but bypass the natural event cooldown. */
    public static boolean researchInsight(ServerPlayer player) {
        if(!player.isAlive() || player.isSpectator()
                || !HauntEffectKind.SPECTRAL_INSIGHT.eligible(PlayerHauntManager.getTier(player).id(), false)
                || !HauntEffects.perform(player, HauntEffectKind.SPECTRAL_INSIGHT))return false;
        completed(player, HauntEffectKind.SPECTRAL_INSIGHT.activeTicks, false);
        return true;
    }
    public static boolean deliverPendingTo(ServerPlayer player) {
        HauntEventData data = HauntEventData.get(player.serverLevel());
        if (data.pendingGhosts() == 0 || !ready(player) || HauntProtection.protectedPlayer(player)
                || !HauntEffects.spawnGhostNear(player)) return false;
        data.deliveredGhost();
        completed(player, 0);
        hint(player, HauntEffectKind.GHOST);
        return true;
    }
    private static boolean eligiblePlayer(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator() && !player.isCreative();
    }
    @SubscribeEvent public static void tick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.players().isEmpty()
                || level.getGameTime() % 20 != 0) return;
        // Retry a stored full charge when a carried book or personal Haunt becomes eligible.
        // This also handles full research restored from older saves without spending it on failure.
        for(ServerPlayer player : level.players())
            com.howierux.mortalexpansion.haunt.MortalityResearch.complete(player);
        HauntEventData data = HauntEventData.get(level);
        // A stored selection needs no new Haunt roll/tier. Any eligible player
        // in this dimension can receive it when a loaded well is within 100.
        if (data.pendingGhosts() > 0) {
            for (ServerPlayer player : level.players()) {
                if (!eligiblePlayer(player)) continue;
                if (deliverPendingTo(player)) {
                    break; // No backlog burst; at most one delivery per second/dimension.
                }
            }
        }
        if (level.getGameTime() % 600 != 0) return;
        data.prune(level.getServer().overworld().getGameTime());
        for (ServerPlayer player : level.players()) {
            if (!eligiblePlayer(player)) continue;
            rollPool(player, false);
            rollPool(player, true);
        }
    }
    public static java.util.List<HauntEffectKind> choices(int tier, boolean protectedPlayer, boolean negative) {
        return Arrays.stream(HauntEffectKind.values())
                .filter(kind -> kind.negative == negative && kind.eligible(tier, protectedPlayer)).toList();
    }
    public static void rollPool(ServerPlayer player, boolean negative) {
        if (!ready(player, negative)) return;
        int tier = PlayerHauntManager.getTier(player).id();
        boolean garb = negative && HauntProtection.fullGarb(player);
        if (negative && HauntProtection.protectedPlayer(player) && !garb) return;
        if (player.getRandom().nextDouble() >= HauntEffectKind.chance(tier)) return;
        var choices = new ArrayList<>(choices(tier, !garb && HauntProtection.protectedPlayer(player), negative));
        boolean researchApplicable=com.howierux.mortalexpansion.haunt.MortalityResearch.bonus(player)>0
                && ready(player,false) && com.howierux.mortalexpansion.haunt.MortalityResearch.ready(player);
        boolean firstSelection=true;
        while (!choices.isEmpty()) {
            int roll = player.getRandom().nextInt(choices.stream().mapToInt(kind -> kind.weight).sum());
            HauntEffectKind chosen = choices.getFirst();
            for (var kind : choices) {
                roll -= kind.weight;
                if (roll < 0) { chosen = kind; break; }
            }
            if(firstSelection && researchApplicable)chosen=com.howierux.mortalexpansion.haunt.MortalityResearch.biasedChoice(player,choices);
            firstSelection=false;
            choices.remove(chosen);
            if (garb && chosen.negative) {
                // Roll and select normally, but never execute a prevented risk or defer a ghost.
                // Use the existing risk cooldown so feedback does not spam or delay benefits.
                completed(player, 0, true);
                if(researchApplicable)com.howierux.mortalexpansion.haunt.MortalityResearch.spent(player);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.mortal_expansion.haunt.garb_prevented"), true);
                return;
            }
            if (tryEffect(player, chosen)){if(researchApplicable)com.howierux.mortalexpansion.haunt.MortalityResearch.spent(player);return;}
        }
    }
    public static void hint(ServerPlayer player, HauntEffectKind kind) {
        if(kind==HauntEffectKind.SPECTRAL_INSIGHT)return; // Specific book whisper already sent on successful revelation.
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                "message.mortal_expansion.haunt." + kind.name().toLowerCase(java.util.Locale.ROOT)), false);
    }
}

