package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.network.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.player.*;
import java.util.function.Consumer;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DamnedAbilitiesGameTests {
 private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h){
  var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"damned-test"),false);
  var p=new net.minecraft.server.level.ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());
  var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(connection);
  h.getLevel().getServer().getPlayerList().placeNewPlayer(connection,p,cookie);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);return p;
 }
 private static void remove(net.minecraft.server.level.ServerPlayer p){p.getServer().getPlayerList().remove(p);p.discard();}

 @GameTest(template="deferred_items_test",batch="damned")
 public static void inverted_terraces_unlock_at_boundaries_and_persist(GameTestHelper h){
  var p=player(h);var book=EctceteraProgression.book(4);var level=h.getLevel();
  var world=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());
  try{
   PlayerHauntManager.setValue(p,69);DarkholdProgression.absorb(p,book);h.assertTrue(p.getData(ModAttachments.DAMNED_KNOWLEDGE)==0,"No early secrets");
   for(var secret:DamnedSecret.values()){
    PlayerHauntManager.setValue(p,secret.threshold()-1);DarkholdProgression.absorb(p,book);h.assertFalse(secret.unlocked(p),"No early "+secret);
    PlayerHauntManager.setValue(p,secret.threshold());DarkholdProgression.absorb(p,book);
    h.assertTrue(secret.unlocked(p) && EctceteraResearchMap.unlocked(secret.key(),4,127|(p.getData(ModAttachments.DAMNED_KNOWLEDGE)<<7)),"Player and book reveal "+secret);
   }
   h.assertTrue(p.getData(ModAttachments.DAMNED_KNOWLEDGE)==255 && new DarkholdKnowledgePayload(255).secrets()==255,"All seven and legacy Demon bits synchronize");
   var saved=p.saveWithoutId(new CompoundTag());p.setData(ModAttachments.DAMNED_KNOWLEDGE,0);p.load(saved);PlayerHauntManager.setValue(p,0);
   h.assertTrue(DamnedSecret.PRIDE.unlocked(p) && DarkholdProgression.canSeeDemon(p),"Knowledge persists without current Haunt");
  }finally{remove(p);level.getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(world,level.registryAccess()));}h.succeed();
 }
 @GameTest(template="deferred_items_test",batch="damned")
 public static void gluttony_saturation_overflow_uses_native_food_save(GameTestHelper h){
  var p=player(h);try{
   p.setData(ModAttachments.DAMNED_KNOWLEDGE,DamnedSecret.GLUTTONY.bit());p.getFoodData().setFoodLevel(20);p.getFoodData().setSaturation(19);h.assertTrue(p.canEat(false),"Unlocked survival player eats when full");
   var food=new ItemStack(Items.COOKED_BEEF,3);var properties=food.getFoodProperties(p);p.eat(h.getLevel(),food,properties);
   h.assertTrue(p.getFoodData().getSaturationLevel()>20 && p.getFoodData().getFoodLevel()==20,"Saturation uncapped, hunger unchanged");
   float before=p.getFoodData().getSaturationLevel();p.eat(h.getLevel(),food,properties);h.assertTrue(p.getFoodData().getSaturationLevel()>before,"Repeated eating keeps overflow");h.assertTrue(food.getCount()==1,"Meals actually consume food in survival");
   var saved=p.saveWithoutId(new CompoundTag());p.getFoodData().setSaturation(0);p.load(saved);h.assertTrue(p.getFoodData().getSaturationLevel()>20,"Native save preserves saturation");
   p.setData(ModAttachments.DAMNED_KNOWLEDGE,0);p.eat(h.getLevel(),food,properties);h.assertFalse(p.canEat(false),"Locked survival player cannot eat when full");h.assertTrue(p.getFoodData().getSaturationLevel()<=20,"Ordinary food cap preserved");
  }finally{remove(p);}h.succeed();
 }
 @GameTest(template="deferred_items_test",batch="damned")
 public static void lust_uses_courtship_and_wrath_caps_without_weakening_potions(GameTestHelper h){
  var p=player(h);var cow=EntityType.COW.create(h.getLevel());var baby=EntityType.COW.create(h.getLevel());var pos=h.absolutePos(new BlockPos(4,2,4));
  p.setPos(pos.getCenter());cow.setPos(pos.getCenter());baby.setPos(pos.getCenter());baby.setAge(-24000);h.getLevel().addFreshEntity(cow);h.getLevel().addFreshEntity(baby);
  try{
   h.assertTrue(DamnedAbilities.breed(p)==0,"Locked Lust does nothing");p.setData(ModAttachments.DAMNED_KNOWLEDGE,DamnedSecret.LUST.bit()|DamnedSecret.WRATH.bit());
   for(int crouch=0;crouch<3;crouch++){
    p.setShiftKeyDown(true);DamnedAbilities.tick(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Post(p));
    p.setShiftKeyDown(false);DamnedAbilities.tick(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Post(p));
   }
   h.assertTrue(cow.isInLove() && !baby.isInLove(),"Adults court, babies excluded");
   DamnedAbilities.wrath(p);h.assertTrue(p.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier()==0,"First trigger Strength I");DamnedAbilities.wrath(p);DamnedAbilities.wrath(p);
   h.assertTrue(p.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier()==1 && p.getEffect(MobEffects.DAMAGE_BOOST).getDuration()==200,"Cap Strength II for ten seconds");
   p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,500,3));DamnedAbilities.wrath(p);h.assertTrue(p.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier()==3,"Stronger potion preserved");
   p.setData(ModAttachments.DAMNED_KNOWLEDGE,DamnedSecret.PRIDE.bit());var memory=p.getData(ModAttachments.DAMNED_ABILITY_STATE);int xp=p.totalExperience;long now=p.level().getGameTime();
   memory.putLong("pride_time",now-199);DamnedAbilities.tick(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Post(p));h.assertTrue(p.totalExperience==xp,"Pride cannot award early");
   memory.putLong("pride_time",now-200);DamnedAbilities.tick(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Post(p));DamnedAbilities.tick(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Post(p));
   h.assertTrue(p.totalExperience==xp+1,"Pride grants one native XP point per interval, never double-pays");
  }finally{cow.discard();baby.discard();remove(p);}h.succeed();
 }
 @GameTest(template="deferred_items_test",batch="damned")
 public static void greed_consumes_real_ore_and_respects_protection(GameTestHelper h){
  var p=player(h);var pos=h.absolutePos(new BlockPos(4,2,4));p.setPos(pos.getCenter());p.getInventory().setItem(0,new ItemStack(Items.DIAMOND));p.getInventory().selected=0;
  p.setData(ModAttachments.DAMNED_KNOWLEDGE,DamnedSecret.GREED.bit());
  var click=new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem(p,net.minecraft.world.InteractionHand.MAIN_HAND);
  DamnedAbilities.use(click);
  h.assertTrue(click.isCanceled() && p.isUsingItem() && p.getUseItem().getUseDuration(p)==72000,"Diamond enters native held-use tracking");p.stopUsingItem();
  h.getLevel().setBlockAndUpdate(pos,Blocks.DIAMOND_ORE.defaultBlockState());
  Consumer<BlockEvent.BreakEvent> protect=e->{if(e.getPos().equals(pos))e.setCanceled(true);};NeoForge.EVENT_BUS.addListener(protect);
  try{h.assertFalse(DamnedAbilities.takeDiamond(p,pos),"Claim rejects extraction");h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.DIAMOND_ORE),"Protected ore remains");}finally{NeoForge.EVENT_BUS.unregister(protect);}
  try{h.assertTrue(DamnedAbilities.takeDiamond(p,pos),"Allowed ore mined");h.assertTrue(!h.getLevel().getBlockState(pos).is(Blocks.DIAMOND_ORE) && p.getInventory().countItem(Items.DIAMOND)==2,"Exactly one diamond from one ore");h.assertFalse(DamnedAbilities.takeDiamond(p,pos),"No duplicate extraction");}finally{remove(p);}h.succeed();
 }
 @GameTest(template="deferred_items_test",batch="damned")
 public static void sloth_preserves_sleep_failures_and_envy_is_detached(GameTestHelper h){
  var p=player(h);var level=h.getLevel();long time=level.getDayTime();try{
   level.setDayTime(1000);p.setData(ModAttachments.DAMNED_KNOWLEDGE,DamnedSecret.SLOTH.bit());var pos=h.absolutePos(new BlockPos(4,2,4));
   var day=new CanPlayerSleepEvent(p,pos,net.minecraft.world.entity.player.Player.BedSleepingProblem.NOT_POSSIBLE_NOW);DamnedAbilities.sleep(day);h.assertTrue(day.getProblem()==null,"Daytime permitted");
   var other=player(h);p.startSleeping(pos);
   try{var join=new CanPlayerSleepEvent(other,pos,net.minecraft.world.entity.player.Player.BedSleepingProblem.NOT_POSSIBLE_NOW);DamnedAbilities.sleep(join);
    h.assertTrue(join.getProblem()==null && !DamnedSecret.SLOTH.unlocked(other),"Uninitiated online player may join Sloth vote");
   }finally{p.stopSleeping();remove(other);}
   var blocked=new CanPlayerSleepEvent(p,pos,net.minecraft.world.entity.player.Player.BedSleepingProblem.OBSTRUCTED);DamnedAbilities.sleep(blocked);h.assertTrue(blocked.getProblem()!=null,"Obstruction preserved");
   h.assertTrue(DamnedAbilities.majority(1)==1 && DamnedAbilities.majority(2)==2 && DamnedAbilities.majority(3)==2 && DamnedAbilities.majority(4)==3,"Strict majority");
   p.getInventory().setItem(0,new ItemStack(Items.DIAMOND,7));p.getInventory().setItem(36,new ItemStack(Items.IRON_BOOTS));p.getInventory().setItem(40,new ItemStack(Items.SHIELD));
   var items=EnvyPayloads.snapshot(p);items.get(0).setCount(1);h.assertTrue(items.size()==41 && items.get(36).is(Items.IRON_BOOTS) && items.get(40).is(Items.SHIELD) && p.getInventory().getItem(0).getCount()==7,"Equipment included, no mutable access");
   var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),level.registryAccess());
   try{EnvyPayloads.View.CODEC.encode(buffer,new EnvyPayloads.View(p.getUUID().toString(),"reader",items));var decoded=EnvyPayloads.View.CODEC.decode(buffer);
    h.assertTrue(decoded.items().size()==41 && decoded.items().get(40).is(Items.SHIELD),"Private snapshot survives native item packet codec");
   }finally{buffer.release();}
  }finally{level.setDayTime(time);remove(p);}h.succeed();
 }
}
