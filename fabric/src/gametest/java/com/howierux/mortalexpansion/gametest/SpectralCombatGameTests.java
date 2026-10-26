package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.combat.*;
import com.howierux.mortalexpansion.entity.*;
import com.howierux.mortalexpansion.entity.mob.*;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import java.util.*;
public final class SpectralCombatGameTests implements FabricGameTest {
 private static ServerPlayer player(GameTestHelper h){var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(UUID.randomUUID(),"combat-test"),false);var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());var c=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(c);h.getLevel().getServer().getPlayerList().placeNewPlayer(c,p,cookie);p.setPos(h.absolutePos(new BlockPos(2,2,2)).getCenter());return p;}
 private static void remove(ServerPlayer p){p.getServer().getPlayerList().remove(p);p.discard();}
 private static <T extends Mob> T spawn(GameTestHelper h,EntityType<T> type,int x){T mob=type.create(h.getLevel());mob.setNoAi(true);mob.setPos(h.absolutePos(new BlockPos(x,2,3)).getCenter());h.getLevel().addFreshEntity(mob);return mob;}
 private static float hit(LivingEntity target,DamageSource source,float amount){target.invulnerableTime=0;float before=target.getHealth();target.hurt(source,amount);return before-target.getHealth();}
 private static boolean near(float a,float b){return Math.abs(a-b)<.02F;}
 @GameTest(template=EMPTY_STRUCTURE,batch="spectral_combat")
 public void real_damage_rules_magic_weapons_demons_boss_phases_and_void(GameTestHelper h){
  var p=player(h);var ghost=spawn(h,CombatEntities.GHOST,4);var demon=spawn(h,CombatEntities.DEMON,6);var boss=spawn(h,CombatEntities.PHARAOHL,8);
  try{
   p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND_SWORD));h.assertTrue(hit(ghost,p.damageSources().playerAttack(p),4)==0,"Ordinary weapon cannot hurt an intangible ghost");
   h.assertTrue(hit(ghost,p.damageSources().magic(),4)==4,"Native magic tag bypasses intangibility");
   p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(CatalogItems.SPECTRALITE_SWORD));var source=p.damageSources().playerAttack(p);h.assertTrue(source.is(com.howierux.mortalexpansion.effect.ModDamageTypes.DEMI_TANGIBLE_PLAYER)&&DemiTangibleDamage.canDamageIntangible(source),"Native melee factory tags spectral weapon damage as magic");
   h.assertTrue(hit(ghost,source,4)==4&&hit(demon,source,4)==2,"Demon halves demi-weapon damage while ghost takes ordinary damage");
   h.assertTrue(hit(demon,p.damageSources().magic(),4)==4,"Demon does not halve unrelated magic spells");
   p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(CatalogItems.PURIFIED_GRAVESTONE_SWORD));h.assertTrue(hit(ghost,p.damageSources().playerAttack(p),4)==6,"Purified gravestone sword has its spectral bonus");
   p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND_SWORD));boss.tickCount=0;h.assertTrue(near(hit(boss,p.damageSources().playerAttack(p),4),6),"Tangible Pharaohl is vulnerable to mundane weapons");boss.tickCount=160;h.assertTrue(hit(boss,p.damageSources().playerAttack(p),4)==0,"Spectral Pharaohl rejects mundane damage");
   h.assertTrue(hit(ghost,p.damageSources().fellOutOfWorld(),1)==1,"Void damage always reaches intangibles");
  }finally{remove(p);ghost.discard();demon.discard();boss.discard();}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="spectral_combat")
 public void native_anvil_accepts_any_item_and_projectiles_keep_launch_permission(GameTestHelper h){
  var p=player(h);try{
   var level=h.getLevel();var enchant=level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(CombatRuntime.DEMI_ENCHANTMENT);var book=new ItemStack(Items.ENCHANTED_BOOK);book.enchant(enchant,1);h.assertFalse(enchant.value().canEnchant(new ItemStack(Items.STICK)),"Test-only empty supported-items tag exercises unsupported mod-item fallback");
   var pos=h.absolutePos(new BlockPos(1,2,2));level.setBlockAndUpdate(pos,Blocks.ANVIL.defaultBlockState());var menu=new AnvilMenu(1,p.getInventory(),ContainerLevelAccess.create(level,pos));menu.getSlot(0).set(new ItemStack(Items.STICK));menu.getSlot(1).set(book);menu.createResult();
   h.assertTrue(DemiTangibleDamage.isDemiTangibleWeapon(menu.getSlot(2).getItem()),"Real anvil applies Demi-Tangible to an ordinarily unenchantable item");
   var bow=new ItemStack(Items.BOW);bow.enchant(enchant,1);p.setItemInHand(InteractionHand.MAIN_HAND,bow);p.getInventory().setItem(9,new ItemStack(Items.ARROW,4));
   var arrow=new Arrow(level,p,new ItemStack(Items.ARROW),bow);level.addFreshEntity(arrow);p.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
   h.assertTrue(DemiTangibleProjectiles.damage(p.damageSources().arrow(arrow,p)),"Arrow keeps firing-weapon permission after the bow is removed");
   var tag=arrow.saveWithoutId(new CompoundTag());var loaded=new Arrow(EntityType.ARROW,level);loaded.load(tag);h.assertTrue(DemiTangibleProjectiles.marked(loaded),"Projectile provenance survives native NBT");
   var plain=new Arrow(level,p,new ItemStack(Items.ARROW),new ItemStack(Items.BOW));level.addFreshEntity(plain);p.setItemInHand(InteractionHand.MAIN_HAND,bow);h.assertFalse(DemiTangibleProjectiles.damage(p.damageSources().arrow(plain,p)),"Equipping an enchanted bow after launch does not upgrade ordinary arrows");arrow.discard();plain.discard();
  }finally{remove(p);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="spectral_combat")
 public void phasing_keeps_floors_and_barriers_clear_on_actual_removal(GameTestHelper h){
  var level=h.getLevel();var ghost=spawn(h,CombatEntities.GHOST,3);try{
   var at=ghost.blockPosition();for(int dx=-2;dx<=5;dx++)for(int dz=-2;dz<=2;dz++)level.setBlockAndUpdate(at.offset(dx,-1,dz),Blocks.STONE.defaultBlockState());
   for(int y=0;y<4;y++)level.setBlockAndUpdate(at.offset(1,y,0),Blocks.STONE.defaultBlockState());double x=ghost.getX();ghost.move(MoverType.SELF,new Vec3(1,0,0));h.assertTrue(ghost.getX()>x+.9,"Ghost passes a mundane wall");
   double y=ghost.getY();ghost.move(MoverType.SELF,new Vec3(0,-1,0));h.assertTrue(ghost.getY()>=at.getY()-.01,"Downward movement retains ordinary floor collision");
   y=ghost.getY();level.setBlockAndUpdate(ghost.blockPosition().above(2),Blocks.STONE.defaultBlockState());ghost.move(MoverType.SELF,new Vec3(0,1,0));h.assertTrue(ghost.getY()>y+.9,"Ghost passes upward through an ordinary ceiling");
   var destination=ghost.getBoundingBox().move(1,0,0);var barrier=BlockPos.containing(destination.getCenter());level.setBlockAndUpdate(barrier,CatalogBlocks.PURIFIED_GRAVESTONE.defaultBlockState());h.assertFalse(SpectralPhasingRules.canEnterHorizontally(ghost,destination),"Spectral-solid material blocks phasing");level.removeBlock(barrier,false);
   var salt=barrier.below(3);level.setBlockAndUpdate(salt,CatalogBlocks.HOLY_SALT.defaultBlockState());h.assertTrue(HolySaltBarrierTracker.hasSaltColumn(level,salt.getX(),salt.getZ()),"Actual block transition creates salt column");level.removeBlock(salt,false);h.assertFalse(HolySaltBarrierTracker.hasSaltColumn(level,salt.getX(),salt.getZ()),"Breaking final salt immediately removes its column");
   var a=at.offset(0,0,4);var b=a.offset(4,0,0);level.setBlockAndUpdate(a,CatalogBlocks.SPIRIT_TORCH.defaultBlockState());level.setBlockAndUpdate(b,CatalogBlocks.SPIRIT_TORCH.defaultBlockState());h.assertTrue(SpiritTorchBarrierTracker.hasBarrierColumn(level,a.getX()+2,a.getZ()),"Aligned torches create a linked wall");level.removeBlock(a,false);h.assertFalse(SpiritTorchBarrierTracker.hasBarrierColumn(level,a.getX()+2,a.getZ()),"Broken torch immediately clears linked wall");level.removeBlock(b,false);
  }finally{ghost.discard();}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="spectral_combat")
 public void all_mobs_create_and_death_children_retaliation_and_golem_tiers_work(GameTestHelper h){
  var level=h.getLevel();var created=new ArrayList<Mob>();try{
   int x=1;for(var type:CombatEntities.MOBS.values()){var mob=spawn(h,type,x++);created.add(mob);h.assertTrue(mob.getMaxHealth()>0,"Registered mob has native attributes: "+type);}
   var mummy=(MummyEntity)created.stream().filter(m->m instanceof MummyEntity).findFirst().orElseThrow();int before=level.getEntitiesOfClass(GhostEntity.class,mummy.getBoundingBox().inflate(3)).size();mummy.hurt(mummy.damageSources().fellOutOfWorld(),1000);h.assertTrue(level.getEntitiesOfClass(GhostEntity.class,mummy.getBoundingBox().inflate(3)).size()>before,"Mummy death creates a ghost");
   var spirit=(SpiritEntity)created.stream().filter(m->m instanceof SpiritEntity).findFirst().orElseThrow();var target=(MortalGolemEntity)created.stream().filter(m->m instanceof MortalGolemEntity).findFirst().orElseThrow();spirit.performRangedAttack(target,1);h.assertTrue(!level.getEntitiesOfClass(SpectralBoltEntity.class,spirit.getBoundingBox().inflate(2)).isEmpty(),"Spirit retaliation fires a native spectral projectile");
   target.setFlesh(64);h.assertTrue(target.fleshTier()==4&&target.getMaxHealth()==100&&target.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)==18.75,"Golem flesh scales health and damage");target.setHealth(50);h.assertFalse(net.minecraft.world.entity.EntityType.SPIDER.create(level).canAttack(target),"Spiders cannot target the Mortal Golem");h.assertTrue(target.canAttackType(CombatEntities.GHOST)&&DemiTangibleDamage.canDamageIntangible(target.damageSources().mobAttack(target)),"Golem can fight spectral targets");
   var tag=target.saveWithoutId(new CompoundTag());var restored=CombatEntities.MORTAL_GOLEM.create(level);restored.load(tag);h.assertTrue(restored.flesh()==64&&restored.getHealth()==50,"Golem state survives native save/load");
   var demon=(DemonEntity)created.stream().filter(m->m instanceof DemonEntity).findFirst().orElseThrow();demon.setSinAffinity(SinAffinity.ENVY);var saved=demon.saveWithoutId(new CompoundTag());var copy=CombatEntities.DEMON.create(level);copy.load(saved);h.assertTrue(copy.sinAffinity()==SinAffinity.ENVY,"Demon affinity survives native save/load");
  }finally{for(var mob:created)mob.discard();}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="spectral_combat")
 public void darkhold_awakened_visibility_exposure_and_spawn_gates_persist(GameTestHelper h){
  var level=h.getLevel();var snapshot=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());var p=player(h);
  try{
   var book=EctceteraProgression.book(4);p.getInventory().setItem(0,book);DarkholdProgression.crafted(p,book);h.assertTrue(DarkholdWorldData.get(level).awakened(),"Crafted Darkhold awakens the shared world");
   PlayerHauntManager.setValue(p,70);DarkholdProgression.pulse(p);h.assertTrue(DarkholdProgression.canSeeDemon(p)&&(book.getOrDefault(KnowledgeData.DAMNED_SECRETS,0)&1)!=0,"Darkhold at 70 reveals permanent Demon sight");p.getInventory().setItem(1,book.copy());DarkholdProgression.pulse(p,100);DarkholdProgression.pulse(p,300);DarkholdProgression.pulse(p,300);h.assertTrue(PlayerHauntManager.getHundredths(p)==7001,"One exposure pulse per carrier, unaffected by duplicate books");p.getInventory().setItem(0,ItemStack.EMPTY);p.getInventory().setItem(1,ItemStack.EMPTY);DarkholdProgression.pulse(p,500);h.assertTrue(DarkholdProgression.canSeeDemon(p),"Demon sight remains after removing book");
   var saved=p.saveWithoutId(new CompoundTag());p.load(saved);h.assertTrue(DarkholdProgression.canSeeDemon(p)&&PlayerHauntManager.getValue(p)==70,"Extended personal Haunt and Demon knowledge persist in native player NBT");
   HauntManager.setValue(level,p.chunkPosition(),35);h.assertFalse(CombatSpawns.eligible(CombatEntities.DEMON,level,p.blockPosition()),"Demon remains gated below Demonic chunk Haunt");HauntManager.setValue(level,p.chunkPosition(),45);h.assertTrue(CombatSpawns.eligible(CombatEntities.DEMON,level,p.blockPosition()),"Awakened world permits Demonic spawns");
  }finally{remove(p);level.getServer().overworld().getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(snapshot,level.registryAccess()));}h.succeed();
 }
}
