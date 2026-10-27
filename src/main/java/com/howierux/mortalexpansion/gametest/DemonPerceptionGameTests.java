package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.mob.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DemonPerceptionGameTests {
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h){
        var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"demon-test-player"),false);
        var player=new net.minecraft.server.level.ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());
        var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);
        new io.netty.channel.embedded.EmbeddedChannel(connection);
        h.getLevel().getServer().getPlayerList().placeNewPlayer(connection,player,cookie);
        return player;
    }
    private static void search(DemonEntity demon){for(int i=0;i<80;i++){demon.getSensing().tick();demon.targetSelector.tick();}}
    @GameTest(template="deferred_items_test",batch="demon_perception")
    public static void demons_acquire_distant_survival_players_through_thick_walls_but_lose_them_behind_spectral_solid(GameTestHelper h){
        var level=h.getLevel();var origin=h.absolutePos(new BlockPos(2048,10,2048));
        for(int x=0;x<=112;x++)for(int y=0;y<4;y++)level.setBlock(origin.offset(x,y,0),Blocks.AIR.defaultBlockState(),3);
        for(int x=20;x<28;x++)for(int y=0;y<4;y++)level.setBlock(origin.offset(x,y,0),Blocks.STONE.defaultBlockState(),3);
        var p=player(h);p.setGameMode(GameType.SURVIVAL);p.setPos(origin.offset(80,0,0).getBottomCenter());
        var demon=ModEntities.DEMON.get().create(level);demon.setPos(origin.getBottomCenter());demon.setNoAi(true);
        try{
            h.assertTrue(demon.getAttributeValue(Attributes.FOLLOW_RANGE)==96 && demon.hasLineOfSight(p),"Demon sees eighty blocks away through eight layers of ordinary stone");
            var ghost=ModEntities.GHOST.get().create(level);ghost.setPos(demon.position());
            h.assertFalse(SpectralPhasingRules.canTrackThroughBlocks(ghost,p),"Other spectral mobs retain their four-layer tracking limit");
            search(demon);h.assertTrue(demon.getTarget()==p,"The actual native target selector acquires a distant Survival player");
            var shield=origin.offset(40,1,0);level.setBlock(shield,ModBlocks.GRAVESTONE.get().defaultBlockState(),3);
            h.assertFalse(demon.hasLineOfSight(p) || new SpectralMeleeAttackGoal(demon,1).canUse(),"Demi-tangible material blocks both perception and direct pursuit");
            search(demon);h.assertTrue(demon.getTarget()==null,"Shielding drops the player target without ordinary unseen-target memory");
            level.setBlock(shield,Blocks.AIR.defaultBlockState(),3);search(demon);h.assertTrue(demon.getTarget()==p,"Removing shielding permits reacquisition");
            p.setGameMode(GameType.CREATIVE);search(demon);h.assertTrue(demon.getTarget()==null,"Creative mode is not hunted");
            p.setGameMode(GameType.SPECTATOR);search(demon);h.assertTrue(demon.getTarget()==null,"Spectators and Spirit Journey players are not hunted");
            p.setGameMode(GameType.SURVIVAL);p.setPos(origin.offset(104,0,0).getBottomCenter());search(demon);
            h.assertTrue(demon.getTarget()==null && !demon.hasLineOfSight(p),"Detection and target search remain bounded to the follow range");
            p.setPos(origin.offset(80,0,0).getBottomCenter());demon.setAlliedOwner(p.getUUID());search(demon);
            h.assertTrue(demon.getTarget()==null,"Allied Demons retain the existing player-target prohibition");
        }finally{demon.discard();p.discard();}
        h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="demon_perception_save")
    public static void perception_upgrades_old_default_range_preserves_custom_ranges_and_never_loads_distant_terrain(GameTestHelper h){
        var level=h.getLevel();var old=ModEntities.DEMON.get().create(level);old.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(32);
        var saved=old.saveWithoutId(new CompoundTag());saved.remove("DemonPerceptionVersion");
        var restored=ModEntities.DEMON.get().create(level);restored.load(saved);
        h.assertTrue(restored.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()==96,"Pre-update Demons migrate their saved thirty-two-block default");
        old.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48);saved=old.saveWithoutId(new CompoundTag());saved.remove("DemonPerceptionVersion");restored.load(saved);
        h.assertTrue(restored.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()==48,"Existing custom follow ranges are preserved");
        saved=old.saveWithoutId(new CompoundTag());restored.load(saved);
        h.assertTrue(restored.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()==48 && saved.getInt("DemonPerceptionVersion")==1,"Versioned saves preserve later tuning");
        var far=new BlockPos(24000000,64,24000000);var target=ModEntities.GHOST.get().create(level);old.setPos(far.getBottomCenter());target.setPos(far.offset(32,0,0).getBottomCenter());
        h.assertFalse(level.hasChunkAt(far),"Perception test begins outside loaded terrain");
        h.assertFalse(SpectralPhasingRules.canTrackThroughBlocks(old,target) || level.hasChunkAt(far),"A tracking ray never requests missing terrain");
        h.succeed();
    }
}
