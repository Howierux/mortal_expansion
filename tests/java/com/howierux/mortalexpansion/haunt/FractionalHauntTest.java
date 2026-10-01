package com.howierux.mortalexpansion.haunt;

public final class FractionalHauntTest {
    public static void main(String[] args) {
        int value = 0;
        for (int kills = 1; kills <= 1000; kills++) {
            value = FractionalHaunt.add(value, FractionalHaunt.MOB_KILL_INCREASE);
            assert value == kills : "Each kill contributes exactly 0.01";
            if (kills == 99) assert value / 100 == 0 && value % 100 == 99;
            if (kills == 100) assert value / 100 == 1 && value % 100 == 0;
        }
        assert HauntTier.fromValue(value / 100) == HauntTier.NATURAL;
        assert FractionalHaunt.add(125, 100) == 225 : "Whole-point changes retain fractions";
        assert FractionalHaunt.add(125, -100) == 25;
        assert FractionalHaunt.add(25, -100) == 0 : "Purification clamps to zero";
        assert FractionalHaunt.add(FractionalHaunt.MAX_UNITS - 1, 1) == FractionalHaunt.MAX_UNITS;
        assert FractionalHaunt.add(FractionalHaunt.MAX_UNITS, 1) == FractionalHaunt.MAX_UNITS;
        assert FractionalHaunt.add(20, Long.MAX_VALUE) == FractionalHaunt.MAX_UNITS;
        assert FractionalHaunt.add(20, Long.MIN_VALUE) == 0;
        System.out.println("Fractional Haunt checks passed");
    }
}
