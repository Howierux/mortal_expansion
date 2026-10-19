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
}
