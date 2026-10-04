package com.howierux.mortalexpansion.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

/** Uses Mojang's loot codec and validator while preserving existing random-sequence policy. */
public final class ModLootTables implements DataProvider {
    private final PackOutput.PathProvider paths;
    private final CompletableFuture<HolderLookup.Provider> lookup;

    public ModLootTables(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        paths = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.lookup = lookup;
    }
    @Override public String getName() { return "Mortal Expansion block loot"; }
    @Override public CompletableFuture<?> run(CachedOutput output) {
        return lookup.thenCompose(registries -> {
            Map<ResourceKey<LootTable>, LootTable> tables = new LinkedHashMap<>();
            new ModBlockLoot(registries).generate((id, builder) -> {
                // Only the original ore tables use named random sequences.
                if (id.location().getPath().startsWith("blocks/bloodstone_ore")) {
                    builder.setRandomSequence(id.location());
                }
                if (tables.put(id, builder.setParamSet(LootContextParamSets.BLOCK).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + id.location());
                }
            });
            var problems = new ProblemReporter.Collector();
            var validation = new ValidationContext(problems, LootContextParamSets.BLOCK, registries.asGetterLookup());
            tables.forEach((id, table) -> table.validate(validation.enterElement("{" + id.location() + "}", id)));
            if (!problems.get().isEmpty()) throw new IllegalStateException("Invalid block loot: " + problems.get());
            return CompletableFuture.allOf(tables.entrySet().stream().map(entry ->
                    DataProvider.saveStable(output, registries, LootTable.DIRECT_CODEC,
                            entry.getValue(), paths.json(entry.getKey().location()))).toArray(CompletableFuture[]::new));
        });
    }
}
