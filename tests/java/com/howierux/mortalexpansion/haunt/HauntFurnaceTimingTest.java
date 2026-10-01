package com.howierux.mortalexpansion.haunt;

public final class HauntFurnaceTimingTest {
    public static void main(String[] args) {
        assert HauntFurnaceTiming.cookTicks(200, HauntTier.VOID) == 200;
        assert HauntFurnaceTiming.cookTicks(200, HauntTier.ELDRITCH) == 20;
        assert HauntFurnaceTiming.cookTicks(1, HauntTier.ELDRITCH) == 1;
        int previous = 201;
        for (HauntTier tier : HauntTier.values()) {
            int duration = HauntFurnaceTiming.cookTicks(200, tier);
            assert duration < previous : "Every higher tier speeds cooking";
            previous = duration;
            assert !HauntFurnaceTiming.preserveBurn(false, tier, false);
            assert !HauntFurnaceTiming.preserveBurn(false, tier, true);
            assert HauntFurnaceTiming.preserveBurn(true, tier, false) : "Idle Haint retains heat";
            assert HauntFurnaceTiming.preserveBurn(true, tier, true)
                    == (tier.id() >= HauntTier.DEMONIC.id());
        }
        assert HauntFurnaceTiming.rescaleProgress(100, 200, 20) == 10;
        assert HauntFurnaceTiming.rescaleProgress(10, 20, 200) == 100;
        assert HauntFurnaceTiming.rescaleProgress(199, 200, 1) == 0;
        // A single remaining fuel tick must survive any length idle pause or Demonic cooking.
        for (boolean cooked : new boolean[]{false, true}) {
            int burn = 1;
            for (int tick = 0; tick < 10000; tick++) {
                if (!HauntFurnaceTiming.preserveBurn(true, HauntTier.DEMONIC, cooked)) burn--;
            }
            assert burn == 1;
        }
        int burn = 1600;
        for (int tick = 0; tick < 200; tick++) {
            if (!HauntFurnaceTiming.preserveBurn(true, HauntTier.DEAD_RISING, true)) burn--;
        }
        assert burn == 1400 : "Below Demonic, active cooking uses real burn ticks";
        System.out.println("Haunt furnace timing checks passed");
    }
}
