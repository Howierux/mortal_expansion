package com.howierux.mortalexpansion.client

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import com.howierux.mortalexpansion.data.HauntBiomeProvider
import com.howierux.mortalexpansion.data.CatalogBlockTags
import com.howierux.mortalexpansion.data.CatalogLoot
import com.howierux.mortalexpansion.data.ProcessingRecipes

object MortalExpansionFabricDataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::HauntBiomeProvider)
        pack.addProvider(::CatalogBlockTags)
        pack.addProvider(CatalogBlockTags::Items)
        pack.addProvider(::CatalogLoot)
        pack.addProvider(::ProcessingRecipes)
	}
}
