package com.howierux.mortalexpansion.haunt;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;

/** Percentage-point research bias belongs to the player, not a shared station. */
public final class MortalityResearch {
    private MortalityResearch(){}
    public static int amount(ItemStack item){return item.is(Items.ROTTEN_FLESH)?1:item.is(Items.BONE)?5:item.is(ModItems.RAW_ECTOPLASM.get())?10:item.is(ModItems.PURE_ECTOPLASM.get())?20:0;}
    private static int ecto(ItemStack item){return item.is(ModItems.RAW_ECTOPLASM.get())?1:item.is(ModItems.PURE_ECTOPLASM.get())?2:0;}
    public static int bonus(ServerPlayer p){return p.getData(ModAttachments.MORTALITY_RESEARCH_BONUS);}
    public static boolean remaining(ServerPlayer p){
        int known=p.getData(ModAttachments.ECTCETERA_KNOWLEDGE);
        for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(book.is(ModItems.BOOK_OF_ECTCETERA.get()))known|=SpectralInsight.secrets(book)&SpectralInsight.allowedAtTier(EctceteraProgression.tier(book));}
        return (known&SpectralInsight.ALL)!=SpectralInsight.ALL;
    }
    public static boolean ready(ServerPlayer p){
        int known=p.getData(ModAttachments.ECTCETERA_KNOWLEDGE);
        for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(book.is(ModItems.BOOK_OF_ECTCETERA.get()))known|=SpectralInsight.secrets(book)&SpectralInsight.allowedAtTier(EctceteraProgression.tier(book));}
        for(int i=0;i<p.getInventory().getContainerSize();i++){var book=p.getInventory().getItem(i);if(book.is(ModItems.BOOK_OF_ECTCETERA.get()) && SpectralInsight.next(EctceteraProgression.tier(book),known&SpectralInsight.allowedAtTier(EctceteraProgression.tier(book)),PlayerHauntManager.getTier(p).id())!=null)return true;}
        return false;
    }
    public static boolean contribute(ServerPlayer p,ItemStack item){
        int amount=amount(item),type=ecto(item),contributed=p.getData(ModAttachments.MORTALITY_RESEARCH_ECTO);
        if(!p.isAlive() || p.gameMode.getGameModeForPlayer()==net.minecraft.world.level.GameType.SPECTATOR || amount==0 || bonus(p)>=100 || !remaining(p))return false;
        p.setData(ModAttachments.MORTALITY_RESEARCH_BONUS,Math.min(100,bonus(p)+amount));if(type!=0)p.setData(ModAttachments.MORTALITY_RESEARCH_ECTO,contributed|type);
        item.shrink(1);
        p.sendSystemMessage(Component.translatable("message.mortal_expansion.research.added",bonus(p)));
        if(bonus(p)==100 && !complete(p))
            p.sendSystemMessage(Component.translatable("message.mortal_expansion.research.waiting"));
        return true;
    }
    /** A full charge reveals one eligible secret immediately, independently of random-roll cooldowns. */
    public static boolean complete(ServerPlayer p){
        if(bonus(p)<100 || !p.isAlive() || p.isSpectator() || !ready(p))return false;
        if(!com.howierux.mortalexpansion.haunt.effects.HauntEventController.researchInsight(p))return false;
        spent(p);
        p.containerMenu.broadcastChanges();
        return true;
    }
    public static void spent(ServerPlayer p){p.setData(ModAttachments.MORTALITY_RESEARCH_BONUS,0);p.setData(ModAttachments.MORTALITY_RESEARCH_ECTO,0);}
    public static com.howierux.mortalexpansion.haunt.effects.HauntEffectKind biasedChoice(ServerPlayer p,java.util.List<com.howierux.mortalexpansion.haunt.effects.HauntEffectKind> candidates){
        var insight=com.howierux.mortalexpansion.haunt.effects.HauntEffectKind.SPECTRAL_INSIGHT;
        int total=candidates.stream().mapToInt(kind->kind.weight).sum();double normal=candidates.contains(insight)?(double)insight.weight/total:0;
        if(p.getRandom().nextDouble()<chance(normal,bonus(p)))return insight;
        var others=candidates.stream().filter(kind->kind!=insight).toList();if(others.isEmpty())return insight;
        int roll=p.getRandom().nextInt(others.stream().mapToInt(kind->kind.weight).sum());for(var kind:others){roll-=kind.weight;if(roll<0)return kind;}return others.getLast();
    }
    public static double chance(double normal,int bonus){return Math.clamp(normal+bonus/100.0,0,1);}
}
