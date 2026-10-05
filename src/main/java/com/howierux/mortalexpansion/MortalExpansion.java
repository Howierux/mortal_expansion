package com.howierux.mortalexpansion;

import com.howierux.mortalexpansion.command.ModCommands;
import com.howierux.mortalexpansion.haunt.HauntGenerationHandler;
import com.howierux.mortalexpansion.haunt.HauntSimulationHandler;
import com.howierux.mortalexpansion.haunt.HauntSpawnHandler;
import com.howierux.mortalexpansion.haunt.HaintWitchHutSpawnHandler;
import com.howierux.mortalexpansion.haunt.NaturalPurificationHandler;
import com.howierux.mortalexpansion.loot.MobDropHandler;
import com.howierux.mortalexpansion.network.GraveNamePayloads;
import com.howierux.mortalexpansion.respawn.GraveRespawnHandler;
import com.howierux.mortalexpansion.combat.IntangibleCombatHandler;
import com.howierux.mortalexpansion.entity.PharaohlPyramidHandler;
import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.registry.ModArmorMaterials;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModCreativeTabs;
import com.howierux.mortalexpansion.registry.ModDataComponents;
import com.howierux.mortalexpansion.registry.ModCapabilities;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModMenus;
import com.howierux.mortalexpansion.registry.ModFluids;
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
        com.howierux.mortalexpansion.registry.ModEffects.register(modBus);
        ModDataComponents.register(modBus);
        GraveNamePayloads.register(modBus);
        com.howierux.mortalexpansion.network.HauntEffectPayloads.register(modBus);
        com.howierux.mortalexpansion.network.SpiritJourneyPayloads.register(modBus);
        ModArmorMaterials.register(modBus);
        ModFluids.register(modBus);
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModCapabilities.register(modBus);
        ModAttachments.register(modBus);
        com.howierux.mortalexpansion.haunt.PurificationWardChunkLoader.register(modBus);
        ModEntities.register(modBus);
        ModFeatures.register(modBus);
        ModCreativeTabs.register(modBus);
        ModStructures.register(modBus);

        ModTerraBlender.registerRegions();

        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.sanctification.SanctifiedAreaHandler.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.spiritwell.SpiritwellProtection.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.haunt.effects.HauntEventController.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.haunt.effects.HauntProtection.class);
        NeoForge.EVENT_BUS.register(ModCommands.class);
        NeoForge.EVENT_BUS.register(HauntGenerationHandler.class);
        NeoForge.EVENT_BUS.register(HauntSimulationHandler.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.haunt.PlayerHauntKillHandler.class);
        NeoForge.EVENT_BUS.register(HauntSpawnHandler.class);
        NeoForge.EVENT_BUS.register(HaintWitchHutSpawnHandler.class);
        NeoForge.EVENT_BUS.register(NaturalPurificationHandler.class);
        NeoForge.EVENT_BUS.register(IntangibleCombatHandler.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.combat.GhostGarbBreathing.class);
        NeoForge.EVENT_BUS.register(PharaohlPyramidHandler.class);
        NeoForge.EVENT_BUS.register(MobDropHandler.class);
        NeoForge.EVENT_BUS.register(GraveRespawnHandler.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.respawn.SpiritJourneyHandler.class);
        NeoForge.EVENT_BUS.register(com.howierux.mortalexpansion.respawn.DeferredDeathItems.class);
    }
}
