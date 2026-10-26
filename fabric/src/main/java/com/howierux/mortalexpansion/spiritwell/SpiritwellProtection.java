package com.howierux.mortalexpansion.spiritwell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
/** Indexed loaded-only safe radius around wells. Consecrated land remains a separate slice. */
public final class SpiritwellProtection {
 public static boolean isProtected(ServerLevel level,BlockPos pos){return SpiritwellIndex.get(level).knownPositions().stream().anyMatch(well->well.distSqr(pos)<=4&&level.hasChunkAt(well)&&level.getBlockState(well).is(com.howierux.mortalexpansion.catalog.CatalogBlocks.SPIRITWELL));}
}
