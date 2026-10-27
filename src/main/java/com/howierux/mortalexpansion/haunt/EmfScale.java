package com.howierux.mortalexpansion.haunt;

/** One fixed-color bulb per named non-Void tier, shared by the item and HUD. */
public final class EmfScale {
    private EmfScale() {}
    public static final int BULBS = HauntTier.MAX_ID;
    private static final int[] COLORS = {0xFF55CC55, 0xFFB7DF45, 0xFFFFDD44, 0xFFFF9933, 0xFFFF4444, 0xFFBB66FF};
    public static String decimal(int hundredths) {
        if (hundredths < 0) return "--.--";
        int fraction = hundredths % FractionalHaunt.UNITS_PER_POINT;
        return hundredths / FractionalHaunt.UNITS_PER_POINT + "." + (fraction < 10 ? "0" : "") + fraction;
    }
    public static int litBulbs(int value) { return HauntTier.fromValue(value).id(); }
    public static int bulbColor(int bulb) { return COLORS[bulb]; }
    public static int modelLevel(int value) { return value < 70 ? litBulbs(value) : Math.min(12, value / 10); }
    public static int bulbColor(int bulb, int value) {
        return modelLevel(value) > 6 && bulb < modelLevel(value)-6
                ? (bulb < 3 ? 0xFFFF2020 : 0xFF510080) : bulbColor(bulb);
    }
    public static net.minecraft.network.chat.Component tierLabel(int value) {
        return value < 70 ? net.minecraft.network.chat.Component.translatable("haunt.mortal_expansion.tier."+HauntTier.fromValue(value).id())
                : net.minecraft.network.chat.Component.translatable("darkhold.mortal_expansion.band",value/10-6);
    }
}
