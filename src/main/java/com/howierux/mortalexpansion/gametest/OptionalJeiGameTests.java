package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.guide.InsightSecret;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Headless regression for optional client integration and the authoritative secret recipe catalog. */
@GameTestHolder("mortal_expansion")
@PrefixGameTestTemplate(false)
public final class OptionalJeiGameTests {
    @GameTest(template = "deferred_items_test")
    public static void secret_catalog_loads_without_jei(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded("mortal_expansion"),
                "Mortal Expansion must initialize with optional integrations isolated");
        for (var secret : InsightSecret.values()) for (var id : secret.recipes()) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(id).isPresent(), "Missing secret recipe: " + id);
            helper.assertTrue(InsightSecret.required(id) == secret.bit, "Overlay classification must match server knowledge: " + id);
        }
        helper.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void altar_display_catalog_matches_loaded_offerings_without_jei(GameTestHelper helper){
        for(boolean impure:new boolean[]{false,true}){
            var recipes=com.howierux.mortalexpansion.integration.jei.AltarOfferingRecipe.forAltar(impure);
            var tag=impure?com.howierux.mortalexpansion.haunt.PurityOfferings.IMPURITY:com.howierux.mortalexpansion.haunt.PurityOfferings.TAG;
            var seen=new java.util.HashSet<net.minecraft.world.item.Item>();
            for(var recipe:recipes){
                helper.assertTrue(recipe.offering().is(tag) && recipe.offering().getCount()==1 && recipe.burnTicks()>0,"Offering display entries use the correct altar tag and one consumed item");
                helper.assertTrue(seen.add(recipe.offering().getItem()),"Each offering appears only once per altar");
                helper.assertTrue(recipe.burnTicks()==com.howierux.mortalexpansion.haunt.PurityOfferings.burnTicks(recipe.offering(),impure),"Displayed duration matches the actual altar fuel rule");
            }
            helper.assertTrue(recipes.stream().anyMatch(r->r.offering().is(com.howierux.mortalexpansion.registry.ModItems.RAW_ECTOPLASM.get()) && r.burnTicks()==1200),"Both categories show Raw Ectoplasm for sixty seconds");
            helper.assertTrue(recipes.stream().anyMatch(r->r.offering().is(com.howierux.mortalexpansion.registry.ModItems.PURE_ECTOPLASM.get()) && r.burnTicks()==2400),"Purified Ectoplasm retains its longer displayed duration");
            helper.assertFalse(seen.contains(net.minecraft.world.item.Items.COAL) || seen.contains(com.howierux.mortalexpansion.registry.ModItems.RITUAL_STAR.get()),"Ordinary fuel and nonfuel rituals are not misrepresented as burnable offerings");
            net.minecraft.core.registries.BuiltInRegistries.ITEM.getTag(tag).orElseThrow().forEach(item->{
                if(com.howierux.mortalexpansion.haunt.PurityOfferings.burnTicks(item.value().getDefaultInstance(),impure)>0)
                    helper.assertTrue(seen.contains(item.value()),"Every accepted loaded-tag member has a display entry");
            });
        }
        var bucket=new com.howierux.mortalexpansion.integration.jei.AltarOfferingRecipe(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WATER_BUCKET),1200);
        helper.assertTrue(bucket.remainder().is(net.minecraft.world.item.Items.BUCKET),"A container-bearing datapack offering can display its real returned item");
        helper.succeed();
    }

}
