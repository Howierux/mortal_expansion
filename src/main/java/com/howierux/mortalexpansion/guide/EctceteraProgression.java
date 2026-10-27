package com.howierux.mortalexpansion.guide;

import com.howierux.mortalexpansion.registry.ModDataComponents;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.world.item.*;

/** The same item ID holds per-book knowledge; unmarked legacy books keep their contents. */
public final class EctceteraProgression {
    private EctceteraProgression() {}
    public static final int MAX_TIER = 4;
    public static int tier(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(ModDataComponents.ECTCETERA_TIER.get(), 3), 0, MAX_TIER);
    }
    public static ItemStack book(int tier) {
        if (tier < 0 || tier > MAX_TIER) throw new IllegalArgumentException("Invalid book tier: " + tier);
        var stack = new ItemStack(ModItems.BOOK_OF_ECTCETERA.get());
        stack.set(ModDataComponents.ECTCETERA_TIER.get(), tier);
        if(tier==4)stack.set(ModDataComponents.ECTCETERA_SECRETS.get(),SpectralInsight.ALL);
        return stack;
    }
    public static Item upgradeIngredient(int tier) {
        return switch (tier) {
            case 0 -> Items.BONE;
            case 1 -> ModItems.RAW_ECTOPLASM.get();
            case 2 -> ModItems.PURE_ECTOPLASM.get();
            case 3 -> ModItems.RITUAL_STAR.get();
            default -> throw new IllegalArgumentException("Complete books have no upgrade ingredient");
        };
    }
}
