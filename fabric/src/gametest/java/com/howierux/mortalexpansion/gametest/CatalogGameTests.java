package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class CatalogGameTests implements FabricGameTest {
 @GameTest(template=EMPTY_STRUCTURE)
 public void native_catalog_equipment_placement_connections_and_bucket_roundtrip(GameTestHelper h) {
  var level=h.getLevel();var at=h.absolutePos(new BlockPos(2,2,2));
  var armor=(ArmorItem)CatalogItems.SPECTRALITE_HAINT_CHESTPLATE;
  h.assertTrue(armor.getDefense()==8 && armor.getToughness()==3 && new ItemStack(armor).getMaxStackSize()==1,"Hybrid retains native Spectralite defense and equipment stacking");
  h.assertTrue(new ItemStack(CatalogItems.GHOST_GARB_CHESTPLATE).getMaxDamage()==240,"Garb retains iron durability");
  level.setBlockAndUpdate(at,CatalogBlocks.SPIRITWOOD_DOOR.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER));
  level.setBlockAndUpdate(at.above(),CatalogBlocks.SPIRITWOOD_DOOR.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER));
  var state=CatalogBlocks.ECTO_PIPE.defaultBlockState(); level.setBlockAndUpdate(at,state); level.setBlockAndUpdate(at.east(),CatalogBlocks.PURIFIED_ECTANK.defaultBlockState());
  h.assertTrue(level.getBlockState(at).getValue(BlockStateProperties.EAST),"Pipe arm connects to adjacent tank using native neighbor updates");
  level.removeBlock(at.east(),false);h.assertFalse(level.getBlockState(at).getValue(BlockStateProperties.EAST),"Removed tank clears pipe arm");
  var fluidAt=at.south(2);var bucket=(BucketItem)CatalogItems.HOLY_WATER_BUCKET;
  h.assertTrue(bucket.emptyContents(null,level,fluidAt,null),"Native holy-water bucket places registered fluid");
  var block=(net.minecraft.world.level.block.LiquidBlock)CatalogBlocks.HOLY_WATER_BLOCK;
  var picked=block.pickupBlock(null,level,fluidAt,level.getBlockState(fluidAt));
  h.assertTrue(picked.is(CatalogItems.HOLY_WATER_BUCKET)&&level.getBlockState(fluidAt).isAir(),"Fluid pickup returns the same native bucket");
  h.succeed();
 }
}
