package com.howierux.mortalexpansion.haunt;

import java.util.Locale;
import java.util.Optional;

/**
 * Persistent haunt states for ordinary chunks.
 *
 * THE_END is deliberately excluded. In the design brief it is a crisis event,
 * not a normal persistent chunk tier.
 */
public enum HauntTier {
    VOID(0, "Void"),
    NATURAL(1, "Natural"),
    SUPERNATURAL(2, "Supernatural"),
    DEAD_RISING(3, "Dead Rising"),
    DEMONIC(4, "Demonic"),
    ABOMINABLE(5, "Abominable"),
    ELDRITCH(6, "Eldritch");

    public static final int MIN_ID = VOID.id;
    public static final int MAX_ID = ELDRITCH.id;

    private final int id;
    private final String displayName;

    HauntTier(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public HauntTier next() {
        return fromId(id + 1);
    }

    public HauntTier previous() {
        return fromId(id - 1);
    }

    public static HauntTier fromId(int id) {
        int clamped = Math.max(MIN_ID, Math.min(MAX_ID, id));
        for (HauntTier tier : values()) {
            if (tier.id == clamped) {
                return tier;
            }
        }
        return VOID;
    }

    public static Optional<HauntTier> parse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String normalized = value.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');

        try {
            return Optional.of(HauntTier.valueOf(normalized));
        } catch (IllegalArgumentException ignored) {
            try {
                return Optional.of(fromId(Integer.parseInt(value)));
            } catch (NumberFormatException ignoredNumber) {
                return Optional.empty();
            }
        }
    }
}
