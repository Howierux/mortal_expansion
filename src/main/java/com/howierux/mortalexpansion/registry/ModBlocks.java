package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.EctchantmentMachineBlock;
import com.howierux.mortalexpansion.block.HauntFurnaceBlock;
import com.howierux.mortalexpansion.block.HaintFurnaceBlock;
import com.howierux.mortalexpansion.block.EctoplasmicGeneratorBlock;
import com.howierux.mortalexpansion.block.EnchantmentGeneratorBlock;
import com.howierux.mortalexpansion.block.SpiritwoodSaplingBlock;
import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.block.PurificationWardBlock;
import com.howierux.mortalexpansion.block.PurificationStationBlock;
import com.howierux.mortalexpansion.block.HolyWaterLiquidBlock;
import com.howierux.mortalexpansion.block.PurifiedEctoplasmLiquidBlock;
import com.howierux.mortalexpansion.block.EctoCauldronBlock;
import com.howierux.mortalexpansion.block.HolySaltWireBlock;
import com.howierux.mortalexpansion.block.SpiritTorchBlock;
import com.howierux.mortalexpansion.block.SpiritWallTorchBlock;
import com.howierux.mortalexpansion.worldgen.ModTreeGrowers;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MortalExpansion.MOD_ID);

    public static final DeferredBlock<com.howierux.mortalexpansion.block.MortalGolemBaseBlock> MORTAL_GOLEM_BASE =
            BLOCKS.registerBlock("mortal_golem_base",com.howierux.mortalexpansion.block.MortalGolemBaseBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK));
    public static final DeferredBlock<com.howierux.mortalexpansion.block.WispLanternBlock> WISP_O_LANTERN =
            BLOCKS.registerBlock("wisp_o_lantern",com.howierux.mortalexpansion.block.WispLanternBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN).lightLevel(state -> 4));
    public static final DeferredBlock<com.howierux.mortalexpansion.block.DreamcatcherBlock> PURIFIED_DREAMCATCHER =
            BLOCKS.registerBlock("purified_dreamcatcher", com.howierux.mortalexpansion.block.DreamcatcherBlock::new,
                    BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().noCollission()
                            .sound(SoundType.WOOD).pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY));

    public static final DeferredBlock<com.howierux.mortalexpansion.block.DreamcatcherBlock> DREAMCATCHER =
            BLOCKS.registerBlock("dreamcatcher", com.howierux.mortalexpansion.block.DreamcatcherBlock::new,
                    BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().noCollission()
                            .sound(SoundType.WOOD).pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY));

    public static final DeferredBlock<com.howierux.mortalexpansion.block.HauntedFarmlandBlock> HAUNTED_FARMLAND = BLOCKS.registerBlock(
            "haunted_farmland",com.howierux.mortalexpansion.block.HauntedFarmlandBlock::new,BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND));

    public static final DeferredBlock<RotatedPillarBlock> SPIRITWOOD_LOG = BLOCKS.registerBlock(
            "spiritwood_log", RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
    );

    public static final DeferredBlock<Block> SPIRITWOOD_PLANKS = BLOCKS.registerSimpleBlock(
            "spiritwood_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final DeferredBlock<StairBlock> SPIRITWOOD_STAIRS = BLOCKS.registerBlock(
            "spiritwood_stairs",
            properties -> new StairBlock(SPIRITWOOD_PLANKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
    );

    public static final DeferredBlock<SlabBlock> SPIRITWOOD_SLAB = BLOCKS.registerBlock(
            "spiritwood_slab", SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)
    );

    public static final DeferredBlock<FenceBlock> SPIRITWOOD_FENCE = BLOCKS.registerBlock(
            "spiritwood_fence", FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)
    );

    public static final DeferredBlock<FenceGateBlock> SPIRITWOOD_FENCE_GATE = BLOCKS.registerBlock(
            "spiritwood_fence_gate",
            properties -> new FenceGateBlock(WoodType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)
    );

    public static final DeferredBlock<DoorBlock> SPIRITWOOD_DOOR = BLOCKS.registerBlock(
            "spiritwood_door",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)
    );

    public static final DeferredBlock<TrapDoorBlock> SPIRITWOOD_TRAPDOOR = BLOCKS.registerBlock(
            "spiritwood_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
    );

    public static final DeferredBlock<PressurePlateBlock> SPIRITWOOD_PRESSURE_PLATE = BLOCKS.registerBlock(
            "spiritwood_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)
    );

    public static final DeferredBlock<ButtonBlock> SPIRITWOOD_BUTTON = BLOCKS.registerBlock(
            "spiritwood_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)
    );

    public static final DeferredBlock<Block> GRAVESTONE = BLOCKS.registerSimpleBlock(
            "gravestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final DeferredBlock<HauntFurnaceBlock> HAUNT_FURNACE = BLOCKS.registerBlock(
            "haunt_furnace", HauntFurnaceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
    );

    public static final DeferredBlock<HaintFurnaceBlock> HAINT_FURNACE = BLOCKS.registerBlock(
            "haint_furnace", HaintFurnaceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
    );

    public static final DeferredBlock<GlowLichenBlock> GRAVE_MOSS = BLOCKS.registerBlock(
            "grave_moss",
            GlowLichenBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLOW_LICHEN)
                    .lightLevel(state -> 0)
    );

    public static final DeferredBlock<Block> PURIFIED_GRAVESTONE = BLOCKS.registerSimpleBlock(
            "purified_gravestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final DeferredBlock<RotatedPillarBlock> PURIFIED_SPIRITWOOD_LOG = BLOCKS.registerBlock(
            "purified_spiritwood_log", RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
    );

    public static final DeferredBlock<Block> PURIFIED_SPIRITWOOD_PLANKS = BLOCKS.registerSimpleBlock(
            "purified_spiritwood_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final DeferredBlock<StairBlock> PURIFIED_SPIRITWOOD_STAIRS = BLOCKS.registerBlock(
            "purified_spiritwood_stairs",
            properties -> new StairBlock(PURIFIED_SPIRITWOOD_PLANKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
    );

    public static final DeferredBlock<SlabBlock> PURIFIED_SPIRITWOOD_SLAB = BLOCKS.registerBlock(
            "purified_spiritwood_slab", SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)
    );

    public static final DeferredBlock<FenceBlock> PURIFIED_SPIRITWOOD_FENCE = BLOCKS.registerBlock(
            "purified_spiritwood_fence", FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)
    );

    public static final DeferredBlock<FenceGateBlock> PURIFIED_SPIRITWOOD_FENCE_GATE = BLOCKS.registerBlock(
            "purified_spiritwood_fence_gate",
            properties -> new FenceGateBlock(WoodType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)
    );

    public static final DeferredBlock<DoorBlock> PURIFIED_SPIRITWOOD_DOOR = BLOCKS.registerBlock(
            "purified_spiritwood_door",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)
    );

    public static final DeferredBlock<TrapDoorBlock> PURIFIED_SPIRITWOOD_TRAPDOOR = BLOCKS.registerBlock(
            "purified_spiritwood_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
    );

    public static final DeferredBlock<PressurePlateBlock> PURIFIED_SPIRITWOOD_PRESSURE_PLATE = BLOCKS.registerBlock(
            "purified_spiritwood_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)
    );

    public static final DeferredBlock<ButtonBlock> PURIFIED_SPIRITWOOD_BUTTON = BLOCKS.registerBlock(
            "purified_spiritwood_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)
    );

    public static final DeferredBlock<com.howierux.mortalexpansion.block.AltarOfImpurityBlock> ALTAR_OF_IMPURITY = BLOCKS.registerBlock("altar_of_impurity",com.howierux.mortalexpansion.block.AltarOfImpurityBlock::new,BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(state->state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)?10:0));
    public static final DeferredBlock<com.howierux.mortalexpansion.block.AltarOfPurityBlock> ALTAR_OF_PURITY = BLOCKS.registerBlock("altar_of_purity",com.howierux.mortalexpansion.block.AltarOfPurityBlock::new,BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(state->state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)?10:0));

    public static final DeferredBlock<com.howierux.mortalexpansion.block.MortalityResearchStationBlock> MORTALITY_RESEARCH_STATION=BLOCKS.registerBlock("mortality_research_station",com.howierux.mortalexpansion.block.MortalityResearchStationBlock::new,BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE));
    public static final DeferredBlock<EctchantmentMachineBlock> ECTCHANTMENT_MACHINE = BLOCKS.registerBlock(
            "ectchantment_machine",
            EctchantmentMachineBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final DeferredBlock<EctoplasmicGeneratorBlock> ECTOPLASMIC_GENERATOR = BLOCKS.registerBlock(
            "ectoplasmic_generator",
            EctoplasmicGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final DeferredBlock<EnchantmentGeneratorBlock> ENCHANTMENT_GENERATOR = BLOCKS.registerBlock(
            "enchantment_generator",
            EnchantmentGeneratorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final DeferredBlock<PurificationWardBlock> PURIFICATION_WARD = BLOCKS.registerBlock(
            "purification_ward",
            PurificationWardBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BEACON)
    );

    public static final DeferredBlock<PurificationStationBlock> PURIFICATION_STATION = BLOCKS.registerBlock(
            "purification_station",
            PurificationStationBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final DeferredBlock<LiquidBlock> LIQUID_ECTOPLASM_BLOCK = BLOCKS.registerBlock(
            "liquid_ectoplasm",
            properties -> new LiquidBlock(
                    ModFluids.LIQUID_ECTOPLASM.get(),
                    properties
            ),
            BlockBehaviour.Properties.of()
                    .replaceable()
                    .liquid()
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()
    );

    public static final DeferredBlock<HolyWaterLiquidBlock> HOLY_WATER_BLOCK = BLOCKS.registerBlock(
            "holy_water",
            properties -> new HolyWaterLiquidBlock(
                    ModFluids.HOLY_WATER.get(),
                    properties
            ),
            BlockBehaviour.Properties.of()
                    .replaceable()
                    .liquid()
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()
    );

    public static final DeferredBlock<PurifiedEctoplasmLiquidBlock> PURIFIED_ECTOPLASM_BLOCK = BLOCKS.registerBlock(
            "purified_ectoplasm_fluid",
            properties -> new PurifiedEctoplasmLiquidBlock(
                    ModFluids.PURIFIED_ECTOPLASM.get(),
                    properties
            ),
            BlockBehaviour.Properties.of()
                    .replaceable()
                    .liquid()
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()
    );

    public static final DeferredBlock<EctoCauldronBlock> LIQUID_ECTOPLASM_CAULDRON = BLOCKS.registerBlock(
            "liquid_ectoplasm_cauldron",
            properties -> new EctoCauldronBlock(properties, false),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
    );

    public static final DeferredBlock<EctoCauldronBlock> HOLY_WATER_CAULDRON = BLOCKS.registerBlock(
            "holy_water_cauldron",
            properties -> new EctoCauldronBlock(properties, true),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
    );

    public static final DeferredBlock<EctoCauldronBlock> PURIFIED_ECTOPLASM_CAULDRON = BLOCKS.registerBlock(
            "purified_ectoplasm_cauldron",
            properties -> new EctoCauldronBlock(properties, false),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
    );

    public static final DeferredBlock<SpiritTorchBlock> SPIRIT_TORCH = BLOCKS.registerBlock(
            "spirit_torch", SpiritTorchBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_TORCH));
    public static final DeferredBlock<SpiritWallTorchBlock> SPIRIT_WALL_TORCH = BLOCKS.registerBlock(
            "spirit_wall_torch", SpiritWallTorchBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WALL_TORCH));

    public static final DeferredBlock<HolySaltWireBlock> HOLY_SALT = BLOCKS.registerBlock(
            "holy_salt",
            HolySaltWireBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE)
    );

    public static final DeferredBlock<GraveBlock> GRAVE = BLOCKS.registerBlock(
            "grave",
            GraveBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final DeferredBlock<FenceBlock> STONE_FENCE = BLOCKS.registerBlock(
            "stone_fence",
            FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final DeferredBlock<DropExperienceBlock> BLOODSTONE_ORE = BLOCKS.registerBlock(
            "bloodstone_ore",
            properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
    );

    public static final DeferredBlock<DropExperienceBlock> BLOODSTONE_ORE_STONE = BLOCKS.registerBlock(
            "bloodstone_ore_stone",
            properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE)
    );

    public static final DeferredBlock<LeavesBlock> SPIRITWOOD_LEAVES = BLOCKS.registerBlock(
            "spiritwood_leaves", LeavesBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
    );

    public static final DeferredBlock<SpiritwoodSaplingBlock> SPIRITWOOD_SAPLING = BLOCKS.registerBlock(
            "spiritwood_sapling",
            properties -> new SpiritwoodSaplingBlock(ModTreeGrowers.SPIRITWOOD, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
    );

    /** Worldgen-only intangible anchor; intentionally has no BlockItem. */
    public static final DeferredBlock<com.howierux.mortalexpansion.block.SpiritwellBlock> SPIRITWELL =
            BLOCKS.registerBlock("spiritwell", com.howierux.mortalexpansion.block.SpiritwellBlock::new,
                    BlockBehaviour.Properties.of().noCollission().noOcclusion()
                            .strength(-1.0F, 3600000.0F).noLootTable()
                            .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
