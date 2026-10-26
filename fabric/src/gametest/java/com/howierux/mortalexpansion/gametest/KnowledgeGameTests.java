package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.machine.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.*;
import java.util.*;

public final class KnowledgeGameTests implements FabricGameTest {
 private static ServerPlayer player(GameTestHelper h){var cookie=net.minecraft.server.network.CommonListenerCookie.createInitial(new com.mojang.authlib.GameProfile(UUID.randomUUID(),"knowledge-test"),false);var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),cookie.gameProfile(),cookie.clientInformation());var c=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(c);h.getLevel().getServer().getPlayerList().placeNewPlayer(c,p,cookie);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setPos(h.absolutePos(new BlockPos(2,2,2)).getCenter());return p;}
 private static void remove(ServerPlayer p){p.getServer().getPlayerList().remove(p);p.discard();}
 private static BlockPos at(GameTestHelper h,int x){return h.absolutePos(new BlockPos(x,2,2));}
 private static ItemStack book(int tier){return EctceteraProgression.book(tier);}
 @GameTest(template=EMPTY_STRUCTURE,batch="knowledge")
 public void native_upgrades_preserve_components_and_station_returns_every_book_tier(GameTestHelper h){
  var level=h.getLevel();var base=CraftingInput.of(2,1,List.of(new ItemStack(Items.BOOK),new ItemStack(Items.ROTTEN_FLESH)));var recipe=level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,base,level).orElseThrow();var stack=recipe.value().assemble(base,level.registryAccess());
  h.assertTrue(stack.is(CatalogItems.BOOK_OF_ECTCETERA)&&EctceteraProgression.tier(stack)==0,"Book plus flesh crafts the starter tier");
  stack.set(DataComponents.CUSTOM_NAME,Component.literal("Remembered"));stack.set(KnowledgeData.BOOK_SECRETS,64);
  for(int tier=0;tier<4;tier++){
   var input=CraftingInput.of(2,1,List.of(stack,new ItemStack(EctceteraProgression.upgradeIngredient(tier))));
   var upgrade=level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,input,level).orElseThrow();stack=upgrade.value().assemble(input,level.registryAccess());
   h.assertTrue(EctceteraProgression.tier(stack)==tier+1&&stack.getHoverName().getString().equals("Remembered")&&(SpectralInsight.secrets(stack)&64)!=0,"Sequential upgrade keeps custom name and learned secrets");
   h.assertTrue(tier==3?SpectralInsight.secrets(stack)==127:SpectralInsight.secrets(stack)==64,"Ordinary upgrades do not automatically reveal new secrets; Darkhold reveals all");
  }
  for(int tier=0;tier<=4;tier++){
   var original=book(tier);original.set(DataComponents.CUSTOM_NAME,Component.literal("Tier "+tier));original.set(KnowledgeData.BOOK_SECRETS,64);
   var slots=new ArrayList<ItemStack>(Collections.nCopies(9,ItemStack.EMPTY));slots.set(1,original);slots.set(3,new ItemStack(CatalogItems.HOLY_SALT));slots.set(4,new ItemStack(Items.OBSIDIAN));slots.set(5,new ItemStack(CatalogItems.HOLY_SALT));for(int i=6;i<9;i++)slots.set(i,new ItemStack(Items.OBSIDIAN));
   var input=CraftingInput.of(3,3,slots);var station=level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,input,level).orElseThrow();var returned=station.value().getRemainingItems(input).get(1);
   h.assertTrue(station.value().assemble(input,level.registryAccess()).is(CatalogItems.MORTALITY_RESEARCH_STATION)&&ItemStack.isSameItemSameComponents(original,returned),"Station returns exactly the tier/components supplied: "+tier);
  }h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="knowledge")
 public void crafting_and_inventory_results_are_gated_even_when_recipe_book_is_awarded(GameTestHelper h){
  var p=player(h);try{
   var pos=at(h,1);h.getLevel().setBlockAndUpdate(pos,Blocks.CRAFTING_TABLE.defaultBlockState());var menu=new CraftingMenu(1,p.getInventory(),ContainerLevelAccess.create(h.getLevel(),pos));p.containerMenu=menu;var grid=menu.getSlot(1).container;
   for(int i=0;i<9;i++)grid.setItem(i,new ItemStack(i==4?CatalogItems.RAW_ECTOPLASM:Items.STRING));
   h.assertTrue(menu.getSlot(0).getItem().isEmpty(),"Crafting table blocks undiscovered Ectothread");
   var holder=h.getLevel().getRecipeManager().byKey(Catalog.id("ectothread")).orElseThrow();p.awardRecipes(List.of(holder));menu.slotsChanged(grid);h.assertTrue(menu.getSlot(0).getItem().isEmpty(),"Recipe-book award cannot bypass knowledge");
   SpectralInsight.learnPlayer(p,1);h.assertTrue(menu.getSlot(0).getItem().is(CatalogItems.ECTOTHREAD)&&menu.getSlot(0).getItem().getCount()==8,"Learning refreshes the already-open table and yields eight thread");
   menu.quickMoveStack(p,0);h.assertTrue(grid.isEmpty(),"Shift crafting consumes exactly the real recipe inputs");
   p.containerMenu=p.inventoryMenu;var small=p.inventoryMenu.getSlot(1).container;small.clearContent();small.setItem(0,new ItemStack(Items.BEACON));small.setItem(2,new ItemStack(CatalogItems.HAUNT_FURNACE));p.inventoryMenu.slotsChanged(small);
   h.assertTrue(p.inventoryMenu.getSlot(0).getItem().isEmpty(),"Two-by-two inventory also blocks locked altar");SpectralInsight.learnPlayer(p,16);h.assertTrue(p.inventoryMenu.getSlot(0).getItem().is(CatalogItems.ALTAR_OF_IMPURITY),"Inventory result updates when its secret is learned");
  }finally{remove(p);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="knowledge")
 public void template_free_smithing_and_real_crafter_enforce_permanent_knowledge(GameTestHelper h){
  var p=player(h);try{
   var level=h.getLevel();var pos=at(h,1);level.setBlockAndUpdate(pos,Blocks.SMITHING_TABLE.defaultBlockState());var smith=new SmithingMenu(2,p.getInventory(),ContainerLevelAccess.create(level,pos));p.containerMenu=smith;
   var helmet=new ItemStack(CatalogItems.GHOST_GARB_HELMET);helmet.set(DataComponents.CUSTOM_NAME,Component.literal("Woven"));helmet.setDamageValue(2);smith.getSlot(1).set(helmet);smith.getSlot(2).set(new ItemStack(CatalogItems.PURE_ECTOPLASM));smith.createResult();h.assertTrue(smith.getSlot(3).getItem().isEmpty(),"Unknown smithing secret has no result");
   SpectralInsight.learnPlayer(p,4);var result=smith.getSlot(3).getItem();h.assertTrue(result.is(CatalogItems.HAINT_GARB_HELMET)&&result.getDamageValue()==2&&result.getHoverName().getString().equals("Woven"),"Known template-free upgrade preserves base components");
   var crafterPos=at(h,4);level.setBlockAndUpdate(crafterPos,Blocks.CRAFTER.defaultBlockState());var crafter=(CrafterBlockEntity)level.getBlockEntity(crafterPos);for(int i=0;i<9;i++)crafter.setItem(i,new ItemStack(i==4?CatalogItems.RAW_ECTOPLASM:Items.STRING));
   level.getBlockState(crafterPos).tick(level,crafterPos,level.random);h.assertTrue(crafter.getItem(4).is(CatalogItems.RAW_ECTOPLASM),"Uneducated vanilla Crafter does not consume locked inputs");
   SpectralInsight.learnPlayer(p,1);UseBlockCallback.EVENT.invoker().interact(p,level,InteractionHand.MAIN_HAND,new BlockHitResult(crafterPos.getCenter(),Direction.UP,crafterPos,false));
   var saved=crafter.saveWithFullMetadata(level.registryAccess());var loaded=BlockEntity.loadStatic(crafterPos,crafter.getBlockState(),saved,level.registryAccess());h.assertTrue((KnowledgeData.get((AttachmentTarget)loaded,KnowledgeData.CRAFTER)&1)!=0,"Taught Crafter knowledge persists in native block-entity NBT");
   level.getBlockState(crafterPos).tick(level,crafterPos,level.random);h.assertTrue(crafter.getItem(4).isEmpty(),"Taught Crafter performs the actual recipe");
   int count=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(crafterPos).inflate(3)).stream().filter(e->e.getItem().is(CatalogItems.ECTOTHREAD)).mapToInt(e->e.getItem().getCount()).sum();h.assertTrue(count==8,"Crafter emits the correct output without duplication");
  }finally{remove(p);}h.succeed();
 }
 @GameTest(template=EMPTY_STRUCTURE,batch="knowledge")
 public void research_reveals_gradually_stacks_both_ectoplasms_and_copies_on_respawn(GameTestHelper h){
  var p=player(h);var other=player(h);ServerPlayer respawn=null;
  try{
   var level=h.getLevel();var starter=book(0);p.getInventory().setItem(0,starter);
   h.assertTrue(SpectralInsight.perform(p)&&SpectralInsight.knowledge(p)==64&&SpectralInsight.secrets(starter)==64,"First whisper reveals Research Station with starter book");
   level.setBlockAndUpdate(at(h,1),CatalogBlocks.MORTALITY_RESEARCH_STATION.defaultBlockState());var station=(ResearchStationEntity)level.getBlockEntity(at(h,1));var menu=new ResearchMenu(3,p.getInventory(),station,station.data(p));p.containerMenu=menu;
   station.setItem(0,new ItemStack(CatalogItems.RAW_ECTOPLASM,2));h.assertTrue(menu.clickMenuButton(p,0)&&menu.clickMenuButton(p,0),"Raw ectoplasm contributions repeat");station.setItem(0,new ItemStack(CatalogItems.PURE_ECTOPLASM));h.assertTrue(menu.clickMenuButton(p,0)&&MortalityResearch.bonus(p)==40&&MortalityResearch.ecto(p)==3,"Purified stacks with raw and both checkboxes persist");
   h.assertTrue(MortalityResearch.bonus(other)==0&&SpectralInsight.knowledge(other)==0,"Shared station does not share player bonuses or knowledge");
   station.setItem(0,new ItemStack(Items.BONE));p.setPos(at(h,1).offset(100,0,0).getCenter());h.assertFalse(menu.clickMenuButton(p,0),"Out-of-range button cannot spend materials");p.setPos(at(h,1).getCenter());
   for(int i=0;i<3;i++)MortalityResearch.contribute(p,new ItemStack(CatalogItems.PURE_ECTOPLASM));h.assertTrue(MortalityResearch.bonus(p)==100&&SpectralInsight.knowledge(p)==64,"Full bonus waits rather than spending when next book/Haunt tiers are ineligible");
   var aware=book(2);p.getInventory().setItem(0,aware);com.howierux.mortalexpansion.haunt.PlayerHauntManager.setValue(p,25);h.assertTrue(MortalityResearch.complete(p)&&MortalityResearch.bonus(p)==0&&(SpectralInsight.knowledge(p)&1)!=0&&(SpectralInsight.knowledge(p)&2)==0,"Full research reveals only one eligible secret, independently of cooldown");
   MortalityResearch.contribute(p,new ItemStack(CatalogItems.RAW_ECTOPLASM));MortalityResearch.contribute(p,new ItemStack(CatalogItems.PURE_ECTOPLASM));int known=SpectralInsight.knowledge(p);var tag=p.saveWithoutId(new CompoundTag());p.load(tag);h.assertTrue(MortalityResearch.bonus(p)==30&&MortalityResearch.ecto(p)==3&&SpectralInsight.knowledge(p)==known,"Native player NBT restores research and knowledge");
   respawn=p.getServer().getPlayerList().respawn(p,false,net.minecraft.world.entity.Entity.RemovalReason.KILLED);h.assertTrue(MortalityResearch.bonus(respawn)==30&&MortalityResearch.ecto(respawn)==3&&SpectralInsight.knowledge(respawn)==known,"Actual death respawn retains earned knowledge and pending research");
  }finally{remove(respawn==null?p:respawn);remove(other);}h.succeed();
 }
}
