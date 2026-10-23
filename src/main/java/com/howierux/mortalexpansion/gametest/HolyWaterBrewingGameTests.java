package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HolyWaterBrewingGameTests {
    private static ServerPlayer player(GameTestHelper h){
        var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"holy-water-test"),false);
        var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());
        var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);
        new io.netty.channel.embedded.EmbeddedChannel(connection);h.getLevel().getServer().getPlayerList().placeNewPlayer(connection,p,cookie);
        p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);PlayerHauntManager.setValue(p,20);return p;
    }
    private static void remove(ServerPlayer p){p.getServer().getPlayerList().remove(p);p.discard();}
    private static void brew(GameTestHelper h,BlockPos pos,BrewingStandBlockEntity stand){
        for(int tick=0;tick<=400;tick++)BrewingStandBlockEntity.serverTick(h.getLevel(),pos,h.getLevel().getBlockState(pos),stand);
    }
    @GameTest(template="deferred_items_test",batch="holy_water_brewing")
    public static void native_stand_fills_three_bottles_returns_bucket_and_brews_splash(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlockAndUpdate(pos,Blocks.BREWING_STAND.defaultBlockState());
        var stand=(BrewingStandBlockEntity)h.getLevel().getBlockEntity(pos);var p=player(h);
        try{
            var menu=new net.minecraft.world.inventory.BrewingStandMenu(1,p.getInventory(),stand,new net.minecraft.world.inventory.SimpleContainerData(2));
            h.assertTrue(menu.getSlot(3).mayPlace(new ItemStack(ModItems.HOLY_WATER_BUCKET.get())) && menu.getSlot(0).mayPlace(new ItemStack(Items.GLASS_BOTTLE)),"Actual menu accepts the top bucket and empty bottom bottles");
            for(int i=0;i<3;i++)stand.setItem(i,new ItemStack(Items.GLASS_BOTTLE));
            stand.setItem(3,new ItemStack(ModItems.HOLY_WATER_BUCKET.get()));stand.setItem(4,new ItemStack(Items.BLAZE_POWDER));
            brew(h,pos,stand);
            for(int i=0;i<3;i++)h.assertTrue(stand.getItem(i).is(Items.POTION) && stand.getItem(i).getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).is(ModPotions.HOLY_WATER),"Every bottle receives registered Holy Water contents");
            h.assertTrue(stand.getItem(3).is(Items.BUCKET) && stand.getItem(3).getCount()==1 && stand.getItem(4).isEmpty(),"Bucket is returned in ingredient slot; normal blaze fuel is used");
            stand.setItem(3,new ItemStack(Items.GUNPOWDER));brew(h,pos,stand);
            for(int i=0;i<3;i++)h.assertTrue(stand.getItem(i).is(Items.SPLASH_POTION) && stand.getItem(i).getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).is(ModPotions.HOLY_WATER),"Gunpowder converts all three through native container recipes");
            h.assertTrue(stand.getItem(3).isEmpty(),"Exactly one gunpowder consumed for the batch");
            var brewing=h.getLevel().potionBrewing();
            h.assertFalse(brewing.hasMix(new ItemStack(Items.GLASS_BOTTLE,2),new ItemStack(ModItems.HOLY_WATER_BUCKET.get())),"Oversized automated bottle stacks cannot lose extra items");
            h.assertFalse(brewing.hasMix(PotionContents.createItemStack(Items.POTION,Potions.WATER),new ItemStack(ModItems.HOLY_WATER_BUCKET.get())),"Recipe requires empty glass bottles");
            h.assertFalse(brewing.hasMix(new ItemStack(Items.GLASS_BOTTLE),new ItemStack(Items.WATER_BUCKET)),"Ordinary water is not Holy Water");
            var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
            try{ItemStack.STREAM_CODEC.encode(buffer,stand.getItem(0));var copy=ItemStack.STREAM_CODEC.decode(buffer);
                h.assertTrue(copy.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).is(ModPotions.HOLY_WATER),"Potion identity survives native item packets");
            }finally{buffer.release();}
        }finally{remove(p);}h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="holy_water_brewing")
    public static void drinking_returns_glass_and_shares_holy_water_cleansing_cooldown(GameTestHelper h){
        var p=player(h);try{
            var bottle=HolyWaterBrewing.bottle();var returned=bottle.finishUsingItem(h.getLevel(),p);
            h.assertTrue(returned.is(Items.GLASS_BOTTLE) && bottle.isEmpty(),"Native drinking consumes potion and returns one empty bottle");
            h.assertTrue(PlayerHauntManager.getValue(p)==19 && HauntProtection.protectedPlayer(p),"Drinking removes one Haunt and grants the existing blessing");
            h.assertTrue(HauntProtection.state(p).getLong("BlessedUntil")==HauntProtection.now(p)+1200,"Blessing lasts one minute");
            HolyWaterBrewing.bottle().finishUsingItem(h.getLevel(),p);HauntProtection.bless(p);
            h.assertTrue(PlayerHauntManager.getValue(p)==19,"More bottles or fluid contact cannot bypass the shared cooldown");
        }finally{remove(p);}h.succeed();
    }
    private static final class Splash extends net.minecraft.world.entity.projectile.ThrownPotion {
        Splash(net.minecraft.world.level.Level level){super(net.minecraft.world.entity.EntityType.POTION,level);}
        void burst(net.minecraft.world.entity.Entity hit){super.onHit(new net.minecraft.world.phys.EntityHitResult(hit));}
    }
    @GameTest(template="deferred_items_test",batch="holy_water_brewing")
    public static void native_splash_blesses_nearby_players_but_not_distant_targets(GameTestHelper h){
        var owner=player(h);var near=player(h);var far=player(h);var origin=h.absolutePos(new BlockPos(5,2,5)).getCenter();
        owner.setPos(origin);near.setPos(origin.add(2,0,0));far.setPos(origin.add(6,0,0));
        var cow=net.minecraft.world.entity.EntityType.COW.create(h.getLevel());cow.setPos(origin.add(1,0,0));h.getLevel().addFreshEntity(cow);float health=cow.getHealth();
        var splash=new Splash(h.getLevel());splash.setOwner(owner);splash.setItem(HolyWaterBrewing.splash());splash.setPos(origin);
        try{
            HauntProtection.bless(owner);splash.burst(owner);
            h.assertTrue(PlayerHauntManager.getValue(owner)==19 && PlayerHauntManager.getValue(near)==19,"Native splash reaches owner and nearby player without bypassing prior fluid cooldown");
            h.assertTrue(HauntProtection.protectedPlayer(near) && PlayerHauntManager.getValue(far)==20 && !HauntProtection.protectedPlayer(far),"Only players in native splash range are blessed");
            h.assertTrue(cow.getHealth()==health && splash.isRemoved(),"Consecration does not damage mobs; thrown potion shatters normally");
        }finally{splash.discard();cow.discard();remove(owner);remove(near);remove(far);}h.succeed();
    }
}
