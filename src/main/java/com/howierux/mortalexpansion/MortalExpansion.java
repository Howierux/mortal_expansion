package com.howierux.mortalexpansion;

import com.howierux.mortalexpansion.command.ModCommands;
import com.howierux.mortalexpansion.haunt.HauntGenerationHandler;
import com.howierux.mortalexpansion.registry.ModAttachments;
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
        ModAttachments.register(modBus);

        NeoForge.EVENT_BUS.register(ModCommands.class);
        NeoForge.EVENT_BUS.register(HauntGenerationHandler.class);
    }
}
