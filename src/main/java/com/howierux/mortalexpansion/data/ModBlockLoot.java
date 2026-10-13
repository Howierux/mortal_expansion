package com.howierux.mortalexpansion.data;

import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public final class ModBlockLoot extends BlockLootSubProvider {
    public ModBlockLoot(HolderLookup.Provider lookup) { super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup); }
    @Override protected void generate() {
        dropOther(ModBlocks.HAUNTED_FARMLAND.get(),net.minecraft.world.item.Items.DIRT);
        // Preserve current drops, including one-item slab drops. Balance changes are separate.
        add(ModBlocks.BLOODSTONE_ORE.get(), createOreDrop(ModBlocks.BLOODSTONE_ORE.get(), ModItems.BLOODSTONE.get()));
        add(ModBlocks.BLOODSTONE_ORE_STONE.get(), createOreDrop(ModBlocks.BLOODSTONE_ORE_STONE.get(), ModItems.BLOODSTONE.get()));
        dropSelf(ModBlocks.ECTCHANTMENT_MACHINE.get());
        dropSelf(ModBlocks.ALTAR_OF_PURITY.get());
        dropSelf(ModBlocks.MORTALITY_RESEARCH_STATION.get());
        dropSelf(ModBlocks.ALTAR_OF_IMPURITY.get());
        dropSelf(ModBlocks.ECTOPLASMIC_GENERATOR.get());
        dropSelf(ModBlocks.ENCHANTMENT_GENERATOR.get());
        dropSelf(ModBlocks.GRAVE.get());
        dropSelf(ModBlocks.GRAVE_MOSS.get());
        dropSelf(ModBlocks.GRAVESTONE.get());
        dropSelf(ModBlocks.HAINT_FURNACE.get());
        dropSelf(ModBlocks.HAUNT_FURNACE.get());
        dropSelf(ModBlocks.PURIFICATION_WARD.get());
        dropSelf(ModBlocks.DREAMCATCHER.get());
        dropSelf(ModBlocks.PURIFIED_GRAVESTONE.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_BUTTON.get());
        add(ModBlocks.PURIFIED_SPIRITWOOD_DOOR.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.PURIFIED_SPIRITWOOD_DOOR.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)))
                .when(ExplosionCondition.survivesExplosion()).add(LootItem.lootTableItem(ModBlocks.PURIFIED_SPIRITWOOD_DOOR.get()))));
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_FENCE_GATE.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_LOG.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_PLANKS.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_SLAB.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_STAIRS.get());
        dropSelf(ModBlocks.PURIFIED_SPIRITWOOD_TRAPDOOR.get());
        dropSelf(ModBlocks.SPIRIT_TORCH.get());
        dropOther(ModBlocks.SPIRIT_WALL_TORCH.get(), ModItems.SPIRIT_TORCH.get());
        dropSelf(ModBlocks.SPIRITWOOD_BUTTON.get());
        add(ModBlocks.SPIRITWOOD_DOOR.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SPIRITWOOD_DOOR.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)))
                .when(ExplosionCondition.survivesExplosion()).add(LootItem.lootTableItem(ModBlocks.SPIRITWOOD_DOOR.get()))));
        dropSelf(ModBlocks.SPIRITWOOD_FENCE.get());
        dropSelf(ModBlocks.SPIRITWOOD_FENCE_GATE.get());
        add(ModBlocks.SPIRITWOOD_LEAVES.get(), createLeavesDrops(ModBlocks.SPIRITWOOD_LEAVES.get(), ModBlocks.SPIRITWOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(ModBlocks.SPIRITWOOD_LOG.get());
        dropSelf(ModBlocks.SPIRITWOOD_PLANKS.get());
        dropSelf(ModBlocks.SPIRITWOOD_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.SPIRITWOOD_SAPLING.get());
        dropSelf(ModBlocks.SPIRITWOOD_SLAB.get());
        dropSelf(ModBlocks.SPIRITWOOD_STAIRS.get());
        dropSelf(ModBlocks.SPIRITWOOD_TRAPDOOR.get());
        dropSelf(ModBlocks.STONE_FENCE.get());
    }
    @Override protected Iterable<Block> getKnownBlocks() {
        // Only blocks with existing drop definitions: fluids, the well, and currently
        // non-dropping blocks must not acquire new loot as part of a schema migration.
        return List.of(ModBlocks.DREAMCATCHER.get(),ModBlocks.MORTALITY_RESEARCH_STATION.get(),ModBlocks.ALTAR_OF_IMPURITY.get(),ModBlocks.ALTAR_OF_PURITY.get(),ModBlocks.HAUNTED_FARMLAND.get(),
                ModBlocks.BLOODSTONE_ORE.get(),
                ModBlocks.BLOODSTONE_ORE_STONE.get(),
                ModBlocks.ECTCHANTMENT_MACHINE.get(),
                ModBlocks.ECTOPLASMIC_GENERATOR.get(),
                ModBlocks.ENCHANTMENT_GENERATOR.get(),
                ModBlocks.GRAVE.get(),
                ModBlocks.GRAVE_MOSS.get(),
                ModBlocks.GRAVESTONE.get(),
                ModBlocks.HAINT_FURNACE.get(),
                ModBlocks.HAUNT_FURNACE.get(),
                ModBlocks.PURIFICATION_WARD.get(),
                ModBlocks.PURIFIED_GRAVESTONE.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_BUTTON.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_DOOR.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_FENCE_GATE.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_LOG.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_PLANKS.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_PRESSURE_PLATE.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_SLAB.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_STAIRS.get(),
                ModBlocks.PURIFIED_SPIRITWOOD_TRAPDOOR.get(),
                ModBlocks.SPIRIT_TORCH.get(),
                ModBlocks.SPIRIT_WALL_TORCH.get(),
                ModBlocks.SPIRITWOOD_BUTTON.get(),
                ModBlocks.SPIRITWOOD_DOOR.get(),
                ModBlocks.SPIRITWOOD_FENCE.get(),
                ModBlocks.SPIRITWOOD_FENCE_GATE.get(),
                ModBlocks.SPIRITWOOD_LEAVES.get(),
                ModBlocks.SPIRITWOOD_LOG.get(),
                ModBlocks.SPIRITWOOD_PLANKS.get(),
                ModBlocks.SPIRITWOOD_PRESSURE_PLATE.get(),
                ModBlocks.SPIRITWOOD_SAPLING.get(),
                ModBlocks.SPIRITWOOD_SLAB.get(),
                ModBlocks.SPIRITWOOD_STAIRS.get(),
                ModBlocks.SPIRITWOOD_TRAPDOOR.get(),
                ModBlocks.STONE_FENCE.get()
        );
    }
}
