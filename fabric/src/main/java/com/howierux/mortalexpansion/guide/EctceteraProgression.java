package com.howierux.mortalexpansion.guide;

import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import net.minecraft.world.item.*;

/** The same item ID holds per-book knowledge; unmarked Fabric preview books remain starter books. */
public final class EctceteraProgression {
    private EctceteraProgression() {}
    public static final int MAX_TIER = 4;
    public static int tier(ItemStack stack) {
        return Math.clamp(stack.getOrDefault(Catalog.BOOK_TIER, 0), 0, MAX_TIER);
    }
    public static ItemStack book(int tier) {
        if (tier < 0 || tier > MAX_TIER) throw new IllegalArgumentException("Invalid book tier: " + tier);
        var stack = new ItemStack(CatalogItems.BOOK_OF_ECTCETERA);
        stack.set(Catalog.BOOK_TIER, tier);
        if(tier==4)stack.set(KnowledgeData.BOOK_SECRETS,SpectralInsight.ALL);
        return stack;
    }
    public static Item upgradeIngredient(int tier) {
        return switch (tier) {
            case 0 -> Items.BONE;
            case 1 -> CatalogItems.RAW_ECTOPLASM;
            case 2 -> CatalogItems.PURE_ECTOPLASM;
            case 3 -> CatalogItems.RITUAL_STAR;
            default -> throw new IllegalArgumentException("Complete books have no upgrade ingredient");
        };
    }
}
