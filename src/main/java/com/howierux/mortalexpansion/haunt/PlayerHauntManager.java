package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;

/**
 * Authoritative API for a player's personal haunt.
 *
 * Player haunt is deliberately separate from chunk haunt. Future effects can
 * compare or combine the two without one value automatically overwriting the other.
 */
public final class PlayerHauntManager {
    private PlayerHauntManager() {}

    public static HauntTier getTier(Player player) {
        return HauntTier.fromId(player.getData(ModAttachments.PLAYER_HAUNT_TIER));
    }

    public static HauntTier setTier(Player player, HauntTier tier) {
        player.setData(ModAttachments.PLAYER_HAUNT_TIER, tier.id());
        return tier;
    }

    public static HauntTier addLevels(Player player, int amount) {
        HauntTier current = getTier(player);
        return setTier(player, HauntTier.fromId(current.id() + amount));
    }

    public static HauntTier raise(Player player) {
        return addLevels(player, 1);
    }

    public static HauntTier lower(Player player) {
        return addLevels(player, -1);
    }

    public static boolean isAtLeast(Player player, HauntTier minimum) {
        return getTier(player).id() >= minimum.id();
    }
}
