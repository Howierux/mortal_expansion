package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GraveyardStoneGameTests {
    @GameTest(template="deferred_items_test")
    public static void graveyard_stone_changes_without_haunt_payment_or_cemetery_requirement(GameTestHelper h){
        h.setBiome(ModBiomes.GRAVEYARD);var pos=h.absolutePos(new BlockPos(7,2,7));h.getLevel().setBlock(pos,Blocks.STONE.defaultBlockState(),3);
        HauntManager.setValue(h.getLevel(),pos,20);HauntManager.addHundredths(h.getLevel(),pos,7);
        h.assertTrue(GraveyardStoneWeathering.convert(h.getLevel(),pos),"Ordinary Graveyard biome terrain converts without looking up cemetery structures");
        h.assertTrue(h.getLevel().getBlockState(pos).is(ModBlocks.GRAVESTONE.get()) && HauntManager.getHundredths(h.getLevel(),pos)==2007,"Conversion preserves all Haunt points and fractions");
        h.assertFalse(GraveyardStoneWeathering.convert(h.getLevel(),pos),"Result does not repeatedly transform");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void other_biomes_materials_and_unloaded_chunks_are_untouched(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(7,2,7));h.setBiome(net.minecraft.world.level.biome.Biomes.PLAINS);h.getLevel().setBlock(pos,Blocks.STONE.defaultBlockState(),3);
        h.assertFalse(GraveyardStoneWeathering.convert(h.getLevel(),pos),"Stone outside Graveyard does not convert");
        h.setBiome(ModBiomes.GRAVEYARD);
        for(var block:new net.minecraft.world.level.block.Block[]{Blocks.COBBLESTONE,Blocks.SMOOTH_STONE,Blocks.DEEPSLATE,ModBlocks.PURIFIED_GRAVESTONE.get()}){
            h.getLevel().setBlock(pos,block.defaultBlockState(),3);h.assertFalse(GraveyardStoneWeathering.convert(h.getLevel(),pos),"Only ordinary Stone is eligible");
        }
        var distant=new BlockPos(25000000,2,25000000);h.assertFalse(h.getLevel().hasChunkAt(distant),"Test target starts unloaded");
        h.assertFalse(GraveyardStoneWeathering.convert(h.getLevel(),distant) || h.getLevel().hasChunkAt(distant),"Weathering does not load remote terrain");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void daily_sampling_is_bounded_and_does_not_spend_haunt(GameTestHelper h){
        h.setBiome(ModBiomes.GRAVEYARD);
        for(int x=0;x<15;x++)for(int z=0;z<15;z++){h.setBlock(new BlockPos(x,2,z),Blocks.STONE);for(int y=3;y<20;y++)h.setBlock(new BlockPos(x,y,z),Blocks.AIR);}
        var pos=h.absolutePos(new BlockPos(7,2,7));var chunk=h.getLevel().getChunkAt(pos);HauntManager.setValue(h.getLevel(),pos,0);int total=0;
        for(int i=0;i<16;i++){int converted=GraveyardStoneWeathering.sample(h.getLevel(),chunk);h.assertTrue(converted<=4,"One daily chunk pass converts at most four blocks");total+=converted;}
        h.assertTrue(total>0,"Loaded Graveyard surface samples can weather actual Stone: top " + chunk.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,pos.getX() & 15,pos.getZ() & 15) + " target " + pos + " biome " + h.getLevel().getBiome(pos));
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==0,"Even Void Haunt can weather and pays nothing");h.succeed();
    }
}
