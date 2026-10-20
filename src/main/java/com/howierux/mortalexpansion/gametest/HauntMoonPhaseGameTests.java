package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.HauntSimulationHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HauntMoonPhaseGameTests {
    @GameTest(template="deferred_items_test")
    public static void moon_cycle_weights_spread_without_enabling_zero_haunt(GameTestHelper h) {
        var level = h.getLevel();
        long oldTime = level.getDayTime();
        float[] weights = {0.5F, 0.75F, 1.0F, 1.25F, 1.5F, 1.25F, 1.0F, 0.75F};
        try {
            for (int day = 0; day < 16; day++) {
                level.setDayTime(day * 24000L);
                int phase = level.getMoonPhase();
                h.assertTrue(phase == day % 8, "Native moon phase repeats every eight days, full at zero and new at four");
                for (int value = 0; value <= 69; value++) {
                    float expected = value == 0 ? 0 : Math.min(1.0F, (2.0F / 3.0F) * weights[phase]);
                    float chance = HauntSimulationHandler.spreadChance(value, phase);
                    h.assertTrue(Math.abs(chance - expected) < 0.00001F && chance >= 0 && chance <= 1,
                            "All Haunt bands use the expected moon weight and zero never spreads");
                }
            }
        } finally {
            level.setDayTime(oldTime);
        }
        h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spread_checks_three_times_per_day_without_time_jump_catchup(GameTestHelper h) {
        int checks = 0;
        for (long tick = 1; tick <= 24000; tick++)
            if (HauntSimulationHandler.spreadCheckDue(tick - 1, tick)) {
                checks++;
                h.assertTrue(tick % 8000 == 0, "Checks occur at 8000, 16000 and 24000 world ticks");
            }
        h.assertTrue(checks == 3, "Exactly three checks per full Minecraft day");
        h.assertFalse(HauntSimulationHandler.spreadCheckDue(8000,8000), "Frozen clock does not repeat checks");
        h.assertFalse(HauntSimulationHandler.spreadCheckDue(24000,0), "Backward clock changes do not spread");
        h.assertTrue(HauntSimulationHandler.spreadCheckDue(13000,24000), "Sleeping across intervals schedules one pass");
        h.assertTrue(HauntSimulationHandler.spreadCheckDue(0,240000), "Forward time jump schedules one pass, not a catch-up loop");
        h.assertFalse(HauntSimulationHandler.spreadCheckDue(240000,240001), "Jumped intervals cannot replay on the next tick");
        for (int value=1; value<=69; value++)
            h.assertTrue(HauntSimulationHandler.spreadChance(value,4)==1.0F, "Every nonzero tier guarantees the new-moon roll");
        h.succeed();
    }

}
