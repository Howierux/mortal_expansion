package com.howierux.mortalexpansion.haunt;

/** Shared furnace timing, expressed in real server ticks. */
public final class HauntFurnaceTiming {
    private HauntFurnaceTiming() {}

    /** Linear tier scaling: Void 1x, Natural 2.5x, ... Eldritch 10x. */
    public static double speed(HauntTier tier) {
        return 1.0D + 9.0D * tier.id() / HauntTier.MAX_ID;
    }

    public static int cookTicks(int recipeTicks, HauntTier tier) {
        return Math.max(1, (int)Math.ceil(recipeTicks / speed(tier)));
    }

    public static int rescaleProgress(int progress, int oldTotal, int newTotal) {
        if (oldTotal <= 0 || progress <= 0) return 0;
        return Math.min(newTotal - 1, (int)((long)progress * newTotal / oldTotal));
    }

    public static boolean freeBurn(boolean haint, HauntTier tier) {
        return haint && tier.id() >= HauntTier.DEMONIC.id();
    }

    public static boolean preserveBurn(boolean haint, HauntTier tier, boolean cooked) {
        return haint && (freeBurn(true, tier) || !cooked);
    }
}
