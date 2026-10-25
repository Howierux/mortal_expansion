package com.howierux.mortalexpansion.client

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import com.howierux.mortalexpansion.data.HauntBiomeProvider

object MortalExpansionFabricDataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        fabricDataGenerator.createPack().addProvider(::HauntBiomeProvider)
	}
}
