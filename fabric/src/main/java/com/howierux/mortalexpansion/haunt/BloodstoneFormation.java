package com.howierux.mortalexpansion.haunt;
import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
/** Inspect section palettes first; only sections containing diamond need a block scan on a successful rare roll. */
public final class BloodstoneFormation {
 public static boolean due(long before,long now){return now>before&&(Math.floorDiv(now,24000)>Math.floorDiv(before,24000)||Math.floorDiv(now-12000,24000)>Math.floorDiv(before-12000,24000)||Math.floorDiv(now-18000,24000)>Math.floorDiv(before-18000,24000));}
 public static float chance(int value){int tier=HauntTier.fromValue(value).id();return tier<3?0:.005F*(1<<(tier-3));}
 public static boolean convert(ServerLevel level,LevelChunk chunk,RandomSource random){
  BlockPos chosen=null;int candidates=0;var sections=chunk.getSections();
  for(int index=0;index<sections.length;index++){
   var section=sections[index];if(!section.maybeHas(state->state.is(Blocks.DIAMOND_ORE)||state.is(Blocks.DEEPSLATE_DIAMOND_ORE)))continue;
   for(int x=0;x<16;x++)for(int y=0;y<16;y++)for(int z=0;z<16;z++){
    var state=section.getBlockState(x,y,z);if(!state.is(Blocks.DIAMOND_ORE)&&!state.is(Blocks.DEEPSLATE_DIAMOND_ORE))continue;
    if(random.nextInt(++candidates)==0)chosen=new BlockPos(chunk.getPos().getMinBlockX()+x,level.getMinBuildHeight()+index*16+y,chunk.getPos().getMinBlockZ()+z);
   }
  }
  return chosen!=null&&level.getWorldBorder().isWithinBounds(chosen)&&level.setBlock(chosen,CatalogBlocks.BLOODSTONE_ORE.defaultBlockState(),3);
 }
}
