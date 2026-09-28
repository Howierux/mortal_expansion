package com.howierux.mortalexpansion.haunt;

import java.util.Locale;
import java.util.Optional;

/**
 * Named haunt bands. The persistent tier remains separate from the finer
 * within-tier point value so older worlds that stored only tier ids can migrate
 * without losing their existing haunt state.
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

    /** Number of fine-grained haunt points contained in each non-Void tier. */
    public static final int POINTS_PER_TIER = 10;

    /** Existing tier-only saves migrate to the middle of their current tier. */
    public static final int DEFAULT_PROGRESS = POINTS_PER_TIER / 2;

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE =
            ELDRITCH.id * POINTS_PER_TIER + (POINTS_PER_TIER - 1);

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

    public int minimumValue() {
        return id * POINTS_PER_TIER;
    }

    public int midpointValue() {
        return this == VOID ? 0 : minimumValue() + DEFAULT_PROGRESS;
    }

    public int maximumValue() {
        return minimumValue() + POINTS_PER_TIER - 1;
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

    public static HauntTier fromValue(int value) {
        int clamped = clampValue(value);
        return fromId(clamped / POINTS_PER_TIER);
    }

    public static int progressFromValue(int value) {
        return Math.floorMod(clampValue(value), POINTS_PER_TIER);
    }

    public static int clampValue(int value) {
        return Math.max(MIN_VALUE, Math.min(MAX_VALUE, value));
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
