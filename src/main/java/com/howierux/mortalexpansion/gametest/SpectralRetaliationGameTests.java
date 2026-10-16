package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.entity.SpectralBoltEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpectralRetaliationGameTests {
    private static <T extends Entity> T spawn(GameTestHelper h,EntityType<T> type,int x){
        var entity=type.create(h.getLevel());var pos=h.absolutePos(new BlockPos(x,3,5));
        entity.setPos(pos.getCenter());entity.setNoGravity(true);if(entity instanceof Mob mob)mob.setPersistenceRequired();h.getLevel().addFreshEntity(entity);return entity;
    }
    private static void arena(GameTestHelper h){
        for(int x=1;x<=11;x++)for(int z=2;z<=8;z++){h.setBlock(new BlockPos(x,2,z),Blocks.STONE);for(int y=3;y<=7;y++)h.setBlock(new BlockPos(x,y,z),Blocks.AIR);}
    }
    @GameTest(template="deferred_items_test",timeoutTicks=180)
    public static void spirit_is_passive_then_retaliates_against_a_mob(GameTestHelper h){
        arena(h);var spirit=spawn(h,ModEntities.SPIRIT.get(),3);spirit.setNoGravity(false);spirit.setNoAi(true);var attacker=spawn(h,EntityType.ZOMBIE,9);attacker.setNoAi(true);attacker.setItemSlot(EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_HELMET));
        h.runAfterDelay(20,()->{
            h.assertTrue(spirit.getTarget()==null && attacker.getHealth()==attacker.getMaxHealth(),"Unprovoked spirit remains passive");
            spirit.setNoAi(false);h.assertTrue(spirit.hurt(spirit.damageSources().indirectMagic(attacker,attacker),1),"Attacker damages spirit");
        });
        h.runAfterDelay(140,()->{
            h.assertTrue(attacker.getHealth()<attacker.getMaxHealth(),"Spirit AI fires ranged bolts that hit the damaging mob");
            spirit.discard();attacker.discard();h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=180)
    public static void elemental_retaliates_against_a_player(GameTestHelper h){
        arena(h);var elemental=spawn(h,ModEntities.ELEMENTAL.get(),3);elemental.setNoGravity(false);elemental.setNoAi(true);var player=h.makeMockServerPlayerInLevel();
        player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);player.getFoodData().setFoodLevel(18);player.setNoGravity(true);player.setPos(h.absolutePos(new BlockPos(9,3,5)).getCenter());
        h.assertTrue(elemental.getTarget()==null,"Unprovoked elemental has no target");
        h.runAfterDelay(20,()->{elemental.setNoAi(false);h.assertTrue(elemental.hurt(elemental.damageSources().indirectMagic(player,player),1),"Player damages elemental");});
        h.runAfterDelay(140,()->{
            h.assertTrue(player.getHealth()<player.getMaxHealth(),"Elemental retaliates: health="+player.getHealth()+" target="+elemental.getTarget()+" shooter="+elemental.position()+" victim="+player.position());
            elemental.discard();player.discard();h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=60)
    public static void bolt_hits_intangible_targets_and_stops_at_blocks(GameTestHelper h){
        arena(h);var shooter=spawn(h,ModEntities.ELEMENTAL.get(),3);shooter.setNoAi(true);
        var ghost=spawn(h,ModEntities.GHOST.get(),9);ghost.setNoAi(true);
        h.assertTrue(com.howierux.mortalexpansion.entity.IntangibleEntities.isIntangible(ghost),"Test target is intangible");
        SpectralBoltEntity.fire(shooter,ghost);
        h.runAfterDelay(15,()->{
            h.assertTrue(ghost.getHealth()<ghost.getMaxHealth(),"Bolt hits intangible: health="+ghost.getHealth()+" shooter="+shooter.position()+" victim="+ghost.position()+" bolts="+h.getLevel().getEntitiesOfClass(SpectralBoltEntity.class,shooter.getBoundingBox().inflate(40)).stream().map(b->b.position().toString()).toList());
            ghost.invulnerableTime=0;float health=ghost.getHealth();
            for(int y=3;y<=6;y++)for(int z=3;z<=7;z++)h.setBlock(new BlockPos(6,y,z),Blocks.STONE);
            SpectralBoltEntity.fire(shooter,ghost);
            h.runAfterDelay(15,()->{
                h.assertTrue(ghost.getHealth()==health,"Solid wall intercepts the second projectile");
                shooter.discard();ghost.discard();h.succeed();
            });
        });
    }
}
