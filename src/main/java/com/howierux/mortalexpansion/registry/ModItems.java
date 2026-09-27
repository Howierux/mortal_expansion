package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.item.SpiritwoodSaplingItem;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MortalExpansion.MOD_ID);

    public static final DeferredItem<BlockItem> SPIRITWOOD_LOG =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_LOG);

    public static final DeferredItem<BlockItem> SPIRITWOOD_PLANKS =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_PLANKS);

    public static final DeferredItem<BlockItem> SPIRITWOOD_STAIRS =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_STAIRS);

    public static final DeferredItem<BlockItem> SPIRITWOOD_SLAB =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_SLAB);

    public static final DeferredItem<BlockItem> SPIRITWOOD_FENCE =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_FENCE);

    public static final DeferredItem<BlockItem> SPIRITWOOD_FENCE_GATE =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_FENCE_GATE);

    public static final DeferredItem<BlockItem> SPIRITWOOD_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_DOOR);

    public static final DeferredItem<BlockItem> SPIRITWOOD_TRAPDOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_TRAPDOOR);

    public static final DeferredItem<BlockItem> SPIRITWOOD_PRESSURE_PLATE =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_PRESSURE_PLATE);

    public static final DeferredItem<BlockItem> SPIRITWOOD_BUTTON =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPIRITWOOD_BUTTON);

    public static final DeferredItem<BlockItem> BLOODSTONE_ORE =
            ITEMS.registerSimpleBlockItem(ModBlocks.BLOODSTONE_ORE);

    public static final DeferredItem<Item> BLOODSTONE = ITEMS.registerItem(
            "bloodstone",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<SwordItem> BLOODSTONE_SWORD = ITEMS.registerItem(
            "bloodstone_sword",
            properties -> new SwordItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(SwordItem.createAttributes(ModTiers.BLOODSTONE, 7.69F, -2.4F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<PickaxeItem> BLOODSTONE_PICKAXE = ITEMS.registerItem(
            "bloodstone_pickaxe",
            properties -> new PickaxeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(PickaxeItem.createAttributes(ModTiers.BLOODSTONE, 4.35F, -2.8F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<AxeItem> BLOODSTONE_AXE = ITEMS.registerItem(
            "bloodstone_axe",
            properties -> new AxeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(AxeItem.createAttributes(ModTiers.BLOODSTONE, 11.03F, -3.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ShovelItem> BLOODSTONE_SHOVEL = ITEMS.registerItem(
            "bloodstone_shovel",
            properties -> new ShovelItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(ShovelItem.createAttributes(ModTiers.BLOODSTONE, 5.185F, -3.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<HoeItem> BLOODSTONE_HOE = ITEMS.registerItem(
            "bloodstone_hoe",
            properties -> new HoeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(HoeItem.createAttributes(ModTiers.BLOODSTONE, -2.33F, 0.0F))
            ),
            new Item.Properties()
    );

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
