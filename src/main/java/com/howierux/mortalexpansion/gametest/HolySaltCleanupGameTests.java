package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.entity.mob.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HolySaltCleanupGameTests {
    private static void salt(GameTestHelper h,BlockPos pos){
        h.getLevel().setBlock(pos.below(),Blocks.STONE.defaultBlockState(),3);
        h.getLevel().setBlock(pos,ModBlocks.HOLY_SALT.get().defaultBlockState(),3);
    }
    private static boolean blocked(GameTestHelper h,BlockPos pos){return HolySaltBarrierTracker.hasSaltColumn(h.getLevel(),pos.getX(),pos.getZ());}
    @GameTest(template="deferred_items_test")
    public static void stacked_salt_and_support_removal_clear_collision(GameTestHelper h){
        var lower=h.absolutePos(new BlockPos(5,3,5));var upper=lower.above(4);salt(h,lower);salt(h,upper);
        var ghost=ModEntities.GHOST.get().create(h.getLevel());ghost.setPos(lower.getCenter().add(2,5,0));
        var destination=new AABB(lower.above(6));
        h.assertTrue(blocked(h,lower) && !SpectralPhasingRules.canEnterHorizontally(ghost,destination),"Loaded salt projects a real collision boundary");
        h.getLevel().destroyBlock(lower,false);h.assertTrue(blocked(h,lower),"Salt at another height retains its boundary");
        h.getLevel().removeBlock(upper.below(),false);
        h.assertTrue(!h.getLevel().getBlockState(upper).is(ModBlocks.HOLY_SALT.get()) && !blocked(h,lower),"Support loss removes final salt and clears the boundary immediately");
        h.assertTrue(SpectralPhasingRules.canEnterHorizontally(ghost,destination),"Phasing no longer sees the removed barrier");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void packet_transitions_and_prediction_corrections_are_idempotent(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));var air=Blocks.AIR.defaultBlockState();var salt=ModBlocks.HOLY_SALT.get().defaultBlockState();
        h.getLevel().removeBlock(pos,false);h.assertFalse(blocked(h,pos),"Empty query does not create a permanent empty-column result");
        HolySaltBarrierTracker.onBlockChanged(h.getLevel(),pos,air,salt);
        HolySaltBarrierTracker.onBlockChanged(h.getLevel(),pos,air,salt);
        HolySaltBarrierTracker.onBlockChanged(h.getLevel(),pos,salt,salt.setValue(com.howierux.mortalexpansion.block.HolySaltWireBlock.POWER,7));
        h.assertTrue(blocked(h,pos),"Repeated packet and power updates keep one salt entry");
        HolySaltBarrierTracker.onBlockChanged(h.getLevel(),pos,salt,air);h.assertFalse(blocked(h,pos),"One confirmed removal clears a predicted salt barrier");
        HolySaltBarrierTracker.onBlockChanged(h.getLevel(),pos,air,salt);h.assertTrue(blocked(h,pos),"A server correction can restore actual salt");
        HolySaltBarrierTracker.onBlockChanged(h.getLevel(),pos,salt,air);h.assertFalse(blocked(h,pos),"Second removal clears again");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void chunk_lifecycle_rebuilds_actual_salt_and_discards_stale_entries(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));var phantom=new BlockPos(pos.getX()^1,pos.getY(),pos.getZ());salt(h,pos);
        h.getLevel().removeBlock(phantom,false);HolySaltBarrierTracker.markPresent(h.getLevel(),phantom);
        var chunk=h.getLevel().getChunkAt(pos);HolySaltBarrierTracker.onChunkUnload(new ChunkEvent.Unload(chunk));
        h.assertTrue(!blocked(h,pos) && !blocked(h,phantom),"Unload removes all column entries owned by this chunk");
        HolySaltBarrierTracker.onChunkLoad(new ChunkEvent.Load(chunk,false));HolySaltBarrierTracker.onChunkLoad(new ChunkEvent.Load(chunk,false));
        h.assertTrue(blocked(h,pos) && !blocked(h,phantom),"Reload indexes actual blocks and removes stale predictions, even if repeated");
        h.getLevel().removeBlock(pos,false);h.assertFalse(blocked(h,pos),"Reload does not duplicate reference counts");
        int farX=pos.getX()+100000,farZ=pos.getZ()+100000;
        h.assertTrue(h.getLevel().getChunkSource().getChunkNow(farX>>4,farZ>>4)==null,"Distant chunk starts unloaded");
        for(int i=0;i<1000;i++)h.assertFalse(HolySaltBarrierTracker.hasSaltColumn(h.getLevel(),farX+i,farZ),"No salt in unloaded space");
        h.assertTrue(h.getLevel().getChunkSource().getChunkNow(farX>>4,farZ>>4)==null,"Collision lookups never load distant chunks");h.succeed();
    }
    @GameTest(template="deferred_items_test",timeoutTicks=180)
    public static void loaded_salt_enclosure_cleanses_and_stops_after_opening(GameTestHelper h){
        for(int x=3;x<=7;x++)for(int z=3;z<=7;z++){
            var pos=h.absolutePos(new BlockPos(x,3,z));h.getLevel().setBlock(pos.below(),Blocks.STONE.defaultBlockState(),3);
            if(x==3 || x==7 || z==3 || z==7)salt(h,pos);
        }
        var boundaryMobs=new java.util.ArrayList<net.minecraft.world.entity.animal.Cow>();
        for(int x:new int[]{3,7}){
            var boundary=EntityType.COW.create(h.getLevel());boundary.setNoAi(true);boundary.setPersistenceRequired();
            boundary.setPos(h.absolutePos(new BlockPos(x,3,x)).getBottomCenter());h.getLevel().addFreshEntity(boundary);boundaryMobs.add(boundary);
        }
        var cow=EntityType.COW.create(h.getLevel());cow.setNoAi(true);cow.setPersistenceRequired();cow.setPos(h.absolutePos(new BlockPos(5,3,5)).getBottomCenter());h.getLevel().addFreshEntity(cow);
        h.startSequence().thenWaitUntil(()->h.assertTrue(cow.hasEffect(ModEffects.SANCTIFIED),"Active loaded salt still sanctifies the enclosed area"))
            .thenExecute(()->h.getLevel().removeBlock(h.absolutePos(new BlockPos(3,3,5)),false)).thenIdle(25)
            .thenExecute(()->{h.assertFalse(cow.hasEffect(ModEffects.SANCTIFIED),"Opening the enclosure stops refreshing sanctification");cow.discard();boundaryMobs.forEach(net.minecraft.world.entity.Entity::discard);}).thenSucceed();
    }
}
