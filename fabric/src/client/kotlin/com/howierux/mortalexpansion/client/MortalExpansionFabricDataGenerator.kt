package com.howierux.mortalexpansion.client

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import com.howierux.mortalexpansion.data.*
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import com.howierux.mortalexpansion.data.HauntBiomeProvider
import com.howierux.mortalexpansion.data.CatalogBlockTags
import com.howierux.mortalexpansion.data.CatalogLoot
import com.howierux.mortalexpansion.data.KnowledgeRecipes

object MortalExpansionFabricDataGenerator : DataGeneratorEntrypoint {
    override fun buildRegistry(builder: RegistrySetBuilder) {
        builder.add(Registries.DAMAGE_TYPE, CombatData::damageTypes)
        builder.add(Registries.ENCHANTMENT, CombatData::enchantments)
        builder.add(Registries.CONFIGURED_FEATURE, TerrainBootstrap::configured)
        builder.add(Registries.PLACED_FEATURE, TerrainBootstrap::placed)
        builder.add(Registries.BIOME, TerrainBootstrap::biomes)
        builder.add(Registries.STRUCTURE, TerrainBootstrap::structures)
        builder.add(Registries.STRUCTURE_SET, TerrainBootstrap::sets)
    }
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::ModPostEffects)
        pack.addProvider(CombatData::RegistryData)
        pack.addProvider(CombatData::Entities)
        pack.addProvider(CombatData::DamageTags)
        pack.addProvider(CombatData::Assets)
        pack.addProvider(::TerrainProvider)
        pack.addProvider(::HauntBiomeProvider)
        pack.addProvider(::CatalogBlockTags)
        pack.addProvider(CatalogBlockTags::Items)
        pack.addProvider(::CatalogLoot)
        pack.addProvider(::KnowledgeRecipes)
	}
}
