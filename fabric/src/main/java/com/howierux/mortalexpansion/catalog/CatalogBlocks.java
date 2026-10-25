package com.howierux.mortalexpansion.catalog;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.util.valueproviders.UniformInt;
public final class CatalogBlocks {
 public static final Map<String,Block> ALL=new LinkedHashMap<>();
 static BlockBehaviour.Properties copy(Block b) { return BlockBehaviour.Properties.ofFullCopy(b); }
 static Block register(String id,Block block) { ALL.put(id,block); return Registry.register(BuiltInRegistries.BLOCK,Catalog.id(id),block); }
public static final Block FANCIFIER=register("fancifier",new FancifierVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block FANCIFIED_BLOCK=register("fancified_block",new FancifiedBlockVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTANK=register("ectank",new EctankVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFIED_ECTANK=register("purified_ectank",new PurifiedEctankVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTO_PIPE=register("ecto_pipe",new EctoPipeVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFIED_ECTO_PIPE=register("purified_ecto_pipe",new PurifiedEctoPipeVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block MORTAL_GOLEM_BASE=register("mortal_golem_base",new MortalGolemBaseVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block WISP_O_LANTERN=register("wisp_o_lantern",new CarvedPumpkinBlock(copy(Blocks.JACK_O_LANTERN).lightLevel(state -> 4)));
public static final Block PURIFIED_DREAMCATCHER=register("purified_dreamcatcher",new PurifiedDreamcatcherVisual(BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().noCollission().sound(SoundType.WOOD)));
public static final Block DREAMCATCHER=register("dreamcatcher",new DreamcatcherVisual(BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().noCollission().sound(SoundType.WOOD)));
public static final Block HAUNTED_FARMLAND=register("haunted_farmland",new FarmBlock(copy(Blocks.FARMLAND)));
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
public static final Block HAUNT_FURNACE=register("haunt_furnace",new HauntFurnaceVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block HAINT_FURNACE=register("haint_furnace",new HaintFurnaceVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
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
public static final Block ALTAR_OF_IMPURITY=register("altar_of_impurity",new AltarOfImpurityVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ALTAR_OF_PURITY=register("altar_of_purity",new AltarOfPurityVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block MORTALITY_RESEARCH_STATION=register("mortality_research_station",new MortalityResearchStationVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTCHANTMENT_MACHINE=register("ectchantment_machine",new EctchantmentMachineVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ECTOPLASMIC_GENERATOR=register("ectoplasmic_generator",new EctoplasmicGeneratorVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block ENCHANTMENT_GENERATOR=register("enchantment_generator",new EnchantmentGeneratorVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFICATION_WARD=register("purification_ward",new PurificationWardVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block PURIFICATION_STATION=register("purification_station",new PurificationStationVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block LIQUID_ECTOPLASM_BLOCK=register("liquid_ectoplasm",new LiquidBlock(CatalogFluids.LIQUID_ECTOPLASM.source,copy(Blocks.WATER)));
public static final Block HOLY_WATER_BLOCK=register("holy_water",new LiquidBlock(CatalogFluids.HOLY_WATER.source,copy(Blocks.WATER)));
public static final Block PURIFIED_ECTOPLASM_BLOCK=register("purified_ectoplasm_fluid",new LiquidBlock(CatalogFluids.PURIFIED_ECTOPLASM.source,copy(Blocks.WATER)));
public static final Block LIQUID_ECTOPLASM_CAULDRON=register("liquid_ectoplasm_cauldron",new LayeredCauldronBlock(net.minecraft.world.level.biome.Biome.Precipitation.RAIN,net.minecraft.core.cauldron.CauldronInteraction.WATER,copy(Blocks.WATER_CAULDRON)));
public static final Block HOLY_WATER_CAULDRON=register("holy_water_cauldron",new LayeredCauldronBlock(net.minecraft.world.level.biome.Biome.Precipitation.RAIN,net.minecraft.core.cauldron.CauldronInteraction.WATER,copy(Blocks.WATER_CAULDRON)));
public static final Block PURIFIED_ECTOPLASM_CAULDRON=register("purified_ectoplasm_cauldron",new LayeredCauldronBlock(net.minecraft.world.level.biome.Biome.Precipitation.RAIN,net.minecraft.core.cauldron.CauldronInteraction.WATER,copy(Blocks.WATER_CAULDRON)));
public static final Block SPIRIT_TORCH=register("spirit_torch",new SpiritTorchVisual(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion()));
public static final Block SPIRIT_WALL_TORCH=register("spirit_wall_torch",new SpiritWallTorchVisual(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion()));
public static final Block HOLY_SALT=register("holy_salt",new HolySaltVisual(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion()));
public static final Block GRAVE=register("grave",new GraveVisual(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
public static final Block STONE_FENCE=register("stone_fence",new FenceBlock(copy(Blocks.STONE)));
public static final Block BLOODSTONE_ORE=register("bloodstone_ore",new DropExperienceBlock(UniformInt.of(3,7),copy(Blocks.DEEPSLATE_DIAMOND_ORE)));
public static final Block BLOODSTONE_ORE_STONE=register("bloodstone_ore_stone",new DropExperienceBlock(UniformInt.of(3,7),copy(Blocks.DIAMOND_ORE)));
public static final Block SPIRITWOOD_LEAVES=register("spiritwood_leaves",new LeavesBlock(copy(Blocks.OAK_LEAVES)));
public static final Block SPIRITWOOD_SAPLING=register("spiritwood_sapling",new CatalogBlocks.SaplingVisual(copy(Blocks.OAK_SAPLING)));
public static final Block SPIRITWELL=register("spiritwell",new SpiritwellVisual(BlockBehaviour.Properties.of().strength(-1,3600000).noCollission().noOcclusion().noLootTable()));
 public static void initialize() {}
 /** Missing function: Spiritwood tree features and growth. */
 static final class SaplingVisual extends BushBlock { SaplingVisual(Properties p) { super(p); } @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState s,net.minecraft.world.level.BlockGetter l,BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c) { return box(2,0,2,14,12,14); } @Override protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() { return simpleCodec(SaplingVisual::new); } }
/** Missing function: port fancifier interactions and simulation; this is its catalog appearance. */
 static final class FancifierVisual extends Block {
 static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
 FancifierVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_FACING); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState s,net.minecraft.world.level.BlockGetter l,BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c) { return box(0,0,0,16,9,16); }
 }
/** Missing function: port fancified_block interactions and simulation; this is its catalog appearance. */
 static final class FancifiedBlockVisual extends Block {
 
 FancifiedBlockVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port ectank interactions and simulation; this is its catalog appearance. */
 static final class EctankVisual extends Block {
 
 EctankVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port purified_ectank interactions and simulation; this is its catalog appearance. */
 static final class PurifiedEctankVisual extends Block {
 
 PurifiedEctankVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port ecto_pipe interactions and simulation; this is its catalog appearance. */
 static final class EctoPipeVisual extends Block {
 static final BooleanProperty P_DOWN = BlockStateProperties.DOWN;
static final BooleanProperty P_EAST = BlockStateProperties.EAST;
static final BooleanProperty P_NORTH = BlockStateProperties.NORTH;
static final BooleanProperty P_SOUTH = BlockStateProperties.SOUTH;
static final BooleanProperty P_UP = BlockStateProperties.UP;
static final BooleanProperty P_WEST = BlockStateProperties.WEST;
 EctoPipeVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) {
  var s=defaultBlockState(); for(var d:net.minecraft.core.Direction.values()) s=s.setValue((BooleanProperty)getStateDefinition().getProperty(d.getName()),connects(c.getLevel().getBlockState(c.getClickedPos().relative(d)))); return s;
 }
 @Override protected BlockState updateShape(BlockState s,net.minecraft.core.Direction d,BlockState n,net.minecraft.world.level.LevelAccessor l,BlockPos p,BlockPos np) { return s.setValue((BooleanProperty)getStateDefinition().getProperty(d.getName()),connects(n)); }
 private static boolean connects(BlockState s) { return s.is(ECTANK)||s.is(PURIFIED_ECTANK)||s.is(ECTO_PIPE)||s.is(PURIFIED_ECTO_PIPE); }

 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_DOWN,P_EAST,P_NORTH,P_SOUTH,P_UP,P_WEST); }
 
 @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState s,net.minecraft.world.level.BlockGetter l,BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c) { var shape=box(5,5,5,11,11,11); for(var dir:net.minecraft.core.Direction.values()) if(s.getValue((BooleanProperty)s.getBlock().getStateDefinition().getProperty(dir.getName()))) shape=net.minecraft.world.phys.shapes.Shapes.or(shape,box(dir==net.minecraft.core.Direction.WEST?0:5,dir==net.minecraft.core.Direction.DOWN?0:5,dir==net.minecraft.core.Direction.NORTH?0:5,dir==net.minecraft.core.Direction.EAST?16:11,dir==net.minecraft.core.Direction.UP?16:11,dir==net.minecraft.core.Direction.SOUTH?16:11)); return shape; }
 }
/** Missing function: port purified_ecto_pipe interactions and simulation; this is its catalog appearance. */
 static final class PurifiedEctoPipeVisual extends Block {
 static final BooleanProperty P_DOWN = BlockStateProperties.DOWN;
static final BooleanProperty P_EAST = BlockStateProperties.EAST;
static final BooleanProperty P_NORTH = BlockStateProperties.NORTH;
static final BooleanProperty P_SOUTH = BlockStateProperties.SOUTH;
static final BooleanProperty P_UP = BlockStateProperties.UP;
static final BooleanProperty P_WEST = BlockStateProperties.WEST;
 PurifiedEctoPipeVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) {
  var s=defaultBlockState(); for(var d:net.minecraft.core.Direction.values()) s=s.setValue((BooleanProperty)getStateDefinition().getProperty(d.getName()),connects(c.getLevel().getBlockState(c.getClickedPos().relative(d)))); return s;
 }
 @Override protected BlockState updateShape(BlockState s,net.minecraft.core.Direction d,BlockState n,net.minecraft.world.level.LevelAccessor l,BlockPos p,BlockPos np) { return s.setValue((BooleanProperty)getStateDefinition().getProperty(d.getName()),connects(n)); }
 private static boolean connects(BlockState s) { return s.is(ECTANK)||s.is(PURIFIED_ECTANK)||s.is(ECTO_PIPE)||s.is(PURIFIED_ECTO_PIPE); }

 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_DOWN,P_EAST,P_NORTH,P_SOUTH,P_UP,P_WEST); }
 
 @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState s,net.minecraft.world.level.BlockGetter l,BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c) { var shape=box(5,5,5,11,11,11); for(var dir:net.minecraft.core.Direction.values()) if(s.getValue((BooleanProperty)s.getBlock().getStateDefinition().getProperty(dir.getName()))) shape=net.minecraft.world.phys.shapes.Shapes.or(shape,box(dir==net.minecraft.core.Direction.WEST?0:5,dir==net.minecraft.core.Direction.DOWN?0:5,dir==net.minecraft.core.Direction.NORTH?0:5,dir==net.minecraft.core.Direction.EAST?16:11,dir==net.minecraft.core.Direction.UP?16:11,dir==net.minecraft.core.Direction.SOUTH?16:11)); return shape; }
 }
/** Missing function: port mortal_golem_base interactions and simulation; this is its catalog appearance. */
 static final class MortalGolemBaseVisual extends Block {
 static final EnumProperty<net.minecraft.core.Direction.Axis> P_AXIS = BlockStateProperties.AXIS;
static final IntegerProperty P_FLESH_STAGE = IntegerProperty.create("flesh_stage",0,4);
 MortalGolemBaseVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_AXIS,P_FLESH_STAGE); }
 
 
 }
/** Missing function: port purified_dreamcatcher interactions and simulation; this is its catalog appearance. */
 static final class PurifiedDreamcatcherVisual extends Block {
 static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
 PurifiedDreamcatcherVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_FACING); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 
 }
/** Missing function: port dreamcatcher interactions and simulation; this is its catalog appearance. */
 static final class DreamcatcherVisual extends Block {
 static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
 DreamcatcherVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_FACING); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 
 }
/** Missing function: port haunt_furnace interactions and simulation; this is its catalog appearance. */
 static final class HauntFurnaceVisual extends Block {
 static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
static final BooleanProperty P_LIT = BlockStateProperties.LIT;
 HauntFurnaceVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_LIT,false).setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_FACING,P_LIT); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 
 }
/** Missing function: port haint_furnace interactions and simulation; this is its catalog appearance. */
 static final class HaintFurnaceVisual extends Block {
 static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
static final BooleanProperty P_HAUNT_POWERED = BooleanProperty.create("haunt_powered");
static final BooleanProperty P_LIT = BlockStateProperties.LIT;
 HaintFurnaceVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_LIT,false).setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_FACING,P_HAUNT_POWERED,P_LIT); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 
 }
/** Missing function: port altar_of_impurity interactions and simulation; this is its catalog appearance. */
 static final class AltarOfImpurityVisual extends Block {
 static final BooleanProperty P_LIT = BlockStateProperties.LIT;
 AltarOfImpurityVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_LIT,false)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_LIT); }
 
 
 }
/** Missing function: port altar_of_purity interactions and simulation; this is its catalog appearance. */
 static final class AltarOfPurityVisual extends Block {
 static final BooleanProperty P_LIT = BlockStateProperties.LIT;
 AltarOfPurityVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_LIT,false)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_LIT); }
 
 
 }
