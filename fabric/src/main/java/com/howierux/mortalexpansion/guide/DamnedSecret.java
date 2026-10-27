package com.howierux.mortalexpansion.guide;

import net.minecraft.world.entity.player.Player;


/** Reverse order of Dante's terraces. Bit zero remains the legacy Demon revelation. */
public enum DamnedSecret {
    LUST, GLUTTONY, GREED, SLOTH, WRATH, ENVY, PRIDE;
    public int bit() { return 2 << ordinal(); }
    public int threshold() { return 70 + ordinal() * 10; }
    public String key() { return "sin_" + name().toLowerCase(java.util.Locale.ROOT); }
    public boolean unlocked(Player player) { return player != null && (((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)player).getAttachedOrCreate(DarkholdProgression.KNOWN) & bit()) != 0; }
    public static int revealedAt(int haunt) {
        int mask = haunt >= 70 ? 1 : 0;
        for (var secret : values()) if (haunt >= secret.threshold()) mask |= secret.bit();
        return mask;
    }
    public static DamnedSecret forKey(String key) {
        for (var secret : values()) if (secret.key().equals(key)) return secret;
        return null;
    }
}
