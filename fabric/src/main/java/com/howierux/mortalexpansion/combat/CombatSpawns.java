package com.howierux.mortalexpansion.combat;
import com.howierux.mortalexpansion.entity.CombatEntities;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.guide.DarkholdWorldData;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
public final class CombatSpawns {
 public static boolean eligible(EntityType<?> type,ServerLevel level,net.minecraft.core.BlockPos pos){
  int tier=HauntManager.getTier(level,pos).id();
  if(type==CombatEntities.DEMON)return DarkholdWorldData.get(level).awakened()&&tier>=4;
  return tier>=(type==CombatEntities.SPIRIT?1:type==CombatEntities.ELEMENTAL?2:type==CombatEntities.GHOST?3:type==CombatEntities.HAINT?5:4);
 }
 public static boolean bypassLight(EntityType<?> type,boolean day,int tier){return type==CombatEntities.DEMON&&day&&tier>4;}
 public static void initialize(){
  for(var type:CombatEntities.MOBS.values())if(type!=CombatEntities.MORTAL_GOLEM)register(type);
  var overworld=BiomeSelectors.foundInOverworld();
  BiomeModifications.addSpawn(overworld,MobCategory.CREATURE,CombatEntities.SPIRIT,24,1,2);
  BiomeModifications.addSpawn(overworld,MobCategory.MONSTER,CombatEntities.GHOST,36,1,2);
  BiomeModifications.addSpawn(overworld,MobCategory.MONSTER,CombatEntities.PHANTOM,14,1,1);
  BiomeModifications.addSpawn(overworld,MobCategory.MONSTER,CombatEntities.HAUNT,8,1,1);
  BiomeModifications.addSpawn(overworld,MobCategory.MONSTER,EntityType.WITHER_SKELETON,5,1,2);BiomeModifications.addSpawn(overworld,MobCategory.MONSTER,EntityType.GHAST,2,1,1);
  BiomeModifications.addSpawn(BiomeSelectors.includeByKey(ModBiomes.SPIRITWOOD_FOREST),MobCategory.CREATURE,CombatEntities.ELEMENTAL,12,1,2);
  BiomeModifications.addSpawn(overworld.or(BiomeSelectors.foundInTheNether()).or(BiomeSelectors.foundInTheEnd()),MobCategory.MONSTER,CombatEntities.DEMON,12,1,1);
  // Loaded structure encounters and player-built formation are registered by ContentRuntime.
 }
 private static <T extends Mob> void register(EntityType<T> type){
  SpawnPlacements.register(type,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(entityType,world,reason,pos,random)->{
   if(reason==MobSpawnType.NATURAL&&world instanceof ServerLevel level){
    if(!eligible(type,level,pos))return false;
    if(type.getCategory()==MobCategory.MONSTER){boolean daylight=bypassLight(type,level.isDay(),HauntManager.getTier(level,pos).id());return daylight?Monster.checkAnyLightMonsterSpawnRules((EntityType<? extends Monster>)(EntityType<?>)entityType,world,reason,pos,random):Monster.checkMonsterSpawnRules((EntityType<? extends Monster>)(EntityType<?>)entityType,world,reason,pos,random);}
   }
   // Chunk generation deliberately does not read saved Haunt or load other chunks.
   return Mob.checkMobSpawnRules(entityType,world,reason,pos,random);
  });
 }
}
