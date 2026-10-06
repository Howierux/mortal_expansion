package com.howierux.mortalexpansion.guide;

import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public final class SpectralInsight {
    public static final int ALL=15;
    private SpectralInsight(){}
    public static int secrets(ItemStack book){return !book.has(ModDataComponents.ECTCETERA_TIER.get())?ALL:book.getOrDefault(ModDataComponents.ECTCETERA_SECRETS.get(),0)&ALL;}
    public static int allowedAtTier(int tier){return tier<2?0:tier==2?7:ALL;}
    public static boolean canCraft(Player player,net.minecraft.resources.ResourceLocation id){
        int required=InsightSecret.required(id);
        return required==0 || (player.getData(ModAttachments.ECTCETERA_KNOWLEDGE)&required)!=0
                || player instanceof ServerPlayer server && server.getRecipeBook().contains(id);
    }
    public static void learnPlayer(ServerPlayer p,int bits){
        int before=p.getData(ModAttachments.ECTCETERA_KNOWLEDGE),after=(before|bits)&ALL;
        p.setData(ModAttachments.ECTCETERA_KNOWLEDGE,after);
        var recipes=new java.util.ArrayList<net.minecraft.world.item.crafting.RecipeHolder<?>>();
        for(var secret:InsightSecret.values())if((after&secret.bit)!=0)for(var id:secret.recipes())p.serverLevel().getRecipeManager().byKey(id).ifPresent(recipes::add);
        p.awardRecipes(recipes);
        if(after!=before){
            p.inventoryMenu.slotsChanged(p.inventoryMenu.getSlot(1).container);
            if(p.containerMenu instanceof net.minecraft.world.inventory.CraftingMenu)p.containerMenu.slotsChanged(p.containerMenu.getSlot(1).container);
            if(p.containerMenu instanceof net.minecraft.world.inventory.SmithingMenu smithing)smithing.createResult();
        }
    }
    public static void absorb(ServerPlayer p,ItemStack book){
        if(!book.is(ModItems.BOOK_OF_ECTCETERA.get()) || EctceteraProgression.tier(book)<2)return;
        int bits=secrets(book)&allowedAtTier(EctceteraProgression.tier(book));
        int playerBits=p.getData(ModAttachments.ECTCETERA_KNOWLEDGE);
        if((bits&~playerBits)!=0)learnPlayer(p,bits);
        int combined=(bits|p.getData(ModAttachments.ECTCETERA_KNOWLEDGE))&allowedAtTier(EctceteraProgression.tier(book));
        if(book.has(ModDataComponents.ECTCETERA_TIER.get()) && combined!=secrets(book))book.set(ModDataComponents.ECTCETERA_SECRETS.get(),combined);
    }
    public static InsightSecret next(int tier,int mask,int personalTier){
        for(var secret:InsightSecret.values())if(tier>=secret.bookTier && personalTier>=secret.personalTier
                && (mask&secret.bit)==0 && (mask&secret.prerequisites)==secret.prerequisites)return secret;
        return null;
    }
    public static boolean perform(ServerPlayer p){
        // Absorb shared/legacy knowledge first so inventory order cannot reveal an already-known secret.
        for(int slot=0;slot<p.getInventory().getContainerSize();slot++)absorb(p,p.getInventory().getItem(slot));
        ItemStack selected=ItemStack.EMPTY;InsightSecret chosen=null;
        for(int slot=0;slot<p.getInventory().getContainerSize();slot++){
            var book=p.getInventory().getItem(slot);
            if(!book.is(ModItems.BOOK_OF_ECTCETERA.get()) || EctceteraProgression.tier(book)<2)continue;
            absorb(p,book);var candidate=next(EctceteraProgression.tier(book),secrets(book),PlayerHauntManager.getTier(p).id());
            if(candidate!=null && (selected.isEmpty() || EctceteraProgression.tier(book)>EctceteraProgression.tier(selected))){selected=book;chosen=candidate;}
        }
        if(chosen==null)return false;
        selected.set(ModDataComponents.ECTCETERA_SECRETS.get(),secrets(selected)|chosen.bit);
        learnPlayer(p,chosen.bit);p.getInventory().setChanged();p.containerMenu.broadcastChanges();
        p.displayClientMessage(Component.translatable("message.mortal_expansion.insight.revealed",
                Component.translatable("insight.mortal_expansion."+chosen.key()+".name"),Component.translatable("insight.mortal_expansion."+chosen.key()+".next")),false);
        return true;
    }
    @SubscribeEvent public static void login(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer p))return;
        // Migrate existing recipe-book unlocks; generated ingredient advancements no longer grant these secrets.
        int bits=p.getData(ModAttachments.ECTCETERA_KNOWLEDGE);
        for(var secret:InsightSecret.values())if(secret.recipes().stream().anyMatch(id->p.getRecipeBook().contains(id)))bits|=secret.bit|secret.prerequisites;
        learnPlayer(p,bits);
    }
    @SubscribeEvent public static void teachCrafter(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event){
        if(!(event.getEntity() instanceof ServerPlayer p) || !(p.serverLevel().getBlockEntity(event.getPos()) instanceof net.minecraft.world.level.block.entity.CrafterBlockEntity crafter))return;
        int bits=p.getData(ModAttachments.ECTCETERA_KNOWLEDGE);
        crafter.getPersistentData().putInt("MortalExpansionEctceteraKnowledge",crafter.getPersistentData().getInt("MortalExpansionEctceteraKnowledge")|bits);crafter.setChanged();
        // missing function: owner/knowledge integration for other mods' automated crafting machines.
    }
}
