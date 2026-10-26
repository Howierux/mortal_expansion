package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class SpectralInsight {
 public static final int ALL=127;
 public static int knowledge(Player p){return KnowledgeData.get((AttachmentTarget)p,KnowledgeData.KNOWLEDGE);}
 public static int secrets(ItemStack book){return EctceteraProgression.tier(book)==4?ALL:book.getOrDefault(KnowledgeData.BOOK_SECRETS,0)&ALL;}
 public static int allowedAtTier(int tier){return tier<2?64:tier==2?87:ALL;}
 public static boolean canCraft(Player p,ResourceLocation id){int bit=InsightSecret.required(id);return bit==0||(knowledge(p)&bit)!=0;}
 public static void learnPlayer(ServerPlayer p,int bits){
  int before=knowledge(p),after=(before|bits)&ALL;if(after==before)return;
  ((AttachmentTarget)p).setAttached(KnowledgeData.KNOWLEDGE,after);
  var recipes=new java.util.ArrayList<net.minecraft.world.item.crafting.RecipeHolder<?>>();for(var secret:InsightSecret.values())if((after&secret.bit)!=0)for(var id:secret.recipes())p.serverLevel().getRecipeManager().byKey(id).ifPresent(recipes::add);p.awardRecipes(recipes);
  p.inventoryMenu.slotsChanged(p.inventoryMenu.getSlot(1).container);
  if(p.containerMenu instanceof net.minecraft.world.inventory.CraftingMenu)p.containerMenu.slotsChanged(p.containerMenu.getSlot(1).container);
  if(p.containerMenu instanceof net.minecraft.world.inventory.SmithingMenu smithing)smithing.createResult();
 }
 public static void absorb(ServerPlayer p,ItemStack book){
  if(!book.is(CatalogItems.BOOK_OF_ECTCETERA))return;
  int allowed=allowedAtTier(EctceteraProgression.tier(book)),bits=secrets(book)&allowed;
  if((bits&~knowledge(p))!=0)learnPlayer(p,bits);
  int combined=(bits|knowledge(p))&allowed;if(combined!=secrets(book))book.set(KnowledgeData.BOOK_SECRETS,combined);
 }
 public static void absorbAll(ServerPlayer p){for(int i=0;i<p.getInventory().getContainerSize();i++)absorb(p,p.getInventory().getItem(i));}
 public static InsightSecret next(int tier,int mask,int personalTier){for(var secret:InsightSecret.values())if(tier>=secret.bookTier&&personalTier>=secret.personalTier&&(mask&secret.bit)==0&&(mask&secret.prerequisites)==secret.prerequisites)return secret;return null;}
 public static boolean ready(ServerPlayer p){
  int known=knowledge(p);for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(book.is(CatalogItems.BOOK_OF_ECTCETERA))known|=secrets(book)&allowedAtTier(EctceteraProgression.tier(book));}
  for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(book.is(CatalogItems.BOOK_OF_ECTCETERA)&&next(EctceteraProgression.tier(book),known,PlayerHauntManager.getTier(p).id())!=null)return true;}return false;
 }
 public static boolean perform(ServerPlayer p){
  if(!p.isAlive()||p.isSpectator())return false;absorbAll(p);ItemStack selected=ItemStack.EMPTY;InsightSecret chosen=null;
  for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(!book.is(CatalogItems.BOOK_OF_ECTCETERA))continue;
   var candidate=next(EctceteraProgression.tier(book),knowledge(p),PlayerHauntManager.getTier(p).id());if(candidate!=null&&(selected.isEmpty()||EctceteraProgression.tier(book)>EctceteraProgression.tier(selected))){selected=book;chosen=candidate;}}
  if(chosen==null)return false;selected.set(KnowledgeData.BOOK_SECRETS,secrets(selected)|chosen.bit);learnPlayer(p,chosen.bit);absorbAll(p);p.getInventory().setChanged();p.inventoryMenu.broadcastChanges();p.containerMenu.broadcastChanges();
  if(chosen==InsightSecret.MORTALITY_RESEARCH_STATION){p.playNotifySound(net.minecraft.sounds.SoundEvents.SOUL_ESCAPE.value(),net.minecraft.sounds.SoundSource.PLAYERS,.35F,1);p.sendSystemMessage(Component.translatable("message.mortal_expansion.insight.first_whisper"));}
  p.sendSystemMessage(Component.translatable("message.mortal_expansion.insight.revealed",Component.translatable("insight.mortal_expansion."+chosen.key()+".name"),Component.translatable("insight.mortal_expansion."+chosen.key()+".next")));return true;
 }
}
