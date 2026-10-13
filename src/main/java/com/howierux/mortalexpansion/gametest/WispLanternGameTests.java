package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.WispLanternBlockEntity;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.entity.mob.GhostEntity;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WispLanternGameTests {
    private static WispLanternBlockEntity lantern(GameTestHelper h) {
        var pos=h.absolutePos(new BlockPos(4,2,4));
        h.getLevel().setBlock(pos,ModBlocks.WISP_O_LANTERN.get().defaultBlockState(),3);
        return (WispLanternBlockEntity)h.getLevel().getBlockEntity(pos);
    }
    @GameTest(template="deferred_items_test")
    public static void night_conversion_preserves_facing_and_spends_exactly_one_point(GameTestHelper h) {
        var level=h.getLevel();long old=level.getDayTime();
        try {
            level.setDayTime(18000);var pos=h.absolutePos(new BlockPos(4,2,4));
            var state=Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING,Direction.EAST);
            level.setBlock(pos,state,3);HauntManager.setValue(level,pos,20);HauntManager.addHundredths(level,pos,7);
            h.assertTrue(WispLanternConversion.convert(level,pos),"Supernatural pumpkin can awaken at night");
            h.assertTrue(level.getBlockState(pos).is(ModBlocks.WISP_O_LANTERN.get())
                    && level.getBlockState(pos).getValue(CarvedPumpkinBlock.FACING)==Direction.EAST
                    && HauntManager.getHundredths(level,pos)==1907,"Facing and fractions survive exactly one Haunt point reduction");
            h.assertFalse(WispLanternConversion.convert(level,pos),"Converted lantern never repeatedly spends Haunt");
            level.setBlock(pos,state,3);HauntManager.setValue(level,pos,19);
            h.assertFalse(WispLanternConversion.convert(level,pos),"Natural tier cannot convert");
            HauntManager.setValue(level,pos,20);level.setDayTime(6000);
            h.assertFalse(WispLanternConversion.convert(level,pos),"Daylight cannot convert");
            var distant=new BlockPos(10000000,80,10000000);level.setDayTime(18000);
            h.assertFalse(WispLanternConversion.convert(level,distant) || level.hasChunkAt(distant),"Conversion cannot load distant terrain");
        } finally {level.setDayTime(old);}
        h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void lantern_glows_faintly_drops_pumpkin_and_persists_its_slow_timer(GameTestHelper h) {
        var level=h.getLevel();long old=level.getDayTime();
        try {
            var lantern=lantern(h);var state=lantern.getBlockState();
            h.assertTrue(state.getLightEmission()==4,"Lantern has a faint light level rather than full Jack o Lantern brightness");
            var drops=Block.getDrops(state,level,lantern.getBlockPos(),lantern);
            h.assertTrue(drops.size()==1 && drops.getFirst().is(Items.CARVED_PUMPKIN)
                    && drops.getFirst().getCount()==1,"Breaking drops exactly one regular Carved Pumpkin");
            level.setDayTime(6000);
            WispLanternBlockEntity.serverTick(level,lantern.getBlockPos(),state,lantern);
            h.assertTrue(lantern.spawnDelay()==24000,"Daylight pauses the twenty-minute nighttime timer");
            level.setDayTime(18000);
            for(int i=0;i<100;i++)WispLanternBlockEntity.serverTick(level,lantern.getBlockPos(),state,lantern);
            h.assertTrue(lantern.spawnDelay()==23900,"Only loaded nighttime ticks advance spawning");
            var restored=new WispLanternBlockEntity(lantern.getBlockPos(),state);
            restored.loadAdditional(lantern.saveWithoutMetadata(level.registryAccess()),level.registryAccess());
            h.assertTrue(restored.spawnDelay()==23900,"Unloading/reloading retains progress without offline catch-up");
        } finally {level.setDayTime(old);}
        h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spawner_waits_for_its_timer_spawns_one_real_ghost_and_obeys_local_cap(GameTestHelper h) {
        var level=h.getLevel();long old=level.getDayTime();
        var player=h.makeMockServerPlayerInLevel();var lantern=lantern(h);
        player.setPos(lantern.getBlockPos().getCenter().add(8,0,0));
        boolean oldMobSpawning=level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING);
        try {
            // Mojang's test world disables natural/spawner activity by default.
            level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(true,level.getServer());
            level.setDayTime(18000);
            var nearby=new AABB(lantern.getBlockPos()).inflate(16);
            for(var ghost:level.getEntitiesOfClass(GhostEntity.class,nearby))ghost.discard();
            var saved=lantern.saveWithoutMetadata(level.registryAccess());saved.putInt("SpawnDelay",2);
            lantern.loadAdditional(saved,level.registryAccess());
            WispLanternBlockEntity.serverTick(level,lantern.getBlockPos(),lantern.getBlockState(),lantern);
            h.assertTrue(level.getEntitiesOfClass(GhostEntity.class,nearby).isEmpty(),"No spawn before the timer expires");
            WispLanternBlockEntity.serverTick(level,lantern.getBlockPos(),lantern.getBlockState(),lantern);
            h.assertTrue(level.getEntitiesOfClass(GhostEntity.class,nearby).size()==1
                    && lantern.spawnDelay()==24000,"Expiry creates one real Ghost and resets the long timer");
            for(int i=1;i<4;i++) {
                var ghost=ModEntities.GHOST.get().create(level);
                ghost.moveTo(lantern.getBlockPos().getX()+0.5,lantern.getBlockPos().getY()+3,
                        lantern.getBlockPos().getZ()+0.5,0,0);level.addFreshEntity(ghost);
            }
            h.assertFalse(lantern.trySpawn(level),"Four nearby Ghosts prevent accumulation");
            level.setDayTime(6000);
            h.assertFalse(lantern.trySpawn(level),"No spawning during daylight");
            for(var ghost:level.getEntitiesOfClass(GhostEntity.class,nearby))ghost.discard();
        } finally {
            level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(oldMobSpawning,level.getServer());
            level.setDayTime(old);player.discard();
        }
        h.succeed();
    }
}
