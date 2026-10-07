package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MortalityResearchGameTests {
    @GameTest(template="deferred_items_test")
    public static void materials_stack_but_raw_and_purified_types_cannot_mix(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var raw=new ItemStack(ModItems.RAW_ECTOPLASM.get(),2);
        h.assertTrue(MortalityResearch.contribute(p,new ItemStack(Items.ROTTEN_FLESH)) && MortalityResearch.bonus(p)==1,"Flesh adds one point");
        MortalityResearch.contribute(p,new ItemStack(Items.BONE));MortalityResearch.contribute(p,raw);MortalityResearch.contribute(p,raw);
        h.assertTrue(MortalityResearch.bonus(p)==26 && raw.isEmpty(),"Bones and repeated chosen-type ectoplasm stack their exact bonuses");
        var pure=new ItemStack(ModItems.PURE_ECTOPLASM.get());h.assertFalse(MortalityResearch.contribute(p,pure),"Cannot mix purified ectoplasm into a raw bonus");h.assertTrue(pure.getCount()==1,"Rejected research preserves material");
        MortalityResearch.spent(p);MortalityResearch.contribute(p,pure);h.assertTrue(MortalityResearch.bonus(p)==20,"A new bonus may choose purified instead");
        p.setData(ModAttachments.MORTALITY_RESEARCH_BONUS,100);var bone=new ItemStack(Items.BONE);h.assertFalse(MortalityResearch.contribute(p,bone) || bone.isEmpty(),"Full bonus cannot waste more materials");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void station_recipe_retains_the_exact_book_and_consumes_salt_obsidian(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(pos,Blocks.CRAFTING_TABLE.defaultBlockState(),3);var menu=new CraftingMenu(1,p.getInventory(),ContainerLevelAccess.create(h.getLevel(),pos));p.containerMenu=menu;
        var book=EctceteraProgression.book(2);book.set(ModDataComponents.ECTCETERA_SECRETS.get(),7);book.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("My memories"));
        menu.getSlot(2).set(book.copy());for(int slot:new int[]{4,6})menu.getSlot(slot).set(new ItemStack(ModItems.HOLY_SALT.get()));for(int slot:new int[]{5,7,8,9})menu.getSlot(slot).set(new ItemStack(Items.OBSIDIAN));
        h.assertTrue(menu.getSlot(0).getItem().is(ModItems.MORTALITY_RESEARCH_STATION.get()),"Enchanting-table-style pattern matches");menu.quickMoveStack(p,0);
        h.assertTrue(ItemStack.isSameItemSameComponents(menu.getSlot(2).getItem(),book) && menu.getSlot(2).getItem().getCount()==1,"Returned book keeps name, upgrade and secrets");
        for(int slot:new int[]{4,5,6,7,8,9})h.assertTrue(menu.getSlot(slot).getItem().isEmpty(),"Only the two salts and four obsidian are consumed");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void a_full_bonus_redirects_the_next_eligible_risk_to_real_insight_even_in_garb(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();var book=EctceteraProgression.book(2);p.getInventory().setItem(0,book);PlayerHauntManager.setTier(p,HauntTier.SUPERNATURAL);
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new ItemStack(ModItems.GHOST_GARB_HELMET.get()));p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,new ItemStack(ModItems.GHOST_GARB_CHESTPLATE.get()));p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,new ItemStack(ModItems.GHOST_GARB_LEGGINGS.get()));p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,new ItemStack(ModItems.GHOST_GARB_BOOTS.get()));
        p.setData(ModAttachments.MORTALITY_RESEARCH_BONUS,100);p.setData(ModAttachments.MORTALITY_RESEARCH_ECTO,2);p.getRandom().setSeed(42);
        for(int i=0;i<1000 && MortalityResearch.bonus(p)>0;i++)HauntEventController.rollPool(p,true);
        h.assertTrue(SpectralInsight.secrets(book)==1 && MortalityResearch.bonus(p)==0 && p.getData(ModAttachments.MORTALITY_RESEARCH_ECTO)==0,"Eligible next selection reveals exactly one secret and resets the combination");
        h.assertTrue(HauntEventController.ready(p,true) && !HauntEventController.ready(p,false),"Redirected Insight spends only benefit cooldown");h.assertTrue(Math.abs(MortalityResearch.chance(.2,20)-.4)<.00001,"Twenty research points add twenty percentage points");
        PlayerHauntManager.setTier(p,HauntTier.DEAD_RISING);p.setData(ModAttachments.MORTALITY_RESEARCH_BONUS,100);
        h.assertTrue(com.howierux.mortalexpansion.command.HauntEffectCommands.trigger(p.createCommandSourceStack().withPermission(2),p,"random")==1 && SpectralInsight.secrets(book)==3 && MortalityResearch.bonus(p)==0,"Random command honors research while retaining actual secret requirements");
        p.setData(ModAttachments.MORTALITY_RESEARCH_BONUS,10);com.howierux.mortalexpansion.command.HauntEffectCommands.trigger(p.createCommandSourceStack().withPermission(2),p,"good_potion");h.assertTrue(MortalityResearch.bonus(p)==10,"Explicitly named commands do not spend research");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void unavailable_secrets_preserve_pending_research_and_complete_knowledge_rejects_materials(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();p.getInventory().setItem(0,EctceteraProgression.book(1));PlayerHauntManager.setTier(p,HauntTier.SUPERNATURAL);MortalityResearch.contribute(p,new ItemStack(Items.BONE));
        h.assertFalse(MortalityResearch.ready(p),"Bone book cannot reveal a secret yet");
        for(int i=0;i<20;i++)HauntEventController.rollPool(p,false);h.assertTrue(MortalityResearch.bonus(p)==5,"Ordinary events cannot waste research while revelation is impossible");
        p.setData(ModAttachments.ECTCETERA_KNOWLEDGE,SpectralInsight.ALL);var flesh=new ItemStack(Items.ROTTEN_FLESH);h.assertFalse(MortalityResearch.contribute(p,flesh) || flesh.isEmpty(),"No research is consumed after every secret is known");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void research_menu_is_player_bound_and_bonus_combination_persists(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(7,2,7));h.getLevel().setBlock(pos,ModBlocks.MORTALITY_RESEARCH_STATION.get().defaultBlockState(),3);var station=(com.howierux.mortalexpansion.blockentity.MortalityResearchStationBlockEntity)h.getLevel().getBlockEntity(pos);
        var p=h.makeMockServerPlayerInLevel();p.setPos(pos.getCenter());station.getItems().setStackInSlot(0,new ItemStack(ModItems.RAW_ECTOPLASM.get(),2));var menu=new com.howierux.mortalexpansion.menu.MortalityResearchStationMenu(1,p.getInventory(),station);p.containerMenu=menu;
        h.assertTrue(menu.clickMenuButton(p,0) && MortalityResearch.bonus(p)==10 && station.getItems().getStackInSlot(0).getCount()==1,"Research button consumes exactly one material for its actor");
        var other=h.makeMockServerPlayerInLevel();h.assertTrue(MortalityResearch.bonus(other)==0,"Station does not share research percentages between players");
        var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());MortalityResearch.spent(p);p.load(saved);h.assertTrue(MortalityResearch.bonus(p)==10 && p.getData(ModAttachments.MORTALITY_RESEARCH_ECTO)==1,"Pending bonus and chosen type survive saving");
        p.setPos(pos.getCenter().add(20,0,0));h.assertFalse(menu.clickMenuButton(p,0),"Remote button packets cannot consume research");p.discard();other.discard();h.succeed();
    }
}
