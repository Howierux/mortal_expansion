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
        }
    }
}
