package com.howierux.mortalexpansion.haunt;
import com.howierux.mortalexpansion.haunt.effects.HauntEffectKind;

public final class HauntEffectPolicyTest {
    public static void main(String[] args) {
        double[] chances = {0, .02, .05, .08, .12, .16, .20};
        for (int tier = 0; tier < chances.length; tier++) {
            assert HauntEffectKind.chance(tier) == chances[tier];
            for (HauntEffectKind effect : HauntEffectKind.values()) {
                assert effect.eligible(tier, false) == (tier >= effect.tier);
                assert effect.eligible(tier, true) == (tier >= effect.tier && !effect.negative);
            }
        }
        assert HauntEffectKind.HIT.weight > HauntEffectKind.SHOVE.weight;
        assert HauntEffectKind.SHADOW.weight > HauntEffectKind.GHOST.weight;
        assert HauntEffectKind.SLOW.activeTicks == 60;
        assert HauntEffectKind.SHADOW.activeTicks == 40;
        System.out.println("Haunt effect policy checks passed");
    }
}
