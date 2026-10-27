package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;

/** Authoritative API for fine-grained personal haunt state. */
public final class PlayerHauntManager {
    private PlayerHauntManager() {}
    public static final int EXTENDED_MAX=20_000_000,MAX_PERSONAL_UNITS=EXTENDED_MAX*100;
    private static int limit(Player player){return player.level() instanceof net.minecraft.server.level.ServerLevel level && com.howierux.mortalexpansion.guide.DarkholdWorldData.get(level).awakened()?EXTENDED_MAX:HauntTier.MAX_VALUE;}

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

        return tier.id() * HauntTier.POINTS_PER_TIER + progress + player.getData(ModAttachments.PLAYER_HAUNT_EXCESS);
    }

    public static HauntTier setTier(Player player, HauntTier tier) {
        setValue(player, tier.midpointValue());
        return tier;
    }

    public static int setValue(Player player, int value) {
        player.setData(ModAttachments.PLAYER_HAUNT_FRACTION, 0);
        return storeWholeValue(player, value);
    }

    private static int storeWholeValue(Player player, int value) {
        int clamped = Math.clamp(value,0,limit(player));
        int base=Math.min(HauntTier.MAX_VALUE,clamped);
        player.setData(ModAttachments.PLAYER_HAUNT_EXCESS,clamped-base);
        HauntTier tier = HauntTier.fromValue(base);

        player.setData(ModAttachments.PLAYER_HAUNT_TIER, tier.id());
        player.setData(
                ModAttachments.PLAYER_HAUNT_PROGRESS,
                HauntTier.progressFromValue(base)
        );
        return clamped;
    }

    public static int addPoints(Player player, int amount) {
        addHundredths(player, (long)amount * FractionalHaunt.UNITS_PER_POINT);
        return getValue(player);
    }

    /** Decimal personal value for display; named tier rules still use whole points. */
    public static double getPreciseValue(Player player) {
        return (double)getHundredths(player) / FractionalHaunt.UNITS_PER_POINT;
    }

    public static int getHundredths(Player player) {
        return getValue(player) * FractionalHaunt.UNITS_PER_POINT
                + player.getData(ModAttachments.PLAYER_HAUNT_FRACTION);
    }

    /** Fixed-point arithmetic avoids decimal drift and preserves fractions during whole-point changes. */
    public static void addHundredths(Player player, long amount) {
        long current=getHundredths(player),max=(long)limit(player)*100;
        int result=(int)(amount>=max-current?max:amount<=-current?0:current+amount);
        storeWholeValue(player, result / FractionalHaunt.UNITS_PER_POINT);
        player.setData(ModAttachments.PLAYER_HAUNT_FRACTION,
                result % FractionalHaunt.UNITS_PER_POINT);
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
