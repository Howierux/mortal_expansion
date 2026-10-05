package com.howierux.mortalexpansion.haunt.effects;

/** Minimum personal tier, selection weight, negative protection, and active ticks. */
public enum HauntEffectKind {
    HIT(1, 30, true, 0), SHADOW(2, 20, true, 40),
    SLOW(2, 6, true, 60), SHOVE(4, 3, true, 0), SHUFFLE(4, 3, true, 0),
    GOOD_POTION(2, 5, false, 100), BAD_POTION(2, 5, true, 100),
    SPIRIT(1, 8, false, 0), GHOST(3, 2, true, 0),
    FURNACE(2, 4, false, 0), PLANKS(2, 3, false, 0), COBBLE(2, 3, false, 0),
    HELPFUL_WHISPERS(1, 6, false, 0), SECOND_SIGHT(2, 4, false, 0),
    SPECTRAL_INSIGHT(2, 4, false, 0), SPIRIT_COMMISSION(3, 3, false, 0),
    HELPFUL_VISITATION(3, 3, false, 0), EMPOWERMENT(4, 4, false, 400),
    SPIRIT_RECOGNITION(5, 3, false, 0), VEIL_REVELATION(6, 2, false, 0),
    UNWANTED_COMPANY(1, 8, true, 120), FALSE_TRACES(2, 5, true, 40),
    RESTLESS_VISITATION(3, 3, true, 0), INTERFERENCE(4, 3, true, 60),
    MARKED_PRESENCE(4, 3, true, 400), PURSUER(5, 2, true, 0),
    VEIL_BREACH(6, 1, true, 100);
    public final int tier, weight, activeTicks;
    public final boolean negative;
    HauntEffectKind(int tier, int weight, boolean negative, int activeTicks) {
        this.tier = tier; this.weight = weight; this.negative = negative; this.activeTicks = activeTicks;
    }
    public boolean eligible(int personalTier, boolean protectedPlayer) {
        return implemented() && personalTier >= tier && !(negative && protectedPlayer);
    }
    public boolean implemented() {
        return switch (this) {
            // Spectral footprints now guide players; hidden inscription revealing remains a missing function.
            // missing function: optional per-player discoveries and dynamic Book entries.
            case SPECTRAL_INSIGHT -> false;
            // missing function: Spirit quest acceptance, objectives, completion and rewards.
            case SPIRIT_COMMISSION -> false;
            // missing function: spirit worker jobs, ownership and offering/inventory permissions.
            case HELPFUL_VISITATION -> false;
            // missing function: expertise-aware Spirit service menu and service definitions.
            case SPIRIT_RECOGNITION -> false;
            // missing function: hidden-site registry and willing powerful Spirit encounters.
            case VEIL_REVELATION -> false;
            default -> true;
        };
    }
    public static double chance(int personalTier) {
        return switch (personalTier) {
            case 1 -> 0.02; case 2 -> 0.05; case 3 -> 0.08;
            case 4 -> 0.12; case 5 -> 0.16; case 6 -> 0.20; default -> 0;
        };
    }
}
