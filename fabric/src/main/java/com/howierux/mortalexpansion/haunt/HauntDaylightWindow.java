package com.howierux.mortalexpansion.haunt;

/** Start preparing at tick 22000, roughly 100 seconds before the next day. */
public final class HauntDaylightWindow {
    private HauntDaylightWindow() {}
    public static boolean active(boolean hasSkyLight, boolean isDay, long dayTime) {
        return hasSkyLight && (isDay || Math.floorMod(dayTime, 24000L) >= 22000L);
    }
}
