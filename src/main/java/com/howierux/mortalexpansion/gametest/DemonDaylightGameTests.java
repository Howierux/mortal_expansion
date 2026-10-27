package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.guide.DarkholdWorldData;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DemonDaylightGameTests {
    private static BlockPos exposed(GameTestHelper h){
        var level=h.getLevel();var pos=h.absolutePos(new BlockPos(4,3,4));
        level.setBlock(pos.below(),Blocks.STONE.defaultBlockState(),3);
        for(int y=pos.getY();y<level.getMaxBuildHeight();y++)level.setBlock(new BlockPos(pos.getX(),y,pos.getZ()),Blocks.AIR.defaultBlockState(),3);
        return pos;
    }
    @GameTest(template="deferred_items_test",batch="demon_day_spawn",timeoutTicks=100)
    public static void bright_day_spawns_require_abominable_or_higher_and_world_awakening(GameTestHelper h){
        var level=h.getLevel();long time=level.getDayTime();var previous=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());
        var pos=exposed(h);var chunk=level.getChunkAt(pos);int units=HauntManager.getHundredths(level,chunk.getPos());
        level.setDayTime(6000);
        h.runAfterDelay(10,()->{
            try{
                h.assertTrue(level.isDay() && level.canSeeSky(pos) && level.getBrightness(LightLayer.SKY,pos)>=14,"Placement test is in bright exposed daylight");
                level.getDataStorage().set(DarkholdWorldData.KEY,new DarkholdWorldData());HauntManager.setValue(level,chunk.getPos(),50);
                h.assertFalse(SpawnPlacements.checkSpawnRules(ModEntities.DEMON.get(),level,MobSpawnType.NATURAL,pos,level.random),"High Haunt still requires the first Darkhold");
                DarkholdWorldData.get(level).awaken();
                for(int value:new int[]{39,40,49,50,59,60,69}){
                    HauntManager.setValue(level,chunk.getPos(),value);
                    h.assertTrue(SpawnPlacements.checkSpawnRules(ModEntities.DEMON.get(),level,MobSpawnType.NATURAL,pos,level.random)==(value>=50),"Bright daylight becomes eligible strictly above Demonic, at 50");
                }
                level.setBlock(pos.below(),Blocks.AIR.defaultBlockState(),3);
                h.assertFalse(SpawnPlacements.checkSpawnRules(ModEntities.DEMON.get(),level,MobSpawnType.NATURAL,pos,level.random),"Daylight exception preserves native supporting-ground rules");
                h.succeed();
            }finally{
                level.setDayTime(time);HauntManager.setValue(level,chunk.getPos(),0);HauntManager.addHundredths(level,chunk.getPos(),units);
                level.getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(previous,level.registryAccess()));
            }
        });
    }
    @GameTest(template="deferred_items_test",batch="demon_sun_safety",timeoutTicks=240)
    public static void demons_ignore_sunlight_and_need_no_daytime_shelter_at_void(GameTestHelper h){
        var level=h.getLevel();long time=level.getDayTime();var pos=exposed(h);var chunk=level.getChunkAt(pos);
        int units=HauntManager.getHundredths(level,chunk.getPos());level.setDayTime(6000);HauntManager.setValue(level,chunk.getPos(),0);
        var demon=ModEntities.DEMON.get().create(level);demon.setPos(pos.getBottomCenter());demon.setNoAi(true);demon.setPersistenceRequired();level.addFreshEntity(demon);
        h.runAfterDelay(200,()->{
            try{
                h.assertTrue(level.isDay() && level.canSeeSky(pos),"Sunlight test remains exposed at noon");
                h.assertTrue(HauntDaylightRules.sunlightProtected(demon) && !HauntDaylightRules.applies(demon),"Demon has intrinsic sunlight protection, with no shelter behavior");
                h.assertTrue(HauntDaylightRules.sustains(level,pos,demon),"Void chunk does not invalidate a sunlight-immune Demon");
                var move=new net.minecraft.world.phys.Vec3(32,0,0);
                h.assertTrue(HauntDaylightRules.limitMovement(demon,move).equals(move),"Sunlight does not restrict Demon chunk crossings");
                h.assertTrue(demon.isAlive() && !demon.isOnFire() && demon.getHealth()==demon.getMaxHealth(),"Ten seconds of exposed sun causes neither fire nor damage");
                h.succeed();
            }finally{demon.discard();level.setDayTime(time);HauntManager.setValue(level,chunk.getPos(),0);HauntManager.addHundredths(level,chunk.getPos(),units);}
        });
    }
}
