package com.howierux.mortalexpansion.entity.mob;

/**
 * Implemented by Mortal Expansion mobs that can phase horizontally through
 * solid blocks while remaining subject to ordinary vertical collision.
 */
public interface HorizontalPhasingMob {
    default boolean canPhaseHorizontally() {
        return true;
    }
}
