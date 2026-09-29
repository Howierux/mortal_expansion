package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.item.SpiritwoodSaplingItem;
import com.howierux.mortalexpansion.item.PharaohlScepterItem;
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

    public static final DeferredItem<BlockItem> GRAVESTONE =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAVESTONE);

    public static final DeferredItem<BlockItem> GRAVE_MOSS =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAVE_MOSS);

    public static final DeferredItem<BlockItem> PURIFIED_GRAVESTONE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_GRAVESTONE);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_LOG =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_LOG);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_PLANKS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_PLANKS);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_STAIRS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_STAIRS);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_SLAB =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_SLAB);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_FENCE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_FENCE);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_FENCE_GATE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_FENCE_GATE);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_DOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_DOOR);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_TRAPDOOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_TRAPDOOR);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_PRESSURE_PLATE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> PURIFIED_SPIRITWOOD_BUTTON =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFIED_SPIRITWOOD_BUTTON);

    public static final DeferredItem<BlockItem> ECTCHANTMENT_MACHINE =
            ITEMS.registerSimpleBlockItem(ModBlocks.ECTCHANTMENT_MACHINE);

    public static final DeferredItem<BlockItem> ECTOPLASMIC_GENERATOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ECTOPLASMIC_GENERATOR);

    public static final DeferredItem<BlockItem> ENCHANTMENT_GENERATOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ENCHANTMENT_GENERATOR);

    public static final DeferredItem<BlockItem> PURIFICATION_WARD =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFICATION_WARD);

    public static final DeferredItem<BlockItem> GRAVE =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAVE);

    public static final DeferredItem<BlockItem> STONE_FENCE =
            ITEMS.registerSimpleBlockItem(ModBlocks.STONE_FENCE);

    public static final DeferredItem<BlockItem> BLOODSTONE_ORE =
            ITEMS.registerSimpleBlockItem(ModBlocks.BLOODSTONE_ORE);

    public static final DeferredItem<BlockItem> BLOODSTONE_ORE_STONE =
            ITEMS.registerSimpleBlockItem(ModBlocks.BLOODSTONE_ORE_STONE);

    public static final DeferredItem<Item> BLOODSTONE = ITEMS.registerItem(
            "bloodstone",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> RAW_ECTOPLASM = ITEMS.registerItem(
            "raw_ectoplasm",
            Item::new,
            new Item.Properties().fireResistant()
    );

    public static final DeferredItem<Item> PURE_ECTOPLASM = ITEMS.registerItem(
            "pure_ectoplasm",
            Item::new,
            new Item.Properties().fireResistant()
    );

    public static final DeferredItem<SwordItem> GRAVESTONE_SWORD = ITEMS.registerItem(
            "gravestone_sword",
            properties -> new SwordItem(
                    Tiers.STONE,
                    properties.attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.4F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<SwordItem> PURIFIED_GRAVESTONE_SWORD = ITEMS.registerItem(
            "purified_gravestone_sword",
            properties -> new SwordItem(
                    Tiers.STONE,
                    properties.attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.4F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<PharaohlScepterItem> PHARAOHL_SCEPTER = ITEMS.registerItem(
            "pharaohl_scepter",
            PharaohlScepterItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredItem<SwordItem> BLOODSTONE_SWORD = ITEMS.registerItem(
            "bloodstone_sword",
            properties -> new SwordItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(SwordItem.createAttributes(ModTiers.BLOODSTONE, 6.5F, -2.4F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<PickaxeItem> BLOODSTONE_PICKAXE = ITEMS.registerItem(
            "bloodstone_pickaxe",
            properties -> new PickaxeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(PickaxeItem.createAttributes(ModTiers.BLOODSTONE, 3.5F, -2.8F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<AxeItem> BLOODSTONE_AXE = ITEMS.registerItem(
            "bloodstone_axe",
            properties -> new AxeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(AxeItem.createAttributes(ModTiers.BLOODSTONE, 9.5F, -3.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ShovelItem> BLOODSTONE_SHOVEL = ITEMS.registerItem(
            "bloodstone_shovel",
            properties -> new ShovelItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(ShovelItem.createAttributes(ModTiers.BLOODSTONE, 4.25F, -3.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<HoeItem> BLOODSTONE_HOE = ITEMS.registerItem(
            "bloodstone_hoe",
            properties -> new HoeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(HoeItem.createAttributes(ModTiers.BLOODSTONE, -2.5F, 0.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_HELMET = ITEMS.registerItem(
            "bloodstone_helmet",
            properties -> new ArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.HELMET,
                    properties.durability(ArmorItem.Type.HELMET.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_CHESTPLATE = ITEMS.registerItem(
            "bloodstone_chestplate",
            properties -> new ArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.CHESTPLATE,
                    properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_LEGGINGS = ITEMS.registerItem(
            "bloodstone_leggings",
            properties -> new ArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.LEGGINGS,
                    properties.durability(ArmorItem.Type.LEGGINGS.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_BOOTS = ITEMS.registerItem(
            "bloodstone_boots",
            properties -> new ArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.BOOTS,
                    properties.durability(ArmorItem.Type.BOOTS.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_HELMET = ITEMS.registerItem(
            "spectralite_helmet",
            properties -> new ArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.HELMET,
                    properties.fireResistant().durability(ArmorItem.Type.HELMET.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_CHESTPLATE = ITEMS.registerItem(
            "spectralite_chestplate",
            properties -> new ArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.CHESTPLATE,
                    properties.fireResistant().durability(ArmorItem.Type.CHESTPLATE.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_LEGGINGS = ITEMS.registerItem(
            "spectralite_leggings",
            properties -> new ArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.LEGGINGS,
                    properties.fireResistant().durability(ArmorItem.Type.LEGGINGS.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_BOOTS = ITEMS.registerItem(
            "spectralite_boots",
            properties -> new ArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.BOOTS,
                    properties.fireResistant().durability(ArmorItem.Type.BOOTS.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<SwordItem> SPECTRALITE_SWORD = ITEMS.registerItem(
            "spectralite_sword",
            properties -> new SwordItem(
                    ModTiers.SPECTRALITE,
                    properties.fireResistant().attributes(SwordItem.createAttributes(ModTiers.SPECTRALITE, 8.65F, -2.4F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<PickaxeItem> SPECTRALITE_PICKAXE = ITEMS.registerItem(
            "spectralite_pickaxe",
            properties -> new PickaxeItem(
                    ModTiers.SPECTRALITE,
                    properties.fireResistant().attributes(PickaxeItem.createAttributes(ModTiers.SPECTRALITE, 4.75F, -2.8F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<AxeItem> SPECTRALITE_AXE = ITEMS.registerItem(
            "spectralite_axe",
            properties -> new AxeItem(
                    ModTiers.SPECTRALITE,
                    properties.fireResistant().attributes(AxeItem.createAttributes(ModTiers.SPECTRALITE, 12.55F, -3.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ShovelItem> SPECTRALITE_SHOVEL = ITEMS.registerItem(
            "spectralite_shovel",
            properties -> new ShovelItem(
                    ModTiers.SPECTRALITE,
                    properties.fireResistant().attributes(ShovelItem.createAttributes(ModTiers.SPECTRALITE, 5.725F, -3.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<HoeItem> SPECTRALITE_HOE = ITEMS.registerItem(
            "spectralite_hoe",
            properties -> new HoeItem(
                    ModTiers.SPECTRALITE,
                    properties.fireResistant().attributes(HoeItem.createAttributes(ModTiers.SPECTRALITE, -3.05F, 0.0F))
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
