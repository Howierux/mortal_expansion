package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.*;
import java.util.Optional;

/** Full recipe material costs are retained; emerald fees buy missing string or assembly work. */
public final class SpiritTrades {
    public static final int CATALOG_VERSION = 2;
    private SpiritTrades() {}
    public static void populate(MerchantOffers offers, int expertise) {
        for (int tier = 1; tier <= expertise; tier++) unlock(offers, tier);
    }
    public static void unlock(MerchantOffers offers, int tier) {
        switch (tier) {
            case 1 -> {
                offers.add(single(ModItems.RAW_ECTOPLASM.get(), 8, Items.EMERALD, 1, 16, 2));
                offers.add(single(Items.EMERALD, 2, ModItems.RAW_ECTOPLASM.get(), 1, 12, 1));
            }
            case 2 -> {
                offers.add(service(2, ModItems.RAW_ECTOPLASM.get(), 1, ModItems.ECTOTHREAD.get(), 8, 12, 5));
                offers.add(single(Items.EMERALD, 2, ModItems.HOLY_SALT.get(), 1, 8, 5));
            }
            case 3 -> {
                offers.add(service(4, ModItems.PURE_ECTOPLASM.get(), 1, ModItems.PURIFIED_ECTOTHREAD.get(), 8, 8, 10));
                offers.add(single(ModItems.PURE_ECTOPLASM.get(), 4, Items.EMERALD, 1, 8, 10));
            }
            case 4 -> {
                offers.add(service(3, ModItems.ECTOTHREAD.get(), 5, ModItems.GHOST_GARB_HELMET.get(), 1, 4, 15));
                offers.add(service(3, ModItems.ECTOTHREAD.get(), 4, ModItems.GHOST_GARB_BOOTS.get(), 1, 4, 15));
            }
            case 5 -> {
                offers.add(service(6, ModItems.PURIFIED_ECTOTHREAD.get(), 8, ModItems.HAINT_GARB_CHESTPLATE.get(), 1, 2, 30));
                offers.add(service(5, ModItems.PURIFIED_ECTOTHREAD.get(), 7, ModItems.HAINT_GARB_LEGGINGS.get(), 1, 2, 30));
            }
            default -> throw new IllegalArgumentException("Unknown Spirit expertise: " + tier);
        }
    }
    private static MerchantOffer single(Item payment, int count, Item result, int output, int stock, int xp) {
        return new MerchantOffer(new ItemCost(payment, count), new ItemStack(result, output), stock, xp, 0.05F);
    }
    private static MerchantOffer service(int emeralds, Item material, int count, Item result, int output, int stock, int xp) {
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), Optional.of(new ItemCost(material, count)),
                new ItemStack(result, output), stock, xp, 0.05F);
    }
}
