package com.howierux.mortalexpansion.data;

import com.howierux.mortalexpansion.MortalExpansion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** runData is the only author of generated resources; runtime registries stay independent. */
@EventBusSubscriber(modid = MortalExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataGenerators {
    private ModDataGenerators() {}

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var lookup = event.getLookupProvider();
        var helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ModBlockModels(output, helper));
        generator.addProvider(event.includeClient(), new ModItemModels(output, helper));
        generator.addProvider(event.includeClient(), new ModPostEffects(output));
        generator.addProvider(event.includeServer(), new ModRecipes(output, lookup));
        generator.addProvider(event.includeServer(), new ModLootTables(output, lookup));
        generator.addProvider(event.includeServer(), new ModTags.BlockTags(output, lookup, helper));
        generator.addProvider(event.includeServer(), new ModTags.ItemTags(output, lookup, helper));
        generator.addProvider(event.includeServer(), new ModTags.EntityTypeTags(output, lookup, helper));
        generator.addProvider(event.includeServer(), new ModTags.DamageTypeTags(output, lookup, helper));
        generator.addProvider(event.includeServer(), new ModTags.BiomeTags(output, lookup, helper));
    }
}
