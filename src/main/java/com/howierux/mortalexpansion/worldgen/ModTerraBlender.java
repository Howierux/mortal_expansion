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
     * Weight 1 keeps Spiritwood regions deliberately uncommon relative to
     * TerraBlender's vanilla Overworld region weight.
     */
    public static void registerRegions() {
        Regions.register(new MortalExpansionOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "overworld"),
                1
        ));
    }
}
