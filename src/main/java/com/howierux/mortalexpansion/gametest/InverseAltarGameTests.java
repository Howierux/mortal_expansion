package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InverseAltarGameTests {
    @GameTest(template="deferred_items_test")
    public static void impurity_burns_offerings_and_raises_only_its_nine_loaded_chunks(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(4096,2,4096));h.getLevel().setBlock(pos,ModBlocks.ALTAR_OF_IMPURITY.get().defaultBlockState(),3);var a=(AltarOfPurityBlockEntity)h.getLevel().getBlockEntity(pos);
        h.assertTrue(a.isImpure() && a.getType()==ModBlockEntities.ALTAR_OF_IMPURITY.get(),"New altar has its own registered entity type");var center=new net.minecraft.world.level.ChunkPos(pos);
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){var cp=new net.minecraft.world.level.ChunkPos(center.x+dx,center.z+dz);h.getLevel().getChunk(cp.x,cp.z);HauntManager.setValue(h.getLevel(),cp,0);}
        a.getItems().setStackInSlot(0,new ItemStack(ModItems.SPIRITWOOD_LOG.get()));for(int i=0;i<200;i++)AltarOfPurityBlockEntity.serverTick(h.getLevel(),pos,a.getBlockState(),a);
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)h.assertTrue(HauntManager.getHundredths(h.getLevel(),new net.minecraft.world.level.ChunkPos(center.x+dx,center.z+dz))==10,"Inverse pulse adds 0.10 Haunt");
        HauntManager.setValue(h.getLevel(),pos,69);AltarOfPurityBlockEntity.corrupt(h.getLevel(),pos);h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==6900,"Haunt cannot exceed its normal maximum");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void beacon_furnace_recipes_require_separate_actual_insight_revelations(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var book=EctceteraProgression.book(3);book.set(ModDataComponents.ECTCETERA_SECRETS.get(),15);p.getInventory().setItem(0,book);PlayerHauntManager.setTier(p,HauntTier.DEMONIC);
        var table=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(table,net.minecraft.world.level.block.Blocks.CRAFTING_TABLE.defaultBlockState(),3);var menu=new CraftingMenu(1,p.getInventory(),ContainerLevelAccess.create(h.getLevel(),table));p.containerMenu=menu;
        menu.getSlot(1).set(new ItemStack(Items.BEACON));menu.getSlot(4).set(new ItemStack(ModItems.HAUNT_FURNACE.get()));h.assertTrue(menu.getSlot(0).getItem().isEmpty(),"Knowing the Impurity layout does not bypass its secret");
        h.assertTrue(SpectralInsight.perform(p) && menu.getSlot(0).getItem().is(ModItems.ALTAR_OF_IMPURITY.get()),"Impurity event refreshes actual crafting output");menu.quickMoveStack(p,0);
        h.assertTrue(menu.getSlot(1).getItem().isEmpty() && menu.getSlot(4).getItem().isEmpty(),"Native craft consumes one Beacon and one Haunt Furnace");
        menu.getSlot(1).set(new ItemStack(Items.BEACON));menu.getSlot(4).set(new ItemStack(ModItems.HAINT_FURNACE.get()));h.assertTrue(menu.getSlot(0).getItem().isEmpty(),"Impurity knowledge does not grant Purity");
        h.assertTrue(SpectralInsight.perform(p) && menu.getSlot(0).getItem().is(ModItems.ALTAR_OF_PURITY.get()),"Separate Purity event unlocks Beacon over Haint Furnace");
        h.assertTrue(EctceteraResearchMap.unlocked("altar_of_impurity",3,63) && EctceteraResearchMap.unlocked("altar_of_purity",3,63),"Both recipes have revealed Secrets of the Dead entries");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void old_recipe_unlocks_and_legacy_books_do_not_grant_new_altar_secrets(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var id=InsightSecret.ALTAR_OF_PURITY.recipes().getFirst();p.awardRecipes(java.util.List.of(h.getLevel().getRecipeManager().byKey(id).orElseThrow()));
        h.assertFalse(SpectralInsight.canCraft(p,id),"Old automatic recipe unlock is insufficient for the new secret");SpectralInsight.login(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(p));
        h.assertFalse(p.getRecipeBook().contains(id),"Old automatic altar recipe is removed until properly learned");var legacy=new ItemStack(ModItems.BOOK_OF_ECTCETERA.get());SpectralInsight.absorb(p,legacy);
        h.assertTrue(EctceteraProgression.tier(legacy)==3 && SpectralInsight.secrets(legacy)==15,"Legacy book keeps its old weaving knowledge but receives no altar bits");
        SpectralInsight.learnPlayer(p,48);SpectralInsight.absorb(p,legacy);h.assertTrue(SpectralInsight.secrets(legacy)==63,"New six-bit knowledge can persist on migrated books");
        var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());p.setData(ModAttachments.ECTCETERA_KNOWLEDGE,0);p.load(saved);h.assertTrue(p.getData(ModAttachments.ECTCETERA_KNOWLEDGE)==63,"Expanded knowledge saves without losing older bits");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void positive_purity_benefits_do_not_mutate_impurity_altars(GameTestHelper h){
        h.setBlock(new BlockPos(7,2,7),ModBlocks.ALTAR_OF_IMPURITY.get());var a=(AltarOfPurityBlockEntity)h.getLevel().getBlockEntity(h.absolutePos(new BlockPos(7,2,7)));var p=h.makeMockServerPlayerInLevel();p.setPos(a.getBlockPos().getCenter());a.getItems().setStackInSlot(0,new ItemStack(ModItems.HOLY_SALT.get()));
        h.assertFalse(a.blessNextOffering() || a.addOfferingGift(),"Purity-specific bonuses cannot be applied to Impurity directly");
        h.assertFalse(HauntEffects.perform(p,HauntEffectKind.OFFERING_GIFT) || HauntEffects.perform(p,HauntEffectKind.OFFERING_BLESSING),"Haunt benefits ignore inverse altars");p.discard();h.succeed();
    }
}