/** Missing function: port mortality_research_station interactions and simulation; this is its catalog appearance. */
 static final class MortalityResearchStationVisual extends Block {
 
 MortalityResearchStationVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port ectchantment_machine interactions and simulation; this is its catalog appearance. */
 static final class EctchantmentMachineVisual extends Block {
 
 EctchantmentMachineVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port ectoplasmic_generator interactions and simulation; this is its catalog appearance. */
 static final class EctoplasmicGeneratorVisual extends Block {
 
 EctoplasmicGeneratorVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port enchantment_generator interactions and simulation; this is its catalog appearance. */
 static final class EnchantmentGeneratorVisual extends Block {
 
 EnchantmentGeneratorVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port purification_ward interactions and simulation; this is its catalog appearance. */
 static final class PurificationWardVisual extends Block {
 
 PurificationWardVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port purification_station interactions and simulation; this is its catalog appearance. */
 static final class PurificationStationVisual extends Block {
 
 PurificationStationVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
/** Missing function: port spirit_torch interactions and simulation; this is its catalog appearance. */
 static final class SpiritTorchVisual extends Block {
 static final BooleanProperty P_LIT = BlockStateProperties.LIT;
 SpiritTorchVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_LIT,false)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_LIT); }
 
 
 }
/** Missing function: port spirit_wall_torch interactions and simulation; this is its catalog appearance. */
 static final class SpiritWallTorchVisual extends Block {
 static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
static final BooleanProperty P_LIT = BlockStateProperties.LIT;
 SpiritWallTorchVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_LIT,false).setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_FACING,P_LIT); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 
 }
/** Missing function: port holy_salt interactions and simulation; this is its catalog appearance. */
 static final class HolySaltVisual extends Block {
 static final EnumProperty<RedstoneSide> P_EAST = BlockStateProperties.EAST_REDSTONE;
static final EnumProperty<RedstoneSide> P_NORTH = BlockStateProperties.NORTH_REDSTONE;
static final EnumProperty<RedstoneSide> P_SOUTH = BlockStateProperties.SOUTH_REDSTONE;
static final EnumProperty<RedstoneSide> P_WEST = BlockStateProperties.WEST_REDSTONE;
 HolySaltVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_EAST,P_NORTH,P_SOUTH,P_WEST); }
 
 
 }
/** Missing function: port grave interactions and simulation; this is its catalog appearance. */
 static final class GraveVisual extends Block {
 static final IntegerProperty P_DEPTH = IntegerProperty.create("depth",0,2);
static final DirectionProperty P_FACING = BlockStateProperties.HORIZONTAL_FACING;
static final BooleanProperty P_NATURAL = BooleanProperty.create("natural");
 GraveVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(P_DEPTH,1).setValue(P_FACING,net.minecraft.core.Direction.NORTH)); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) { b.add(P_DEPTH,P_FACING,P_NATURAL); }
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c) { return defaultBlockState().setValue(P_FACING,c.getHorizontalDirection().getOpposite()); }
 @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState s,net.minecraft.world.level.BlockGetter l,BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c) { int d=s.getValue(P_DEPTH)*4; return s.getValue(P_FACING).getAxis()==net.minecraft.core.Direction.Axis.X?box(d,0,0,d+8,16,16):box(0,0,d,16,16,d+8); }
 }
/** Missing function: port spiritwell interactions and simulation; this is its catalog appearance. */
 static final class SpiritwellVisual extends Block {
 
 SpiritwellVisual(Properties p) { super(p); registerDefaultState(stateDefinition.any()); }
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b) {  }
 
 
 }
}
