package com.howierux.mortalexpansion.entity.mob;

/**
 * Implemented by Mortal Expansion mobs that can phase horizontally through
 * solid blocks. The shared mob collision hook also permits upward phasing,
 * while ordinary downward collision preserves floors.
 */
public interface HorizontalPhasingMob {
    default boolean canPhaseHorizontally() {
        return true;
    }
}
