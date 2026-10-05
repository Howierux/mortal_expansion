package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.*;

/** Initial resource trades; prices and stock are isolated from entity lifecycle rules. */
public final class SpiritTrades {
    private SpiritTrades() {}
    public static void populate(MerchantOffers offers) {
        offers.add(new MerchantOffer(new ItemCost(ModItems.RAW_ECTOPLASM.get(), 8), new ItemStack(Items.EMERALD), 16, 1, 0.05F));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(ModItems.ECTOTHREAD.get(), 8), 12, 1, 0.05F));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(ModItems.PURIFIED_ECTOTHREAD.get(), 8), 8, 1, 0.05F));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(ModItems.HOLY_SALT.get(), 4), 12, 1, 0.05F));
    }
}
