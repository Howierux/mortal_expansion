package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;

/** Authoritative API for fine-grained personal haunt state. */
public final class PlayerHauntManager {
    private PlayerHauntManager() {}

    public static HauntTier getTier(Player player) {
        return HauntTier.fromValue(getValue(player));
    }

    public static int getValue(Player player) {
        HauntTier tier = HauntTier.fromId(player.getData(ModAttachments.PLAYER_HAUNT_TIER));
        int progress = player.getData(ModAttachments.PLAYER_HAUNT_PROGRESS);

        if (progress < 0) {
            progress = tier == HauntTier.VOID ? 0 : HauntTier.DEFAULT_PROGRESS;
            player.setData(ModAttachments.PLAYER_HAUNT_PROGRESS, progress);
        }

        return tier.id() * HauntTier.POINTS_PER_TIER + progress;
    }

    public static HauntTier setTier(Player player, HauntTier tier) {
        setValue(player, tier.midpointValue());
        return tier;
    }

    public static int setValue(Player player, int value) {
        int clamped = HauntTier.clampValue(value);
        HauntTier tier = HauntTier.fromValue(clamped);

        player.setData(ModAttachments.PLAYER_HAUNT_TIER, tier.id());
        player.setData(
                ModAttachments.PLAYER_HAUNT_PROGRESS,
                HauntTier.progressFromValue(clamped)
        );
        return clamped;
    }

    public static int addPoints(Player player, int amount) {
        return setValue(player, getValue(player) + amount);
    }

    /** Whole-tier adjustment retained for explicit tier-changing effects. */
    public static HauntTier addLevels(Player player, int amount) {
        addPoints(player, amount * HauntTier.POINTS_PER_TIER);
        return getTier(player);
    }

    public static int raisePoint(Player player) {
        return addPoints(player, 1);
    }

    public static int lowerPoint(Player player) {
        return addPoints(player, -1);
    }

    public static boolean isAtLeast(Player player, HauntTier minimum) {
        return getValue(player) >= minimum.minimumValue();
    }
}
