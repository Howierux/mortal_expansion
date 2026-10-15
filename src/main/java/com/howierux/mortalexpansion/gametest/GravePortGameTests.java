package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.*;
import com.howierux.mortalexpansion.spiritwell.GravePorting;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GravePortGameTests {
    private static GraveBlockEntity grave(ServerLevel level,BlockPos pos,ServerPlayer player){
        for(var at:BlockPos.betweenClosed(pos.offset(-3,-1,-3),pos.offset(3,-1,3)))level.setBlock(at,Blocks.STONE.defaultBlockState(),3);
        for(var at:BlockPos.betweenClosed(pos.offset(-3,0,-3),pos.offset(3,3,3)))level.setBlock(at,Blocks.AIR.defaultBlockState(),3);
        level.setBlock(pos,ModBlocks.GRAVE.get().defaultBlockState(),3);
        var grave=(GraveBlockEntity)level.getBlockEntity(pos);grave.beginNaming(player);
        grave.trySetInscription(player,player.getGameProfile().getName());grave.setNaturallyGenerated(true);grave.onLoad();return grave;
    }
    private static MortalGolemBaseBlockEntity frame(ServerLevel level,BlockPos pos,int flesh){
        level.setBlock(pos,ModBlocks.MORTAL_GOLEM_BASE.get().defaultBlockState(),3);
        var base=(MortalGolemBaseBlockEntity)level.getBlockEntity(pos);base.addFlesh(flesh);return base;
    }
    private static BlockPos well(GameTestHelper h,ServerPlayer player){
        var well=h.absolutePos(new BlockPos(5,2,5));h.getLevel().setBlock(well,ModBlocks.SPIRITWELL.get().defaultBlockState(),3);
        player.setPos(well.getCenter().add(0,0,2));return well;
    }
    @GameTest(template="deferred_items_test")
    public static void eligibility_requires_owner_naturalization_64_flesh_and_three_block_radius(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var q=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(16,2,5));
        var grave=grave(h.getLevel(),pos,p);var base=frame(h.getLevel(),pos.offset(3,0,0),63);
        h.assertFalse(GravePorting.eligible(p,h.getLevel(),pos),"63 flesh cannot enable a port");base.addFlesh(1);
        h.assertTrue(GravePorting.eligible(p,h.getLevel(),pos),"Full frame at exactly three blocks enables the named owner");
        h.assertFalse(GravePorting.eligible(q,h.getLevel(),pos),"Other players cannot use this Grave");
        grave.setNaturallyGenerated(false);h.assertFalse(GravePorting.eligible(p,h.getLevel(),pos),"Placed non-natural Grave cannot port");grave.setNaturallyGenerated(true);
        h.getLevel().removeBlock(base.getBlockPos(),false);frame(h.getLevel(),pos.offset(3,1,0),64);
        h.assertFalse(GravePorting.eligible(p,h.getLevel(),pos),"Three-block radius includes vertical distance");
        grave.setRemoved();h.assertFalse(GravePorting.destinations(p).stream().anyMatch(d->d.pos().equals(pos)),"Unloaded/removed Graves leave the live destination index");
        p.discard();q.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void port_consumes_frame_once_adds_one_point_and_revalidates_missing_resources(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var well=well(h,p);var pos=h.absolutePos(new BlockPos(16,2,5));grave(h.getLevel(),pos,p);
        var base=frame(h.getLevel(),pos.offset(3,0,0),64);PlayerHauntManager.setValue(p,15);
        String token=GravePorting.open(p,well);var list=GravePorting.destinations(p);int index=0;while(!list.get(index).pos().equals(pos))index++;
        h.assertTrue(GravePorting.choose(p,token,index),"Valid menu selection ports the player");
        h.assertTrue(PlayerHauntManager.getValue(p)==16 && h.getLevel().getBlockState(base.getBlockPos()).isAir()
                && h.getLevel().getBlockState(pos).is(ModBlocks.GRAVE.get()),"Only frame is consumed and exactly one personal point is added");
        h.assertFalse(GravePorting.choose(p,token,index),"Selection token cannot be replayed");
        p.setPos(well.getCenter().add(0,0,2));base=frame(h.getLevel(),pos.offset(3,0,0),64);token=GravePorting.open(p,well);
        h.getLevel().removeBlock(base.getBlockPos(),false);
        h.assertFalse(GravePorting.choose(p,token,index),"Broken frame invalidates an already-open menu");
        h.assertTrue(PlayerHauntManager.getValue(p)==16,"Rejected port spends no Haunt");
        base=frame(h.getLevel(),pos.offset(3,0,0),64);
        for(var at:BlockPos.betweenClosed(pos.offset(-2,0,-2),pos.offset(2,4,2)))if(!at.equals(pos))h.getLevel().setBlock(at,Blocks.STONE.defaultBlockState(),3);
        token=GravePorting.open(p,well);
        h.assertFalse(GravePorting.choose(p,token,index),"Blocked landing rejects the selected port");
        h.assertTrue(!base.isRemoved() && base.flesh()==64 && PlayerHauntManager.getValue(p)==16,"Blocked arrival preserves flesh and Haunt");
        for(var at:BlockPos.betweenClosed(pos.offset(-2,0,-2),pos.offset(2,4,2)))if(!at.equals(pos))h.getLevel().setBlock(at,Blocks.AIR.defaultBlockState(),3);
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void loaded_other_dimensions_are_listed_and_cancelled_travel_preserves_frame(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var well=well(h,p);var other=p.server.getLevel(net.minecraft.world.level.Level.NETHER);
        var pos=new BlockPos(0,90,0);other.getChunk(pos);var grave=grave(other,pos,p);var base=frame(other,pos.offset(3,0,0),64);
        var choices=GravePorting.destinations(p);int index=-1;
        for(int i=0;i<choices.size();i++)if(choices.get(i).dimension().equals(other.dimension().location().toString()) && choices.get(i).pos().equals(pos))index=i;
        h.assertTrue(index>=0,"Loaded eligible Graves in another dimension are listed");
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent> cancel=e->{if(e.getEntity()==p)e.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);int before=PlayerHauntManager.getValue(p);
        try{String token=GravePorting.open(p,well);h.assertFalse(GravePorting.choose(p,token,index),"Canceled cross-dimension travel is rejected");}
        finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
        h.assertTrue(!base.isRemoved() && PlayerHauntManager.getValue(p)==before,"Canceled travel preserves all resources");
        String token=GravePorting.open(p,well);h.assertTrue(GravePorting.choose(p,token,index) && p.serverLevel()==other,"Cross-dimension teleport reaches the chosen Grave");
        h.assertTrue(other.getBlockState(base.getBlockPos()).isAir() && PlayerHauntManager.getValue(p)==before+1,"Cross-dimension success consumes one frame and charges one point");
        other.removeBlock(pos,false);GravePorting.unloaded(other,pos);p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void void_damage_bypasses_spectral_immunity(GameTestHelper h){
        for(var type:java.util.List.of(ModEntities.GHOST.get(),ModEntities.SPIRIT.get(),ModEntities.HAINT.get(),ModEntities.PHARAOHL.get())){
            var mob=type.create(h.getLevel());float before=mob.getHealth();mob.hurt(mob.damageSources().fellOutOfWorld(),4);
            h.assertTrue(mob.getHealth()==before-4,"Intangible creatures receive ordinary void damage");mob.discard();
        }
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new net.minecraft.world.item.ItemStack(ModItems.GHOST_GARB_HELMET.get()));
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,new net.minecraft.world.item.ItemStack(ModItems.GHOST_GARB_CHESTPLATE.get()));
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,new net.minecraft.world.item.ItemStack(ModItems.GHOST_GARB_LEGGINGS.get()));
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,new net.minecraft.world.item.ItemStack(ModItems.GHOST_GARB_BOOTS.get()));
        float before=p.getHealth();p.hurt(p.damageSources().fellOutOfWorld(),4);h.assertTrue(p.getHealth()==before-4,"Full garb cannot prevent void damage");p.discard();h.succeed();
    }
}
