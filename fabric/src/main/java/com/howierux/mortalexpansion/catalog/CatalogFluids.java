package com.howierux.mortalexpansion.catalog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;

/** Vanilla fluid behavior isolated from the later Fabric Transfer API machine port. */
public final class CatalogFluids {
 public static final Pair LIQUID_ECTOPLASM=new Pair("liquid_ectoplasm","liquid_ectoplasm","liquid_ectoplasm_bucket");
 public static final Pair HOLY_WATER=new Pair("holy_water","holy_water","holy_water_bucket");
 public static final Pair PURIFIED_ECTOPLASM=new Pair("purified_ectoplasm","purified_ectoplasm_fluid","purified_ectoplasm_bucket");
 public static final class Pair {
  public final FlowingFluid source,flowing;
  final String block,bucket;
  Pair(String id,String block,String bucket) {
   this.block=block; this.bucket=bucket;
   source=Registry.register(BuiltInRegistries.FLUID,Catalog.id(id),new Source(this));
   flowing=Registry.register(BuiltInRegistries.FLUID,Catalog.id("flowing_"+id),new Flowing(this));
  }
 }
 private abstract static class Base extends WaterFluid {
  final Pair pair;
  Base(Pair pair) { this.pair=pair; }
  @Override public Fluid getSource() { return pair.source; }
  @Override public Fluid getFlowing() { return pair.flowing; }
  @Override public Item getBucket() { return CatalogItems.ALL.get(pair.bucket); }
  @Override public boolean isSame(Fluid f) { return f==pair.source||f==pair.flowing; }
  @Override public BlockState createLegacyBlock(FluidState state) { return CatalogBlocks.ALL.get(pair.block).defaultBlockState().setValue(LiquidBlock.LEVEL,getLegacyLevel(state)); }
  // Missing function: ectoplasm viscosity, holy-water blessing, and cauldrons.
 }
 private static final class Source extends Base {
  Source(Pair p) { super(p); }
  @Override public boolean isSource(FluidState s) { return true; }
  @Override public int getAmount(FluidState s) { return 8; }
 }
 private static final class Flowing extends Base {
  Flowing(Pair p) { super(p); }
  @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid,FluidState> b) { super.createFluidStateDefinition(b); b.add(LEVEL); }
  @Override public boolean isSource(FluidState s) { return false; }
  @Override public int getAmount(FluidState s) { return s.getValue(LEVEL); }
 }
 public static void initialize() {}
}
