package com.howierux.mortalexpansion;

import com.howierux.mortalexpansion.command.ModCommands;
import com.howierux.mortalexpansion.haunt.HauntGenerationHandler;
import com.howierux.mortalexpansion.haunt.HauntSimulationHandler;
import com.howierux.mortalexpansion.loot.MobDropHandler;
import com.howierux.mortalexpansion.combat.IntangibleCombatHandler;
import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModCreativeTabs;
import com.howierux.mortalexpansion.registry.ModCapabilities;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModMenus;
import com.howierux.mortalexpansion.registry.ModStructures;
import com.howierux.mortalexpansion.worldgen.ModTerraBlender;
import com.howierux.mortalexpansion.worldgen.ModFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Main entry point for Mortal Expansion.
 *
 * Minecraft: 1.21.1
 * Loader: NeoForge 21.1.x
 */
@Mod(MortalExpansion.MOD_ID)
public final class MortalExpansion {
    public static final String MOD_ID = "mortal_expansion";

    public MortalExpansion(IEventBus modBus) {
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModCapabilities.register(modBus);
        ModAttachments.register(modBus);
        ModEntities.register(modBus);
        ModFeatures.register(modBus);
        ModCreativeTabs.register(modBus);
        ModStructures.register(modBus);

        ModTerraBlender.registerRegions();

        NeoForge.EVENT_BUS.register(ModCommands.class);
        NeoForge.EVENT_BUS.register(HauntGenerationHandler.class);
        NeoForge.EVENT_BUS.register(HauntSimulationHandler.class);
        NeoForge.EVENT_BUS.register(IntangibleCombatHandler.class);
        NeoForge.EVENT_BUS.register(MobDropHandler.class);
    }
}
