package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
/** One exposure pulse per carrier, with bounded read-only traversal of nested inventories. */
public final class DarkholdProgression {
    public static final int REVEAL_AT=70,INTERVAL=200;
    private DarkholdProgression(){}
    public static boolean isDarkhold(ItemStack stack){return stack.is(ModItems.BOOK_OF_ECTCETERA.get()) && EctceteraProgression.tier(stack)==4;}
    public static boolean contains(ItemStack stack){return find(stack,0,new int[]{4096},java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));}
    private static boolean find(ItemStack stack,int depth,int[] budget,java.util.Set<ItemStack> visited){
        if(stack.isEmpty() || depth>8 || budget[0]--<=0 || !visited.add(stack))return false;
        if(isDarkhold(stack))return true;
        var container=stack.get(DataComponents.CONTAINER);
        if(container!=null)for(var child:container.nonEmptyItems())if(find(child,depth+1,budget,visited))return true;
        var bundle=stack.get(DataComponents.BUNDLE_CONTENTS);
        if(bundle!=null)for(var child:bundle.items())if(find(child,depth+1,budget,visited))return true;
        var inventory=stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM);
        if(inventory!=null)for(int slot=0;slot<inventory.getSlots() && budget[0]>0;slot++)if(find(inventory.getStackInSlot(slot),depth+1,budget,visited))return true;
        return false;
    }
    public static boolean carries(ServerPlayer player){for(int i=0;i<player.getInventory().getContainerSize();i++)if(contains(player.getInventory().getItem(i)))return true;return false;}
    public static void absorb(ServerPlayer player,ItemStack book){
        if(!isDarkhold(book))return;
        DarkholdWorldData.get(player.serverLevel()).awaken();
        if(player.getData(ModAttachments.ECTCETERA_KNOWLEDGE)!=SpectralInsight.ALL)SpectralInsight.learnPlayer(player,SpectralInsight.ALL);
        int known=player.getData(ModAttachments.DAMNED_KNOWLEDGE)|book.getOrDefault(ModDataComponents.DAMNED_SECRETS.get(),0);
        if(PlayerHauntManager.getValue(player)>=REVEAL_AT)known|=1;
        if((known&1)!=0 && (player.getData(ModAttachments.DAMNED_KNOWLEDGE)&1)==0)
            player.sendSystemMessage(Component.translatable("darkhold.mortal_expansion.revealed"));
        player.setData(ModAttachments.DAMNED_KNOWLEDGE,known);
        if(book.getOrDefault(ModDataComponents.DAMNED_SECRETS.get(),0)!=known)book.set(ModDataComponents.DAMNED_SECRETS.get(),known);
    }
    public static boolean pulse(ServerPlayer player,long now){
        if(!player.isAlive() || player.isSpectator() || !carries(player)){player.setData(ModAttachments.DARKHOLD_EXPOSURE_TIME,-1L);return false;}
        DarkholdWorldData.get(player.serverLevel()).awaken();
        if(player.getData(ModAttachments.ECTCETERA_KNOWLEDGE)!=SpectralInsight.ALL)SpectralInsight.learnPlayer(player,SpectralInsight.ALL);
        long last=player.getData(ModAttachments.DARKHOLD_EXPOSURE_TIME);
        boolean gained=last>=0 && now>=last && now-last>=INTERVAL;
        if(last<0 || now<last || gained){player.setData(ModAttachments.DARKHOLD_EXPOSURE_TIME,now);if(gained)PlayerHauntManager.addHundredths(player,1);}
        if(PlayerHauntManager.getValue(player)>=REVEAL_AT && (player.getData(ModAttachments.DAMNED_KNOWLEDGE)&1)==0){
            player.setData(ModAttachments.DAMNED_KNOWLEDGE,1);
            player.sendSystemMessage(Component.translatable("darkhold.mortal_expansion.revealed"));
        }
        for(int i=0;i<player.getInventory().getContainerSize();i++){var stack=player.getInventory().getItem(i);if(isDarkhold(stack))absorb(player,stack);}
        player.containerMenu.broadcastChanges();return gained;
    }
    @SubscribeEvent public static void tick(PlayerTickEvent.Post event){if(event.getEntity() instanceof ServerPlayer player && player.level().getGameTime()%20==0)pulse(player,player.getServer().overworld().getGameTime());}
    @SubscribeEvent public static void crafted(PlayerEvent.ItemCraftedEvent event){if(event.getEntity() instanceof ServerPlayer player && isDarkhold(event.getCrafting())){absorb(player,event.getCrafting());}}
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event){if(event.getEntity() instanceof ServerPlayer player)player.setData(ModAttachments.DARKHOLD_EXPOSURE_TIME,-1L);}
}
