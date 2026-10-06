package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.item.SpiritwoodSaplingItem;
import com.howierux.mortalexpansion.item.BookOfEctceteraItem;
import com.howierux.mortalexpansion.item.PharaohlScepterItem;
import com.howierux.mortalexpansion.item.SpectralArmorItem;
import com.howierux.mortalexpansion.item.GarbArmorItem;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MortalExpansion.MOD_ID);

    public static final DeferredItem<com.howierux.mortalexpansion.item.EmfReaderItem> PRECISE_EMF_READER =
            ITEMS.registerItem("precise_emf_reader", com.howierux.mortalexpansion.item.EmfReaderItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<com.howierux.mortalexpansion.item.EmfReaderItem> EMF_READER =
            ITEMS.registerItem("emf_reader", com.howierux.mortalexpansion.item.EmfReaderItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ECTOTHREAD = ITEMS.registerSimpleItem("ectothread");
    public static final DeferredItem<ArmorItem> GHOST_GARB_HELMET = garb("ghost_garb_helmet", ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> GHOST_GARB_CHESTPLATE = garb("ghost_garb_chestplate", ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> GHOST_GARB_LEGGINGS = garb("ghost_garb_leggings", ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> GHOST_GARB_BOOTS = garb("ghost_garb_boots", ArmorItem.Type.BOOTS);
    public static final DeferredItem<Item> PURIFIED_ECTOTHREAD = ITEMS.registerSimpleItem("purified_ectothread");
    public static final DeferredItem<ArmorItem> HAINT_GARB_HELMET = garb("haint_garb_helmet", ArmorItem.Type.HELMET, ModArmorMaterials.HAINT_GARB, 0x3AB3DA);
    public static final DeferredItem<ArmorItem> HAINT_GARB_CHESTPLATE = garb("haint_garb_chestplate", ArmorItem.Type.CHESTPLATE, ModArmorMaterials.HAINT_GARB, 0x3AB3DA);
    public static final DeferredItem<ArmorItem> HAINT_GARB_LEGGINGS = garb("haint_garb_leggings", ArmorItem.Type.LEGGINGS, ModArmorMaterials.HAINT_GARB, 0x3AB3DA);
    public static final DeferredItem<ArmorItem> HAINT_GARB_BOOTS = garb("haint_garb_boots", ArmorItem.Type.BOOTS, ModArmorMaterials.HAINT_GARB, 0x3AB3DA);
    public static final DeferredItem<ArmorItem> SPECTRALITE_HAINT_HELMET = spectralHaint("spectralite_haint_helmet", ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SPECTRALITE_HAINT_CHESTPLATE = spectralHaint("spectralite_haint_chestplate", ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> SPECTRALITE_HAINT_LEGGINGS = spectralHaint("spectralite_haint_leggings", ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> SPECTRALITE_HAINT_BOOTS = spectralHaint("spectralite_haint_boots", ArmorItem.Type.BOOTS);
    private static DeferredItem<ArmorItem> spectralHaint(String id, ArmorItem.Type type) {
        return ITEMS.registerItem(id, properties -> new GarbArmorItem(ModArmorMaterials.SPECTRALITE, type,
                properties.fireResistant().durability(type.getDurability(65))));
    }
    private static DeferredItem<ArmorItem> garb(String id, ArmorItem.Type type) {
        return garb(id, type, ModArmorMaterials.GHOST_GARB, 0x80C71F);
    }
    private static DeferredItem<ArmorItem> garb(String id, ArmorItem.Type type,
            net.minecraft.core.Holder<ArmorMaterial> material, int color) {
        return ITEMS.registerItem(id, properties -> new GarbArmorItem(material, type,
                properties.durability(type.getDurability(15)).component(net.minecraft.core.component.DataComponents.DYED_COLOR,
                        new net.minecraft.world.item.component.DyedItemColor(color, false))));
    }

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

    public static final DeferredItem<BlockItem> PURIFICATION_STATION =
            ITEMS.registerSimpleBlockItem(ModBlocks.PURIFICATION_STATION);

    public static final DeferredItem<BucketItem> LIQUID_ECTOPLASM_BUCKET = ITEMS.registerItem(
            "liquid_ectoplasm_bucket",
            properties -> new BucketItem(
                    ModFluids.LIQUID_ECTOPLASM.get(),
                    properties.craftRemainder(Items.BUCKET).stacksTo(1)
            ),
            new Item.Properties()
    );

    public static final DeferredItem<BucketItem> HOLY_WATER_BUCKET = ITEMS.registerItem(
            "holy_water_bucket",
            properties -> new BucketItem(
                    ModFluids.HOLY_WATER.get(),
                    properties.craftRemainder(Items.BUCKET).stacksTo(1)
            ),
            new Item.Properties()
    );

    public static final DeferredItem<BucketItem> PURIFIED_ECTOPLASM_BUCKET = ITEMS.registerItem(
            "purified_ectoplasm_bucket",
            properties -> new BucketItem(
                    ModFluids.PURIFIED_ECTOPLASM.get(),
                    properties.craftRemainder(Items.BUCKET).stacksTo(1)
            ),
            new Item.Properties()
    );

    public static final DeferredItem<StandingAndWallBlockItem> SPIRIT_TORCH = ITEMS.registerItem(
            "spirit_torch", properties -> new StandingAndWallBlockItem(
                    ModBlocks.SPIRIT_TORCH.get(), ModBlocks.SPIRIT_WALL_TORCH.get(),
                    properties, net.minecraft.core.Direction.DOWN), new Item.Properties());

    public static final DeferredItem<BlockItem> HOLY_SALT =
            ITEMS.registerSimpleBlockItem(ModBlocks.HOLY_SALT);

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
            properties -> new com.howierux.mortalexpansion.item.HauntedHoeItem(
                    ModTiers.BLOODSTONE,
                    properties.attributes(HoeItem.createAttributes(ModTiers.BLOODSTONE, -2.5F, 0.0F))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_HELMET = ITEMS.registerItem(
            "bloodstone_helmet",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.HELMET,
                    properties.durability(ArmorItem.Type.HELMET.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_CHESTPLATE = ITEMS.registerItem(
            "bloodstone_chestplate",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.CHESTPLATE,
                    properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_LEGGINGS = ITEMS.registerItem(
            "bloodstone_leggings",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.LEGGINGS,
                    properties.durability(ArmorItem.Type.LEGGINGS.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> BLOODSTONE_BOOTS = ITEMS.registerItem(
            "bloodstone_boots",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.BLOODSTONE,
                    ArmorItem.Type.BOOTS,
                    properties.durability(ArmorItem.Type.BOOTS.getDurability(50))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_HELMET = ITEMS.registerItem(
            "spectralite_helmet",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.HELMET,
                    properties.fireResistant().durability(ArmorItem.Type.HELMET.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_CHESTPLATE = ITEMS.registerItem(
            "spectralite_chestplate",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.CHESTPLATE,
                    properties.fireResistant().durability(ArmorItem.Type.CHESTPLATE.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_LEGGINGS = ITEMS.registerItem(
            "spectralite_leggings",
            properties -> new SpectralArmorItem(
                    ModArmorMaterials.SPECTRALITE,
                    ArmorItem.Type.LEGGINGS,
                    properties.fireResistant().durability(ArmorItem.Type.LEGGINGS.getDurability(65))
            ),
            new Item.Properties()
    );

    public static final DeferredItem<ArmorItem> SPECTRALITE_BOOTS = ITEMS.registerItem(
            "spectralite_boots",
            properties -> new SpectralArmorItem(
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
            properties -> new com.howierux.mortalexpansion.item.HauntedHoeItem(
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

    public static final DeferredItem<BlockItem> HAUNT_FURNACE =
            ITEMS.registerSimpleBlockItem(ModBlocks.HAUNT_FURNACE);
    public static final DeferredItem<BlockItem> HAINT_FURNACE =
            ITEMS.registerSimpleBlockItem(ModBlocks.HAINT_FURNACE);

    public static final DeferredItem<BookOfEctceteraItem> BOOK_OF_ECTCETERA = ITEMS.registerItem(
            "book_of_ectcetera", BookOfEctceteraItem::new, new Item.Properties().stacksTo(1));
    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
