package com.howierux.mortalexpansion

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.LoggerFactory

object MortalExpansionFabric : ModInitializer {
	const val MOD_ID: String = "mortal_expansion"

	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		com.howierux.mortalexpansion.haunt.HauntRuntime.initialize()
		LOGGER.info("Mortal Expansion Fabric: Haunt foundation initialized.")
	}

	fun id(path: String): ResourceLocation
		= ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
