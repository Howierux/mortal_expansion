package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

/**
 * Tree growers used by Mortal Expansion saplings.
 */
public final class ModTreeGrowers {
    private ModTreeGrowers() {}

    public static final ResourceKey<ConfiguredFeature<?, ?>> SPIRITWOOD_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_tree")
    );

    public static final TreeGrower SPIRITWOOD = new TreeGrower(
            "spiritwood",
            Optional.empty(),
            Optional.of(SPIRITWOOD_TREE),
            Optional.empty()
    );
}
