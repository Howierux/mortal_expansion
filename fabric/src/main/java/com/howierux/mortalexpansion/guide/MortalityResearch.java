package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
public final class MortalityResearch {
 public static int amount(ItemStack stack){return stack.is(Items.ROTTEN_FLESH)?1:stack.is(Items.BONE)?5:stack.is(CatalogItems.RAW_ECTOPLASM)?10:stack.is(CatalogItems.PURE_ECTOPLASM)?20:0;}
 public static int bonus(ServerPlayer p){return KnowledgeData.get((AttachmentTarget)p,KnowledgeData.BONUS);}
 public static int ecto(ServerPlayer p){return KnowledgeData.get((AttachmentTarget)p,KnowledgeData.ECTO);}
 public static void spent(ServerPlayer p){((AttachmentTarget)p).setAttached(KnowledgeData.BONUS,0);((AttachmentTarget)p).setAttached(KnowledgeData.ECTO,0);}
 public static boolean remaining(ServerPlayer p){int mask=SpectralInsight.knowledge(p);for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(book.is(CatalogItems.BOOK_OF_ECTCETERA))mask|=SpectralInsight.secrets(book)&SpectralInsight.allowedAtTier(EctceteraProgression.tier(book));}return(mask&127)!=127;}
 public static boolean contribute(ServerPlayer p,ItemStack input){
  int value=amount(input);if(!p.isAlive()||p.isSpectator()||value==0||bonus(p)>=100||!remaining(p))return false;
  ((AttachmentTarget)p).setAttached(KnowledgeData.BONUS,Math.min(100,bonus(p)+value));int type=input.is(CatalogItems.RAW_ECTOPLASM)?1:input.is(CatalogItems.PURE_ECTOPLASM)?2:0;if(type!=0)((AttachmentTarget)p).setAttached(KnowledgeData.ECTO,ecto(p)|type);input.shrink(1);
  p.sendSystemMessage(Component.translatable("message.mortal_expansion.research.added",bonus(p)));if(bonus(p)==100&&!complete(p))p.sendSystemMessage(Component.translatable("message.mortal_expansion.research.waiting"));return true;
 }
 public static boolean complete(ServerPlayer p){if(bonus(p)<100||!SpectralInsight.ready(p)||!SpectralInsight.perform(p))return false;spent(p);GuideRuntime.completed(p);p.containerMenu.broadcastChanges();return true;}
 public static double chance(double normal,int bonus){return Math.clamp(normal+bonus/100.0,0,1);}
}
