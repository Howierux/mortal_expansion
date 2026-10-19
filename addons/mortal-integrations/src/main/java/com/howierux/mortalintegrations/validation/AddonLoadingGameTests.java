package com.howierux.mortalintegrations.validation;

import com.howierux.mortalexpansion.guide.InsightSecret;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Headless regression for optional client integration and the authoritative secret recipe catalog. */
@GameTestHolder("mortal_integrations")
@PrefixGameTestTemplate(false)
public final class AddonLoadingGameTests {
    @GameTest(template = "loading_test")
    public static void addon_and_secret_catalog_load_on_server(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded("mortal_integrations") && ModList.get().isLoaded("mortal_expansion"),
                "Both addon and base mod must initialize");
        for (var secret : InsightSecret.values()) for (var id : secret.recipes()) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(id).isPresent(), "Missing secret recipe: " + id);
            helper.assertTrue(InsightSecret.required(id) == secret.bit, "Overlay classification must match server knowledge: " + id);
        }
        helper.succeed();
    }
}
