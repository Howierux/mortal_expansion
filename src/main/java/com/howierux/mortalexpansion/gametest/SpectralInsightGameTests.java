package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.haunt.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpectralInsightGameTests {
    @GameTest(template="deferred_items_test")
    public static void aware_books_reveal_secrets_gradually_with_both_tier_rules(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var book=EctceteraProgression.book(1);p.getInventory().setItem(0,book);
        PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);h.assertTrue(SpectralInsight.perform(p) && (SpectralInsight.secrets(book)&64)!=0,"A Bone book can receive the first research whisper before awareness");
        book=EctceteraProgression.book(2);p.getInventory().setItem(0,book);PlayerHauntManager.setTier(p,HauntTier.NATURAL);
        h.assertFalse(SpectralInsight.perform(p),"Awareness alone does not bypass personal Haunt thresholds");
        PlayerHauntManager.setTier(p,HauntTier.SUPERNATURAL);h.assertTrue(SpectralInsight.perform(p),"First Insight teaches Ectothread");
        h.assertTrue(SpectralInsight.secrets(book)==65 && p.getData(ModAttachments.ECTCETERA_KNOWLEDGE)==65,"One event reveals only one family");
        h.assertFalse(SpectralInsight.perform(p),"Next family waits for Dead Rising");
        PlayerHauntManager.setTier(p,HauntTier.DEAD_RISING);h.assertTrue(SpectralInsight.perform(p),"Ghost Garb follows thread knowledge");
        PlayerHauntManager.setTier(p,HauntTier.DEMONIC);h.assertTrue(SpectralInsight.perform(p),"Raw upgraded book can reveal Haint weaving");
        h.assertTrue(SpectralInsight.secrets(book)==71,"Thread and both garb types remembered");
        PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);h.assertTrue(SpectralInsight.perform(p) && SpectralInsight.secrets(book)==87,"Raw book reveals Impurity but still excludes Spectralite Haint and Purity");
        var upgraded=book.copy();upgraded.set(ModDataComponents.ECTCETERA_TIER.get(),3);p.getInventory().setItem(0,upgraded);
        h.assertTrue(SpectralInsight.perform(p) && SpectralInsight.secrets(upgraded)==95,"Purified awareness enables Spectralite Haint secret");
        h.assertTrue(SpectralInsight.perform(p) && SpectralInsight.secrets(upgraded)==127,"A separate Insight reveals Purity after Impurity");
        h.assertFalse(SpectralInsight.perform(p),"No repeated fake revelation after all secrets are known");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void manual_crafting_stays_locked_until_insight_and_consumes_normally(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(pos,Blocks.CRAFTING_TABLE.defaultBlockState(),3);
        var menu=new CraftingMenu(1,p.getInventory(),ContainerLevelAccess.create(h.getLevel(),pos));p.containerMenu=menu;
        for(int slot=1;slot<=9;slot++)menu.getSlot(slot).set(new ItemStack(slot==5?ModItems.RAW_ECTOPLASM.get():Items.STRING));
        menu.slotsChanged(menu.getSlot(1).container);h.assertTrue(menu.getSlot(0).getItem().isEmpty(),"Knowing the pattern cannot bypass knowledge lock");
        h.assertTrue(menu.getSlot(5).getItem().getCount()==1,"Locked crafting preserves input");
        SpectralInsight.learnPlayer(p,1);h.assertTrue(menu.getSlot(0).getItem().is(ModItems.ECTOTHREAD.get()),"Learning refreshes an already-open crafting table");
        var output=menu.getSlot(0).remove(1);menu.getSlot(0).onTake(p,output);
        h.assertTrue(output.getCount()==8 && menu.getSlot(5).getItem().isEmpty(),"Unlocked recipe gives eight and consumes materials normally");
        for(int slot=1;slot<=9;slot++)menu.getSlot(slot).set(ItemStack.EMPTY);
        for(int slot:new int[]{1,3,4,6})menu.getSlot(slot).set(new ItemStack(ModItems.ECTOTHREAD.get()));
        menu.slotsChanged(menu.getSlot(1).container);h.assertTrue(menu.getSlot(0).getItem().isEmpty(),"Ghost Garb remains locked after learning only thread");
        SpectralInsight.learnPlayer(p,2);h.assertTrue(menu.getSlot(0).getItem().is(ModItems.GHOST_GARB_BOOTS.get()),"Ghost knowledge unlocks the normal armor pattern");
        p.containerMenu=p.inventoryMenu;for(int slot=1;slot<=4;slot++)p.inventoryMenu.getSlot(slot).set(new ItemStack(Items.OAK_PLANKS));
        p.inventoryMenu.slotsChanged(p.inventoryMenu.getSlot(1).container);
        h.assertTrue(p.inventoryMenu.getSlot(0).getItem().is(Items.CRAFTING_TABLE),"Unrelated inventory-grid recipes remain available");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void smithing_requires_each_secret_and_preserves_base_components(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var menu=new SmithingMenu(1,p.getInventory());p.containerMenu=menu;
        var base=new ItemStack(ModItems.GHOST_GARB_HELMET.get());base.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Remember me"));
        menu.getSlot(1).set(base);menu.getSlot(2).set(new ItemStack(ModItems.PURE_ECTOPLASM.get()));menu.createResult();
        h.assertTrue(menu.getSlot(3).getItem().isEmpty() && menu.getSlot(1).hasItem(),"Smithing cannot bypass Haint knowledge");
        SpectralInsight.learnPlayer(p,7);h.assertTrue(menu.getSlot(3).getItem().is(ModItems.HAINT_GARB_HELMET.get()) && menu.getSlot(3).getItem().getHoverName().getString().equals("Remember me"),"Learning refreshes smithing and keeps base name");
        menu.getSlot(1).set(new ItemStack(ModItems.HAINT_GARB_HELMET.get()));menu.getSlot(2).set(new ItemStack(ModItems.SPECTRALITE_HELMET.get()));menu.createResult();
        h.assertTrue(menu.getSlot(3).getItem().isEmpty(),"Combined armor has its own knowledge lock");
        SpectralInsight.learnPlayer(p,8);h.assertTrue(menu.getSlot(3).getItem().is(ModItems.SPECTRALITE_HAINT_HELMET.get()),"Combined secret unlocks smithing");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void knowledge_survives_saved_and_network_roundtrips_and_book_upgrades(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var book=EctceteraProgression.book(2);book.set(ModDataComponents.ECTCETERA_SECRETS.get(),7);
        SpectralInsight.absorb(p,book);var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());p.setData(ModAttachments.ECTCETERA_KNOWLEDGE,0);p.load(saved);
        h.assertTrue(p.getData(ModAttachments.ECTCETERA_KNOWLEDGE)==7,"Learned knowledge survives save/load");
        var input=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(book,new ItemStack(ModItems.PURE_ECTOPLASM.get())));
        var recipe=h.getLevel().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,input,h.getLevel()).orElseThrow();
        var upgraded=recipe.value().assemble(input,h.getLevel().registryAccess());h.assertTrue(EctceteraProgression.tier(upgraded)==3 && SpectralInsight.secrets(upgraded)==7,"Book upgrading preserves secrets");
        var ops=net.minecraft.resources.RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE,h.getLevel().registryAccess());
        var restored=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,upgraded).getOrThrow()).getOrThrow();h.assertTrue(SpectralInsight.secrets(restored)==7,"Book secrets persist");
        var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());try{
            ItemStack.STREAM_CODEC.encode(buffer,upgraded);h.assertTrue(SpectralInsight.secrets(ItemStack.STREAM_CODEC.decode(buffer))==7,"Client receives secret bits");
        }finally{buffer.release();}p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void legacy_books_and_existing_recipe_unlocks_retain_knowledge(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();SpectralInsight.absorb(p,new ItemStack(ModItems.BOOK_OF_ECTCETERA.get()));
        h.assertTrue(p.getData(ModAttachments.ECTCETERA_KNOWLEDGE)==15,"Unmarked legacy full guides retain all knowledge");
        var other=h.makeMockServerPlayerInLevel();var id=InsightSecret.GHOST_GARB.recipes().getFirst();other.awardRecipes(java.util.List.of(h.getLevel().getRecipeManager().byKey(id).orElseThrow()));
        SpectralInsight.login(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(other));
        h.assertTrue((other.getData(ModAttachments.ECTCETERA_KNOWLEDGE)&3)==3,"Previously unlocked recipes migrate with prerequisites");
        var shared=EctceteraProgression.book(2);shared.set(ModDataComponents.ECTCETERA_SECRETS.get(),7);SpectralInsight.absorb(other,shared);
        h.assertTrue((other.getData(ModAttachments.ECTCETERA_KNOWLEDGE)&7)==7,"A learned book can teach another player");
        var mixed=h.makeMockServerPlayerInLevel();mixed.getInventory().setItem(0,EctceteraProgression.book(2));mixed.getInventory().setItem(1,new ItemStack(ModItems.BOOK_OF_ECTCETERA.get()));
        PlayerHauntManager.setTier(mixed,HauntTier.ELDRITCH);
        h.assertTrue(SpectralInsight.perform(mixed) && (mixed.getData(ModAttachments.ECTCETERA_KNOWLEDGE)&15)==15 && (mixed.getData(ModAttachments.ECTCETERA_KNOWLEDGE)&64)!=0,"Legacy weaving knowledge is retained while the research whisper is revealed first");
        p.discard();other.discard();mixed.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void guide_hides_recipes_until_the_book_knows_them(GameTestHelper h){
        h.assertFalse(EctceteraGuide.chapters(2,0).stream().anyMatch(c->c.key().equals("ectothread") || c.key().equals("ghost_garb") || c.key().equals("haint_garb")),"Aware upgrade does not reveal recipes immediately");
        h.assertTrue(EctceteraGuide.chapters(2,7).stream().anyMatch(c->c.key().equals("haint_garb")),"Raw aware book reveals learned Haint chapter");
        var chapters=EctceteraGuide.chapters(3,15);int page=1+EctceteraGuide.contentsPages(chapters);
        for(int i=0;i<chapters.size();i++){h.assertTrue(EctceteraGuide.firstPage(chapters,i)==page,"Filtered contents offsets remain correct");page+=chapters.get(i).pages();}
        h.assertTrue(page<=128,"Complete knowledge stays within vanilla page budget");h.succeed();
    }
    @GameTest(template="deferred_items_test",timeoutTicks=60)
    public static void vanilla_crafter_requires_taught_knowledge_and_preserves_locked_inputs(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(5,2,5));h.getLevel().setBlock(pos,Blocks.CRAFTER.defaultBlockState(),3);
        var crafter=(net.minecraft.world.level.block.entity.CrafterBlockEntity)h.getLevel().getBlockEntity(pos);
        for(int i=0;i<9;i++)crafter.setItem(i,new ItemStack(i==4?ModItems.RAW_ECTOPLASM.get():Items.STRING));
        h.getLevel().scheduleTick(pos,Blocks.CRAFTER,1);
        h.runAfterDelay(2,()->{
            h.assertTrue(crafter.getItem(4).getCount()==1,"Untaught automatic crafting cannot bypass the lock");
            SpectralInsight.learnPlayer(p,1);
            SpectralInsight.teachCrafter(new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock(p,net.minecraft.world.InteractionHand.MAIN_HAND,pos,
                    new net.minecraft.world.phys.BlockHitResult(pos.getCenter(),net.minecraft.core.Direction.UP,pos,false)));
            h.getLevel().scheduleTick(pos,Blocks.CRAFTER,1);
            h.runAfterDelay(2,()->{h.assertTrue(crafter.getItem(4).isEmpty(),"Opening with knowledge teaches the crafter and enables normal consumption");p.discard();h.succeed();});
        });
    }
}
