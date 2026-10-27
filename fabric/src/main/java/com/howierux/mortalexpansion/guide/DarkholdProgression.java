package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
public final class DarkholdProgression {
 public static final AttachmentType<Integer> KNOWN=AttachmentRegistry.create(com.howierux.mortalexpansion.catalog.Catalog.id("damned_knowledge"),b->b.initializer(()->0).persistent(Codec.intRange(0,255)).copyOnDeath().syncWith(ByteBufCodecs.VAR_INT,AttachmentSyncPredicate.targetOnly()));
 private static final AttachmentType<Long> LAST=AttachmentRegistry.create(com.howierux.mortalexpansion.catalog.Catalog.id("darkhold_exposure"),b->b.initializer(()->-1L));
 public static void initialize(){}
 private static void remember(ServerPlayer p,int bits){
  int old=((AttachmentTarget)p).getAttachedOrCreate(KNOWN);for(var secret:DamnedSecret.values())if((bits&secret.bit())!=0&&(old&secret.bit())==0)p.sendSystemMessage(net.minecraft.network.chat.Component.translatable("damned.mortal_expansion.reveal",net.minecraft.network.chat.Component.translatable("darkhold.mortal_expansion."+secret.key()+".name")));
  if((bits&1)!=0&&!canSeeDemon(p))p.sendSystemMessage(net.minecraft.network.chat.Component.translatable("darkhold.mortal_expansion.revealed"));
  ((AttachmentTarget)p).setAttached(KNOWN,bits);
 }

 public static void crafted(ServerPlayer p,ItemStack book){if(!isDarkhold(book))return;DarkholdWorldData.get(p.serverLevel()).awaken();SpectralInsight.learnPlayer(p,SpectralInsight.ALL);int bits=((AttachmentTarget)p).getAttachedOrCreate(KNOWN)|book.getOrDefault(KnowledgeData.DAMNED_SECRETS,0)|DamnedSecret.revealedAt(PlayerHauntManager.getValue(p));remember(p,bits);book.set(KnowledgeData.DAMNED_SECRETS,bits);}
 public static boolean canSeeDemon(Player p){return p!=null&&((((AttachmentTarget)p).getAttachedOrCreate(KNOWN)&1)!=0);}
 public static boolean isDarkhold(ItemStack stack){return stack.is(CatalogItems.BOOK_OF_ECTCETERA)&&EctceteraProgression.tier(stack)==4;}
 /** Low eight bits are read knowledge; bit eight marks a found Darkhold. Never mutate an opaque inventory. */
 private static int scan(ItemStack stack,int depth,int[] budget){
  if(stack.isEmpty()||depth>8||budget[0]--<=0)return 0;if(isDarkhold(stack))return 256|stack.getOrDefault(KnowledgeData.DAMNED_SECRETS,0);
  int found=0;var container=stack.get(DataComponents.CONTAINER);if(container!=null)for(var child:container.nonEmptyItems())found|=scan(child,depth+1,budget);
  var bundle=stack.get(DataComponents.BUNDLE_CONTENTS);if(bundle!=null)for(var child:bundle.items())found|=scan(child,depth+1,budget);
  var storage=net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.ITEM.find(stack,net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.withConstant(stack));
  if(storage!=null)for(var view:storage){if(budget[0]--<=0)break;if(!view.isResourceBlank()&&view.getAmount()>0)found|=scan(view.getResource().toStack(1),depth+1,budget);}
  return found;
 }
 public static void pulse(ServerPlayer p){pulse(p,p.getServer().overworld().getGameTime());}
 public static void pulse(ServerPlayer p,long now){
  boolean carried=false;int bits=((AttachmentTarget)p).getAttachedOrCreate(KNOWN);int[] budget={4096};
  for(int i=0;i<p.getInventory().getContainerSize();i++){var stack=p.getInventory().getItem(i);int found=scan(stack,0,budget);carried|=(found&256)!=0;bits|=found&255;}
  var attached=(AttachmentTarget)p;
  if(!carried||!p.isAlive()||p.isSpectator()){attached.setAttached(LAST,-1L);return;}
  DarkholdWorldData.get(p.serverLevel()).awaken();SpectralInsight.learnPlayer(p,SpectralInsight.ALL);long last=attached.getAttachedOrCreate(LAST);
  if(last<0||now<last||now-last>=200){attached.setAttached(LAST,now);if(last>=0&&now>=last)PlayerHauntManager.addHundredths(p,1);}
  bits|=DamnedSecret.revealedAt(PlayerHauntManager.getValue(p));
  remember(p,bits);
  for(int i=0;i<p.getInventory().getContainerSize();i++){var stack=p.getInventory().getItem(i);if(isDarkhold(stack)&&stack.getOrDefault(KnowledgeData.DAMNED_SECRETS,0)!=bits)stack.set(KnowledgeData.DAMNED_SECRETS,bits);}

 }
}
