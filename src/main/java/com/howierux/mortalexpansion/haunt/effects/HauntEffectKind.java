package com.howierux.mortalexpansion.haunt.effects;

/** Minimum personal tier, selection weight, negative protection, and active ticks. */
public enum HauntEffectKind {
    HIT(1, 30, true, 0), SHADOW(2, 20, true, 40),
    SLOW(2, 6, true, 60), SHOVE(4, 3, true, 0), SHUFFLE(4, 3, true, 0),
    GOOD_POTION(2, 5, false, 100), BAD_POTION(2, 5, true, 100),
    SPIRIT(1, 8, false, 0), GHOST(3, 2, true, 0),
    FURNACE(2, 4, false, 0), PLANKS(2, 3, false, 0), COBBLE(2, 3, false, 0);
    public final int tier, weight, activeTicks;
    public final boolean negative;
    HauntEffectKind(int tier, int weight, boolean negative, int activeTicks) {
        this.tier = tier; this.weight = weight; this.negative = negative; this.activeTicks = activeTicks;
    }
    public boolean eligible(int personalTier, boolean protectedPlayer) {
        return personalTier >= tier && !(negative && protectedPlayer);
    }
    public static double chance(int personalTier) {
        return switch (personalTier) {
            case 1 -> 0.02; case 2 -> 0.05; case 3 -> 0.08;
            case 4 -> 0.12; case 5 -> 0.16; case 6 -> 0.20; default -> 0;
        };
    }
}
