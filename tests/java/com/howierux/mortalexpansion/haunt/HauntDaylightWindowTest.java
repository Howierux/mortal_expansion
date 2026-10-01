package com.howierux.mortalexpansion.haunt;

public final class HauntDaylightWindowTest {
    public static void main(String[] args) {
        assert !HauntDaylightWindow.active(true, false, 21999);
        assert HauntDaylightWindow.active(true, false, 22000);
        assert HauntDaylightWindow.active(true, false, 23999);
        assert HauntDaylightWindow.active(true, true, 0);
        assert HauntDaylightWindow.active(true, true, 6000);
        assert !HauntDaylightWindow.active(true, false, 18000);
        assert !HauntDaylightWindow.active(false, true, 6000);
        assert !HauntDaylightWindow.active(false, false, 23000);
        assert HauntDaylightWindow.active(true, false, 24000L * 300 + 22000);
        assert !HauntDaylightWindow.active(true, false, 24000L * 300 + 18000);
        System.out.println("Haunt daylight boundary checks passed");
    }
}
