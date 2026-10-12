package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FirstWhisperGameTests {
    @GameTest(template="deferred_items_test")
    public static void starter_book_whispers_research_first_without_becoming_aware(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var book=EctceteraProgression.book(0);p.getInventory().setItem(0,book);h.assertTrue(PlayerHauntManager.getTier(p)==HauntTier.NATURAL && PlayerHauntManager.getValue(p)==15,"New players start at Natural midpoint without commands");
        var recipe=InsightSecret.MORTALITY_RESEARCH_STATION.recipes().getFirst();h.assertFalse(SpectralInsight.canCraft(p,recipe),"Station remains a secret until its whisper");
        h.assertTrue(HauntEventController.tryEffect(p,HauntEffectKind.SPECTRAL_INSIGHT),"Natural Haunt can choose the pre-aware whispered effect");
        h.assertTrue(EctceteraProgression.tier(book)==0 && SpectralInsight.secrets(book)==64 && SpectralInsight.canCraft(p,recipe),"Only research is revealed without upgrading the book");
        h.assertFalse(SpectralInsight.perform(p),"Subsequent secrets still require awareness");h.assertTrue(EctceteraResearchMap.unlocked("mortality_research_station",0,64),"Starting book displays its learned research entry");PlayerHauntManager.setValue(p,0);var oldSave=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());PlayerHauntManager.setTier(p,HauntTier.DEMONIC);p.load(oldSave);h.assertTrue(PlayerHauntManager.getValue(p)==0,"Existing saved Void values are not reset to the new default");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void research_whisper_stays_known_through_book_upgrades_and_save(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var book=EctceteraProgression.book(0);p.getInventory().setItem(0,book);PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);SpectralInsight.perform(p);
        book.set(ModDataComponents.ECTCETERA_TIER.get(),2);h.assertTrue(SpectralInsight.perform(p) && SpectralInsight.secrets(book)==65,"Awakened book continues into weaving after its first research secret");
        var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());p.setData(ModAttachments.ECTCETERA_KNOWLEDGE,0);p.load(saved);h.assertTrue(p.getData(ModAttachments.ECTCETERA_KNOWLEDGE)==65,"Seven-bit knowledge preserves old and whispered secrets");p.discard();h.succeed();
    }
}
