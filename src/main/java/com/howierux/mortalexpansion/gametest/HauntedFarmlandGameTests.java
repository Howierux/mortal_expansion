package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.block.HauntedFarmlandBlock;
import com.howierux.mortalexpansion.haunt.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HauntedFarmlandGameTests {
    private static net.minecraft.server.level.ServerPlayer farmer(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);return p;
    }
    private static void till(GameTestHelper h,net.minecraft.server.level.ServerPlayer p,net.minecraft.world.item.Item hoe,BlockPos pos){
        var tool=new ItemStack(hoe);p.setItemInHand(InteractionHand.MAIN_HAND,tool);
        var result=tool.useOn(new UseOnContext(p,InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(pos.getCenter(),Direction.UP,pos,false)));
        h.assertTrue(result.consumesAction(),"Normal hoe use succeeds");h.assertTrue(tool.getDamageValue()==1,"Exactly one normal tool wear");
    }
    @GameTest(template="deferred_items_test")
    public static void only_spectral_hoes_create_haunted_soil_with_chunk_based_moisture(GameTestHelper h){
        var p=farmer(h);var pos=h.absolutePos(new BlockPos(5,2,5));PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);
        for(var hoe:new Item[]{ModItems.BLOODSTONE_HOE.get(),ModItems.SPECTRALITE_HOE.get()}){
            h.getLevel().setBlock(pos.above(),Blocks.AIR.defaultBlockState(),3);h.getLevel().setBlock(pos,Blocks.DIRT.defaultBlockState(),3);
            HauntManager.setTier(h.getLevel(),pos,HauntTier.NATURAL);till(h,p,hoe,pos);
            h.assertTrue(h.getLevel().getBlockState(pos).is(ModBlocks.HAUNTED_FARMLAND.get()) && h.getLevel().getBlockState(pos).getValue(FarmBlock.MOISTURE)==0,"Special hoe marks soil; personal Haunt does not hydrate it");
            h.getLevel().setBlock(pos,Blocks.DIRT.defaultBlockState(),3);HauntManager.setTier(h.getLevel(),pos,HauntTier.DEAD_RISING);till(h,p,hoe,pos);
            h.assertTrue(h.getLevel().getBlockState(pos).getValue(FarmBlock.MOISTURE)==7,"Above Supernatural tills fully moist");
        }
        h.getLevel().setBlock(pos,Blocks.DIRT.defaultBlockState(),3);till(h,p,Items.IRON_HOE,pos);
        h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.FARMLAND),"Ordinary hoe does not gain the bonus");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void growth_rates_escalate_and_moisture_starts_above_supernatural(GameTestHelper h){
        int previous=17;for(var tier:HauntTier.values()){
            int rate=HauntedFarmlandBlock.growthDenominator(tier);
            if(tier.id()==0)h.assertTrue(rate==0,"Void has no supernatural growth bonus");
            else{h.assertTrue(rate>0 && rate<previous,"Each higher tier grows more often");previous=rate;}
            h.assertTrue(HauntedFarmlandBlock.keepsMoist(tier)==(tier.id()>2),"Hydration starts strictly above Supernatural");
        }
        var pos=h.absolutePos(new BlockPos(5,2,5));h.getLevel().setBlock(pos.above(),Blocks.AIR.defaultBlockState(),3);
        h.getLevel().setBlock(pos.above(6),Blocks.STONE.defaultBlockState(),3); // Block rainfall without covering the soil directly.
        var soil=ModBlocks.HAUNTED_FARMLAND.get();
        HauntManager.setTier(h.getLevel(),pos,HauntTier.SUPERNATURAL);h.getLevel().setBlock(pos,soil.defaultBlockState().setValue(FarmBlock.MOISTURE,7),3);
        soil.randomTick(h.getLevel().getBlockState(pos),h.getLevel(),pos,net.minecraft.util.RandomSource.create(1));
        h.assertTrue(h.getLevel().getBlockState(pos).getValue(FarmBlock.MOISTURE)==6,"Supernatural still dries normally without water");
        for(var tier:new HauntTier[]{HauntTier.DEAD_RISING,HauntTier.DEMONIC,HauntTier.ABOMINABLE,HauntTier.ELDRITCH}){
            HauntManager.setTier(h.getLevel(),pos,tier);h.getLevel().setBlock(pos,soil.defaultBlockState(),3);
            soil.randomTick(h.getLevel().getBlockState(pos),h.getLevel(),pos,net.minecraft.util.RandomSource.create(1));
            h.assertTrue(h.getLevel().getBlockState(pos).getValue(FarmBlock.MOISTURE)==7,"High Haunt maintains full moisture without water or rain");
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void eldritch_soil_applies_real_bonemeal_and_stops_at_mature_crops(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));var soil=ModBlocks.HAUNTED_FARMLAND.get();HauntManager.setTier(h.getLevel(),pos,HauntTier.ELDRITCH);
        h.getLevel().setBlock(pos,soil.defaultBlockState(),3);h.getLevel().setBlock(pos.above(),Blocks.WHEAT.defaultBlockState(),3);
        h.assertTrue(Blocks.WHEAT.defaultBlockState().canSurvive(h.getLevel(),pos.above()),"Vanilla crops support the custom farmland subclass");
        soil.randomTick(h.getLevel().getBlockState(pos),h.getLevel(),pos,net.minecraft.util.RandomSource.create(1));
        int age=h.getLevel().getBlockState(pos.above()).getValue(CropBlock.AGE);h.assertTrue(age>=2 && age<=5,"Bonus applies native bonemeal growth, not just one normal growth step");
        h.getLevel().setBlock(pos.above(),Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE,7),3);
        h.assertFalse(HauntedFarmlandBlock.applyBonemeal(h.getLevel(),pos),"Mature crop does not consume repeated attempts");
        h.getLevel().setBlock(pos.above(),Blocks.STONE.defaultBlockState(),3);
        h.assertFalse(HauntedFarmlandBlock.applyBonemeal(h.getLevel(),pos),"Non-crop blocks are never fertilized");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void drying_and_replacement_end_the_bonus(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));var soil=ModBlocks.HAUNTED_FARMLAND.get();HauntManager.setTier(h.getLevel(),pos,HauntTier.VOID);
        var stored=soil.defaultBlockState().setValue(FarmBlock.MOISTURE,4);
        var encoded=net.minecraft.world.level.block.state.BlockState.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,stored).getOrThrow();
        var restored=net.minecraft.world.level.block.state.BlockState.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,encoded).getOrThrow();
        h.assertTrue(restored.is(soil) && restored.getValue(FarmBlock.MOISTURE)==4,"Native saved-block codec retains haunted identity and moisture");
        h.getLevel().setBlock(pos,soil.defaultBlockState(),3);h.getLevel().setBlock(pos.above(),Blocks.AIR.defaultBlockState(),3);h.getLevel().setBlock(pos.above(6),Blocks.STONE.defaultBlockState(),3);
        soil.randomTick(h.getLevel().getBlockState(pos),h.getLevel(),pos,net.minecraft.util.RandomSource.create(1));
        h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.DIRT),"Dry empty soil returns to dirt normally");
        h.getLevel().setBlock(pos,Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE,7),3);h.getLevel().setBlock(pos.above(),Blocks.WHEAT.defaultBlockState(),3);
        h.assertFalse(HauntedFarmlandBlock.applyBonemeal(h.getLevel(),pos),"Replacing it with ordinary farmland does not retain hidden provenance");h.succeed();
    }
    @GameTest(template="deferred_items_test",timeoutTicks=40)
    public static void covering_high_haunt_soil_still_returns_it_to_dirt(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));HauntManager.setTier(h.getLevel(),pos,HauntTier.ELDRITCH);
        h.getLevel().setBlock(pos,ModBlocks.HAUNTED_FARMLAND.get().defaultBlockState().setValue(FarmBlock.MOISTURE,7),3);
        h.getLevel().setBlock(pos.above(),Blocks.STONE.defaultBlockState(),3);
        h.runAfterDelay(3,()->{h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.DIRT),"Moisture cannot override native farmland survival");h.succeed();});
    }
}
