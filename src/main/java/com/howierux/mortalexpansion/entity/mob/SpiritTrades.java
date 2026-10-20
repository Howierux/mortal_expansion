package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModItems;
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
        variations(offers,tier,seed);
    }
    /** Two distinct extras per tier, stable for this Spirit and retained in saved offers. */
    public static void variations(MerchantOffers offers,int tier,long seed) {
        var pool = new java.util.ArrayList<MerchantOffer>();
        int xp = new int[]{0,2,5,10,15,30}[tier];
        int stock = tier<=2 ? 12 : tier==3 ? 8 : 4;
        switch(tier) {
            case 1 -> {
                pool.add(single(Items.EMERALD,1,ModItems.GRAVE_MOSS.get(),4,stock,xp));
                pool.add(single(Items.EMERALD,2,ModItems.SPIRITWOOD_SAPLING.get(),1,stock,xp));
                pool.add(single(ModItems.SPIRITWOOD_LOG.get(),16,Items.EMERALD,1,stock,xp));
                pool.add(single(ModItems.GRAVE_MOSS.get(),16,Items.EMERALD,1,stock,xp));
            }
            case 2 -> {
                pool.add(single(Items.EMERALD,2,ModItems.GRAVESTONE.get(),8,stock,xp));
                pool.add(single(Items.EMERALD,3,ModItems.SPIRIT_TORCH.get(),2,stock,xp));
                pool.add(single(Items.EMERALD,3,ModItems.SPIRITWOOD_LOG.get(),8,stock,xp));
                pool.add(single(Items.EMERALD,4,ModItems.GRAVE.get(),1,stock,xp));
            }
            case 3 -> {
                pool.add(single(Items.EMERALD,6,ModItems.PURE_ECTOPLASM.get(),1,stock,xp));
                pool.add(single(Items.EMERALD,5,ModItems.PURIFIED_GRAVESTONE.get(),8,stock,xp));
                pool.add(single(Items.EMERALD,5,ModItems.EMF_READER.get(),1,stock,xp));
                pool.add(single(Items.EMERALD,6,ModItems.BLOODSTONE.get(),1,stock,xp));
            }
            case 4 -> {
                pool.add(service(4,ModItems.ECTOTHREAD.get(),8,ModItems.GHOST_GARB_CHESTPLATE.get(),1,stock,xp));
                pool.add(service(4,ModItems.ECTOTHREAD.get(),7,ModItems.GHOST_GARB_LEGGINGS.get(),1,stock,xp));
                pool.add(service(3,ModItems.ECTOTHREAD.get(),5,ModItems.DREAMCATCHER.get(),1,stock,xp));
                pool.add(single(Items.EMERALD,10,ModItems.PRECISE_EMF_READER.get(),1,stock,xp));
            }
            case 5 -> {
                pool.add(service(5,ModItems.PURIFIED_ECTOTHREAD.get(),5,ModItems.HAINT_GARB_HELMET.get(),1,stock,xp));
                pool.add(service(5,ModItems.PURIFIED_ECTOTHREAD.get(),4,ModItems.HAINT_GARB_BOOTS.get(),1,stock,xp));
                pool.add(service(6,ModItems.PURIFIED_ECTOTHREAD.get(),5,ModItems.PURIFIED_DREAMCATCHER.get(),1,stock,xp));
                pool.add(single(Items.EMERALD,12,ModItems.WISP_O_LANTERN.get(),1,stock,xp));
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
