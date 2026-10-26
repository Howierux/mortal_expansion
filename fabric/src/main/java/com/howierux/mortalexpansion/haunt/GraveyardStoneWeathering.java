package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

/** Biome-driven loaded-chunk weathering. No Haunt reads, writes or structure requirements. */
public final class GraveyardStoneWeathering {
    public static final int MAX_CONVERSIONS=4,MAX_SAMPLES=64;
    private GraveyardStoneWeathering(){}
    public static boolean convert(ServerLevel level,BlockPos pos){
        if(!level.hasChunkAt(pos) || !level.getWorldBorder().isWithinBounds(pos)
            || !level.getBlockState(pos).is(Blocks.STONE) || !level.getBiome(pos).is(ModBiomes.GRAVEYARD))return false;
        return level.setBlock(pos,CatalogBlocks.GRAVESTONE.defaultBlockState(),3);
    }
    public static int sample(ServerLevel level,LevelChunk chunk){
        // Keep weathering randomness independent of the existing Haunt spread/decay rolls.
        var random=RandomSource.create(level.getSeed() ^ chunk.getPos().toLong() ^ (Math.floorDiv(level.getDayTime(),24000L)*0x9E3779B97F4A7C15L));
        int converted=0;
        for(int attempt=0;attempt<MAX_SAMPLES && converted<MAX_CONVERSIONS;attempt++){
            int x=random.nextInt(16),z=random.nextInt(16);
            int y=attempt%2==0?chunk.getHeight(Heightmap.Types.WORLD_SURFACE,x,z)
                :level.getMinBuildHeight()+random.nextInt(level.getHeight());
            var pos=new BlockPos(chunk.getPos().getMinBlockX()+x,y,chunk.getPos().getMinBlockZ()+z);
            if(convert(level,pos))converted++;
        }
        return converted;
    }
}
