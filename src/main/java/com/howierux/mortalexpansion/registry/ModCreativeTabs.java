package com.howierux.mortalexpansion.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModCreativeTabs::buildContents);
    }

    private static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModItems.SPIRITWOOD_LOG.get());
            event.accept(ModItems.SPIRITWOOD_LEAVES.get());
            event.accept(ModItems.SPIRITWOOD_SAPLING.get());
            event.accept(ModItems.BLOODSTONE_ORE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BLOODSTONE.get());
            event.accept(ModItems.RAW_ECTOPLASM.get());
            event.accept(ModItems.PURE_ECTOPLASM.get());
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.BLOODSTONE_PICKAXE.get());
            event.accept(ModItems.BLOODSTONE_SHOVEL.get());
            event.accept(ModItems.BLOODSTONE_HOE.get());
            event.accept(ModItems.SPECTRALITE_PICKAXE.get());
            event.accept(ModItems.SPECTRALITE_SHOVEL.get());
            event.accept(ModItems.SPECTRALITE_HOE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.PHARAOHL_SCEPTER.get());
            event.accept(ModItems.GRAVESTONE_SWORD.get());
            event.accept(ModItems.PURIFIED_GRAVESTONE_SWORD.get());
            event.accept(ModItems.BLOODSTONE_SWORD.get());
            event.accept(ModItems.BLOODSTONE_AXE.get());
            event.accept(ModItems.SPECTRALITE_SWORD.get());
            event.accept(ModItems.SPECTRALITE_AXE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.ECTCHANTMENT_MACHINE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.SPIRITWOOD_PLANKS.get());
            event.accept(ModItems.PURIFIED_GRAVESTONE.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_LOG.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_STAIRS.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_SLAB.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_FENCE.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_FENCE_GATE.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_DOOR.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_TRAPDOOR.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_PRESSURE_PLATE.get());
            event.accept(ModItems.PURIFIED_SPIRITWOOD_BUTTON.get());
            event.accept(ModItems.SPIRITWOOD_STAIRS.get());
            event.accept(ModItems.SPIRITWOOD_SLAB.get());
            event.accept(ModItems.SPIRITWOOD_FENCE.get());
            event.accept(ModItems.SPIRITWOOD_FENCE_GATE.get());
            event.accept(ModItems.SPIRITWOOD_DOOR.get());
            event.accept(ModItems.SPIRITWOOD_TRAPDOOR.get());
            event.accept(ModItems.SPIRITWOOD_PRESSURE_PLATE.get());
            event.accept(ModItems.SPIRITWOOD_BUTTON.get());
            event.accept(ModItems.GRAVESTONE.get());
            event.accept(ModItems.GRAVE.get());
            event.accept(ModItems.STONE_FENCE.get());
        }
    }
}
