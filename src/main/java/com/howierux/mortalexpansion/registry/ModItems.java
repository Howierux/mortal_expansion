package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.item.SpiritwoodSaplingItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MortalExpansion.MOD_ID);

    public static final DeferredItem<BlockItem> SPIRITWOOD_LOG =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_LOG);

    public static final DeferredItem<BlockItem> SPIRITWOOD_LEAVES =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_LEAVES);

    public static final DeferredItem<SpiritwoodSaplingItem> SPIRITWOOD_SAPLING = ITEMS.registerItem(
            "spiritwood_sapling",
            properties -> new SpiritwoodSaplingItem(ModBlocks.SPIRITWOOD_SAPLING.get(), properties),
            new Item.Properties()
    );

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
