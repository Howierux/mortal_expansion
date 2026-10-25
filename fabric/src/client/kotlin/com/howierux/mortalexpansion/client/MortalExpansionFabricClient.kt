package com.howierux.mortalexpansion.client

import net.fabricmc.api.ClientModInitializer

object MortalExpansionFabricClient : ClientModInitializer {
	override fun onInitializeClient() {
		CatalogClient.initialize()
	}
}
