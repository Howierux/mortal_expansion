package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpawnBalanceGameTests {
    private static void weight(GameTestHelper h,Biome biome,EntityType<?> type,int expected){
        var matches=biome.getMobSettings().getMobs(type.getCategory()).unwrap().stream().filter(entry->entry.type==type).toList();
        h.assertTrue(matches.size()==1 && matches.getFirst().getWeight().asInt()==expected,"Native modified biome has one correctly weighted entry for "+type);
    }
    @GameTest(template="deferred_items_test")
    public static void generated_spawn_modifiers_apply_once_and_elementals_stay_in_spiritwood(GameTestHelper h){
        var registry=h.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
        var forest=registry.getOrThrow(ModBiomes.SPIRITWOOD_FOREST);var graveyard=registry.getOrThrow(ModBiomes.GRAVEYARD);
        for(var biome:java.util.List.of(forest,graveyard)){
            weight(h,biome,ModEntities.SPIRIT.get(),24);weight(h,biome,ModEntities.GHOST.get(),36);
            weight(h,biome,ModEntities.PHANTOM.get(),14);weight(h,biome,ModEntities.HAUNT.get(),8);weight(h,biome,ModEntities.DEMON.get(),12);
        }
        weight(h,forest,ModEntities.ELEMENTAL.get(),12);
        h.assertTrue(graveyard.getMobSettings().getMobs(MobCategory.CREATURE).unwrap().stream().noneMatch(entry->entry.type==ModEntities.ELEMENTAL.get()),"Elementals do not spill into other Overworld biomes");
        h.succeed();
    }
    private static boolean accepted(GameTestHelper h,Mob mob,MobSpawnType reason){
        var event=new FinalizeSpawnEvent(mob,h.getLevel(),mob.getX(),mob.getY(),mob.getZ(),h.getLevel().getCurrentDifficultyAt(mob.blockPosition()),reason,null,null);
        event.setSpawnCancelled(false);HauntSpawnHandler.onFinalizeSpawn(event);return !event.isSpawnCancelled();
    }
    @GameTest(template="deferred_items_test",batch="spawn_balance")
    public static void common_hostile_reduction_and_elemental_haunt_gate_preserve_explicit_spawns(GameTestHelper h){
        var level=h.getLevel();var pos=h.absolutePos(new BlockPos(4,3,4));var chunk=level.getChunkAt(pos);int saved=HauntManager.getHundredths(level,chunk.getPos());
        try{
            var elemental=ModEntities.ELEMENTAL.get().create(level);elemental.setPos(pos.getBottomCenter());
            HauntManager.setValue(level,chunk.getPos(),19);h.assertFalse(accepted(h,elemental,MobSpawnType.NATURAL),"Natural Elementals require Supernatural Haunt");
            h.assertTrue(accepted(h,elemental,MobSpawnType.COMMAND),"Explicit summons keep working below the natural threshold");
            HauntManager.setValue(level,chunk.getPos(),20);h.assertTrue(accepted(h,elemental,MobSpawnType.NATURAL),"Supernatural minimum is inclusive");
            for(var type:java.util.List.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.CREEPER)){
                var mob=type.create(level);mob.setPos(pos.getBottomCenter());mob.getRandom().setSeed(123456);
                HauntManager.setValue(level,chunk.getPos(),45);int count=0;
                for(int i=0;i<2000;i++)if(accepted(h,mob,MobSpawnType.NATURAL))count++;
                h.assertTrue(count>1600 && count<1800,"Eligible common hostiles retain approximately 85% acceptance");
                h.assertTrue(accepted(h,mob,MobSpawnType.COMMAND),"Reduction never cancels explicit summoned hostiles");
            }
            var zombie=EntityType.ZOMBIE.create(level);zombie.setPos(pos.getBottomCenter());zombie.getRandom().setSeed(98765);
            HauntManager.setValue(level,chunk.getPos(),15);int count=0;for(int i=0;i<2000;i++)if(accepted(h,zombie,MobSpawnType.NATURAL))count++;
            h.assertTrue(count>270 && count<410,"Natural-tier Zombie scarcity is preserved with the additional 15% reduction");
        }finally{HauntManager.setValue(level,chunk.getPos(),0);HauntManager.addHundredths(level,chunk.getPos(),saved);}
        h.succeed();
    }
}
