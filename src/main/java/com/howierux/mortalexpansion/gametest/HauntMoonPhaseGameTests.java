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
                    float expected = value == 0 ? 0 : 0.075F * (value / 10 + 1) * weights[phase];
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
}
