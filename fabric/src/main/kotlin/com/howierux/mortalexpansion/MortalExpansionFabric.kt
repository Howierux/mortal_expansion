package com.howierux.mortalexpansion

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.LoggerFactory

object MortalExpansionFabric : ModInitializer {
	const val MOD_ID: String = "mortal_expansion"

	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Mortal Expansion Fabric scaffold initialized; gameplay port pending.")
	}

	fun id(path: String): ResourceLocation
		= ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
