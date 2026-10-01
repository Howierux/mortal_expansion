package com.howierux.mortalexpansion.haunt;

/** Personal Haunt in exact hundredths, using the existing 0-69 point cap. */
public final class FractionalHaunt {
    public static final int UNITS_PER_POINT = 100;
    public static final int MOB_KILL_INCREASE = 1;
    public static final int MAX_UNITS = HauntTier.MAX_VALUE * UNITS_PER_POINT;
    private FractionalHaunt() {}

    public static int add(int current, long amount) {
        int clamped = Math.max(0, Math.min(MAX_UNITS, current));
        if (amount >= MAX_UNITS - clamped) return MAX_UNITS;
        if (amount <= -clamped) return 0;
        return clamped + (int)amount;
    }
}
