package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

/**
 * TerraBlender hookup for Mortal Expansion world generation.
 */
public final class ModTerraBlender {
    private ModTerraBlender() {}

    /**
     * Graveyard and Spiritwood each use their own low-weight region so their
     * appearance odds are independent from one another.
     */
    public static void registerRegions() {
        Regions.register(new MortalExpansionOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(
                        MortalExpansion.MOD_ID,
                        "graveyard_overworld"
                ),
                1
        ));

        Regions.register(new SpiritwoodOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(
                        MortalExpansion.MOD_ID,
                        "spiritwood_overworld"
                ),
                1
        ));
    }
}
