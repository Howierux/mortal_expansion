package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.catalog.CatalogItems;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.*;
import java.util.Optional;

/** Full recipe material costs are retained; emerald fees buy missing string or assembly work. */
public final class SpiritTrades {
    public static final int CATALOG_VERSION = 3;
    private SpiritTrades() {}
    public static void populate(MerchantOffers offers, int expertise) {
        populate(offers,expertise,0L);
    }
    public static void populate(MerchantOffers offers, int expertise, long seed) {
        for (int tier=1;tier<=expertise;tier++) unlock(offers,tier,seed);
    }
    public static void unlock(MerchantOffers offers, int tier) { unlock(offers,tier,0L); }
    public static void unlock(MerchantOffers offers, int tier, long seed) {
        switch (tier) {
            case 1 -> {
                offers.add(single(CatalogItems.RAW_ECTOPLASM, 8, Items.EMERALD, 1, 16, 2));
                offers.add(single(Items.EMERALD, 2, CatalogItems.RAW_ECTOPLASM, 1, 12, 1));
            }
            case 2 -> {
                offers.add(service(2, CatalogItems.RAW_ECTOPLASM, 1, CatalogItems.ECTOTHREAD, 8, 12, 5));
                offers.add(single(Items.EMERALD, 2, CatalogItems.HOLY_SALT, 1, 8, 5));
            }
            case 3 -> {
                offers.add(service(4, CatalogItems.PURE_ECTOPLASM, 1, CatalogItems.PURIFIED_ECTOTHREAD, 8, 8, 10));
                offers.add(single(CatalogItems.PURE_ECTOPLASM, 4, Items.EMERALD, 1, 8, 10));
            }
            case 4 -> {
                offers.add(service(3, CatalogItems.ECTOTHREAD, 5, CatalogItems.GHOST_GARB_HELMET, 1, 4, 15));
                offers.add(service(3, CatalogItems.ECTOTHREAD, 4, CatalogItems.GHOST_GARB_BOOTS, 1, 4, 15));
            }
            case 5 -> {
                offers.add(service(6, CatalogItems.PURIFIED_ECTOTHREAD, 8, CatalogItems.HAINT_GARB_CHESTPLATE, 1, 2, 30));
                offers.add(service(5, CatalogItems.PURIFIED_ECTOTHREAD, 7, CatalogItems.HAINT_GARB_LEGGINGS, 1, 2, 30));
            }
            default -> throw new IllegalArgumentException("Unknown Spirit expertise: " + tier);
        }
        variations(offers,tier,seed);
    }
    /** Two distinct extras per tier, stable for this Spirit and retained in saved offers. */
    public static void variations(MerchantOffers offers,int tier,long seed) {
        var pool = new java.util.ArrayList<MerchantOffer>();
        int xp = new int[]{0,2,5,10,15,30}[tier];
        int stock = tier<=2 ? 12 : tier==3 ? 8 : 4;
        switch(tier) {
            case 1 -> {
                pool.add(single(Items.EMERALD,1,CatalogItems.GRAVE_MOSS,4,stock,xp));
                pool.add(single(Items.EMERALD,2,CatalogItems.SPIRITWOOD_SAPLING,1,stock,xp));
                pool.add(single(CatalogItems.SPIRITWOOD_LOG,16,Items.EMERALD,1,stock,xp));
                pool.add(single(CatalogItems.GRAVE_MOSS,16,Items.EMERALD,1,stock,xp));
            }
            case 2 -> {
                pool.add(single(Items.EMERALD,2,CatalogItems.GRAVESTONE,8,stock,xp));
                pool.add(single(Items.EMERALD,3,CatalogItems.SPIRIT_TORCH,2,stock,xp));
                pool.add(single(Items.EMERALD,3,CatalogItems.SPIRITWOOD_LOG,8,stock,xp));
                pool.add(single(Items.EMERALD,4,CatalogItems.GRAVE,1,stock,xp));
            }
            case 3 -> {
                pool.add(single(Items.EMERALD,6,CatalogItems.PURE_ECTOPLASM,1,stock,xp));
                pool.add(single(Items.EMERALD,5,CatalogItems.PURIFIED_GRAVESTONE,8,stock,xp));
                pool.add(single(Items.EMERALD,5,CatalogItems.EMF_READER,1,stock,xp));
                pool.add(single(Items.EMERALD,6,CatalogItems.BLOODSTONE,1,stock,xp));
            }
            case 4 -> {
                pool.add(service(4,CatalogItems.ECTOTHREAD,8,CatalogItems.GHOST_GARB_CHESTPLATE,1,stock,xp));
                pool.add(service(4,CatalogItems.ECTOTHREAD,7,CatalogItems.GHOST_GARB_LEGGINGS,1,stock,xp));
                pool.add(service(3,CatalogItems.ECTOTHREAD,5,CatalogItems.DREAMCATCHER,1,stock,xp));
                pool.add(single(Items.EMERALD,10,CatalogItems.PRECISE_EMF_READER,1,stock,xp));
            }
            case 5 -> {
                pool.add(service(5,CatalogItems.PURIFIED_ECTOTHREAD,5,CatalogItems.HAINT_GARB_HELMET,1,stock,xp));
                pool.add(service(5,CatalogItems.PURIFIED_ECTOTHREAD,4,CatalogItems.HAINT_GARB_BOOTS,1,stock,xp));
                pool.add(service(6,CatalogItems.PURIFIED_ECTOTHREAD,5,CatalogItems.PURIFIED_DREAMCATCHER,1,stock,xp));
                pool.add(single(Items.EMERALD,12,CatalogItems.WISP_O_LANTERN,1,stock,xp));
            }
            default -> throw new IllegalArgumentException("Unknown Spirit expertise: "+tier);
        }
        var random=net.minecraft.util.RandomSource.create(seed ^ (tier*0x9E3779B97F4A7C15L));
        for(int i=0;i<2;i++) offers.add(pool.remove(random.nextInt(pool.size())));
    }
    private static MerchantOffer single(Item payment, int count, Item result, int output, int stock, int xp) {
        return new MerchantOffer(new ItemCost(payment, count), new ItemStack(result, output), stock, xp, 0.05F);
    }
    private static MerchantOffer service(int emeralds, Item material, int count, Item result, int output, int stock, int xp) {
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), Optional.of(new ItemCost(material, count)),
                new ItemStack(result, output), stock, xp, 0.05F);
    }
}
