package com.howierux.mortalexpansion.catalog;
import java.util.*;
import com.howierux.mortalexpansion.machine.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.util.valueproviders.UniformInt;
public final class CatalogBlocks {
 public static final IntegerProperty GRAVE_DEPTH=IntegerProperty.create("depth",0,2);
 public static final BooleanProperty GRAVE_NATURAL=BooleanProperty.create("natural");
 public static final Map<String,Block> ALL=new LinkedHashMap<>();
 static BlockBehaviour.Properties copy(Block b) { return BlockBehaviour.Properties.ofFullCopy(b); }
 static Block register(String id,Block block) { ALL.put(id,block); return Registry.register(BuiltInRegistries.BLOCK,Catalog.id(id),block); }
public static final Block FANCIFIER=register("fancifier",new com.howierux.mortalexpansion.block.FancifierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block FANCIFIED_BLOCK=register("fancified_block",new FancifiedBlockVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTANK=register("ectank",new FluidMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFIED_ECTANK=register("purified_ectank",new FluidMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTO_PIPE=register("ecto_pipe",new FluidMachineBlock.Pipe(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFIED_ECTO_PIPE=register("purified_ecto_pipe",new FluidMachineBlock.Pipe(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block MORTAL_GOLEM_BASE=register("mortal_golem_base",new com.howierux.mortalexpansion.block.MortalGolemBaseBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block WISP_O_LANTERN=register("wisp_o_lantern",new com.howierux.mortalexpansion.block.WispLanternBlock(copy(Blocks.JACK_O_LANTERN).lightLevel(state -> 4)));
public static final Block PURIFIED_DREAMCATCHER=register("purified_dreamcatcher",new com.howierux.mortalexpansion.block.DreamcatcherBlock(BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().noCollission().sound(SoundType.WOOD)));
public static final Block DREAMCATCHER=register("dreamcatcher",new com.howierux.mortalexpansion.block.DreamcatcherBlock(BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().noCollission().sound(SoundType.WOOD)));
public static final Block HAUNTED_FARMLAND=register("haunted_farmland",new com.howierux.mortalexpansion.block.HauntedFarmlandBlock(copy(Blocks.FARMLAND)));
public static final Block SPIRITWOOD_LOG=register("spiritwood_log",new RotatedPillarBlock(copy(Blocks.OAK_LOG)));
public static final Block SPIRITWOOD_PLANKS=register("spiritwood_planks",new Block(copy(Blocks.OAK_PLANKS)));
public static final Block SPIRITWOOD_STAIRS=register("spiritwood_stairs",new StairBlock(SPIRITWOOD_PLANKS.defaultBlockState(),copy(Blocks.OAK_STAIRS)));
public static final Block SPIRITWOOD_SLAB=register("spiritwood_slab",new SlabBlock(copy(Blocks.OAK_SLAB)));
public static final Block SPIRITWOOD_FENCE=register("spiritwood_fence",new FenceBlock(copy(Blocks.OAK_FENCE)));
public static final Block SPIRITWOOD_FENCE_GATE=register("spiritwood_fence_gate",new FenceGateBlock(WoodType.OAK,copy(Blocks.OAK_FENCE_GATE)));
public static final Block SPIRITWOOD_DOOR=register("spiritwood_door",new DoorBlock(BlockSetType.OAK,copy(Blocks.OAK_DOOR)));
public static final Block SPIRITWOOD_TRAPDOOR=register("spiritwood_trapdoor",new TrapDoorBlock(BlockSetType.OAK,copy(Blocks.OAK_TRAPDOOR)));
public static final Block SPIRITWOOD_PRESSURE_PLATE=register("spiritwood_pressure_plate",new PressurePlateBlock(BlockSetType.OAK,copy(Blocks.OAK_PRESSURE_PLATE)));
public static final Block SPIRITWOOD_BUTTON=register("spiritwood_button",new ButtonBlock(BlockSetType.OAK,30,copy(Blocks.OAK_BUTTON)));
public static final Block GRAVESTONE=register("gravestone",new Block(copy(Blocks.STONE)));
public static final Block HAUNT_FURNACE=register("haunt_furnace",new HauntFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));
public static final Block HAINT_FURNACE=register("haint_furnace",new HauntFurnaceBlock.Haint(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));
public static final Block GRAVE_MOSS=register("grave_moss",new GlowLichenBlock(copy(Blocks.GLOW_LICHEN)));
public static final Block PURIFIED_GRAVESTONE=register("purified_gravestone",new Block(copy(Blocks.STONE)));
public static final Block PURIFIED_SPIRITWOOD_LOG=register("purified_spiritwood_log",new RotatedPillarBlock(copy(Blocks.OAK_LOG)));
public static final Block PURIFIED_SPIRITWOOD_PLANKS=register("purified_spiritwood_planks",new Block(copy(Blocks.OAK_PLANKS)));
public static final Block PURIFIED_SPIRITWOOD_STAIRS=register("purified_spiritwood_stairs",new StairBlock(PURIFIED_SPIRITWOOD_PLANKS.defaultBlockState(),copy(Blocks.OAK_STAIRS)));
public static final Block PURIFIED_SPIRITWOOD_SLAB=register("purified_spiritwood_slab",new SlabBlock(copy(Blocks.OAK_SLAB)));
public static final Block PURIFIED_SPIRITWOOD_FENCE=register("purified_spiritwood_fence",new FenceBlock(copy(Blocks.OAK_FENCE)));
public static final Block PURIFIED_SPIRITWOOD_FENCE_GATE=register("purified_spiritwood_fence_gate",new FenceGateBlock(WoodType.OAK,copy(Blocks.OAK_FENCE_GATE)));
public static final Block PURIFIED_SPIRITWOOD_DOOR=register("purified_spiritwood_door",new DoorBlock(BlockSetType.OAK,copy(Blocks.OAK_DOOR)));
public static final Block PURIFIED_SPIRITWOOD_TRAPDOOR=register("purified_spiritwood_trapdoor",new TrapDoorBlock(BlockSetType.OAK,copy(Blocks.OAK_TRAPDOOR)));
public static final Block PURIFIED_SPIRITWOOD_PRESSURE_PLATE=register("purified_spiritwood_pressure_plate",new PressurePlateBlock(BlockSetType.OAK,copy(Blocks.OAK_PRESSURE_PLATE)));
public static final Block PURIFIED_SPIRITWOOD_BUTTON=register("purified_spiritwood_button",new ButtonBlock(BlockSetType.OAK,30,copy(Blocks.OAK_BUTTON)));
public static final Block ALTAR_OF_IMPURITY=register("altar_of_impurity",new AltarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(state->state.getValue(BlockStateProperties.LIT)?10:0)));
public static final Block ALTAR_OF_PURITY=register("altar_of_purity",new AltarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(state->state.getValue(BlockStateProperties.LIT)?10:0)));
public static final Block MORTALITY_RESEARCH_STATION=register("mortality_research_station",new ResearchStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTCHANTMENT_MACHINE=register("ectchantment_machine",new com.howierux.mortalexpansion.machine.EctchantmentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTOPLASMIC_GENERATOR=register("ectoplasmic_generator",new GeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ENCHANTMENT_GENERATOR=register("enchantment_generator",new GeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFICATION_WARD=register("purification_ward",new PurificationWardBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFICATION_STATION=register("purification_station",new PurificationStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block LIQUID_ECTOPLASM_BLOCK=register("liquid_ectoplasm",new LiquidBlock(CatalogFluids.LIQUID_ECTOPLASM.source,copy(Blocks.WATER)));
public static final Block HOLY_WATER_BLOCK=register("holy_water",new com.howierux.mortalexpansion.block.HolyWaterLiquidBlock(CatalogFluids.HOLY_WATER.source,copy(Blocks.WATER)));
public static final Block PURIFIED_ECTOPLASM_BLOCK=register("purified_ectoplasm_fluid",new LiquidBlock(CatalogFluids.PURIFIED_ECTOPLASM.source,copy(Blocks.WATER)));
public static final Block LIQUID_ECTOPLASM_CAULDRON=register("liquid_ectoplasm_cauldron",new com.howierux.mortalexpansion.block.EctoCauldronBlock(copy(Blocks.CAULDRON)));
public static final Block HOLY_WATER_CAULDRON=register("holy_water_cauldron",new com.howierux.mortalexpansion.block.EctoCauldronBlock(copy(Blocks.CAULDRON)));
public static final Block PURIFIED_ECTOPLASM_CAULDRON=register("purified_ectoplasm_cauldron",new com.howierux.mortalexpansion.block.EctoCauldronBlock(copy(Blocks.CAULDRON)));
public static final Block SPIRIT_TORCH=register("spirit_torch",new com.howierux.mortalexpansion.block.SpiritTorchBlock(copy(Blocks.REDSTONE_TORCH)));
public static final Block SPIRIT_WALL_TORCH=register("spirit_wall_torch",new com.howierux.mortalexpansion.block.SpiritWallTorchBlock(copy(Blocks.REDSTONE_WALL_TORCH)));
public static final Block HOLY_SALT=register("holy_salt",new com.howierux.mortalexpansion.block.HolySaltWireBlock(copy(Blocks.REDSTONE_WIRE)));
public static final Block GRAVE=register("grave",new com.howierux.mortalexpansion.travel.GraveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block STONE_FENCE=register("stone_fence",new FenceBlock(copy(Blocks.STONE)));
public static final Block BLOODSTONE_ORE=register("bloodstone_ore",new DropExperienceBlock(UniformInt.of(3,7),copy(Blocks.DEEPSLATE_DIAMOND_ORE)));
public static final Block BLOODSTONE_ORE_STONE=register("bloodstone_ore_stone",new DropExperienceBlock(UniformInt.of(3,7),copy(Blocks.DIAMOND_ORE)));
public static final Block SPIRITWOOD_LEAVES=register("spiritwood_leaves",new LeavesBlock(copy(Blocks.OAK_LEAVES)));
public static final Block SPIRITWOOD_SAPLING=register("spiritwood_sapling",new com.howierux.mortalexpansion.worldgen.SpiritwoodSaplingBlock(com.howierux.mortalexpansion.worldgen.ModTreeGrowers.SPIRITWOOD,copy(Blocks.OAK_SAPLING)));
public static final Block SPIRITWELL=register("spiritwell",new com.howierux.mortalexpansion.spiritwell.SpiritwellBlock(BlockBehaviour.Properties.of().strength(-1,3600000).noCollission().noOcclusion().noLootTable()));
 public static void initialize() {}

/** Reserved legacy proxy registry ID; Fabric fading retains native block identity. NeoForge saves need migration. */
 static final class FancifiedBlockVisual extends Block {
 
 FancifiedBlockVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }

 }

}
