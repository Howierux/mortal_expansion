package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    private ModFeatures() {}

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MortalExpansion.MOD_ID);

    public static final DeferredHolder<Feature<?>, GraveyardBorderFeature> GRAVEYARD_BORDER =
            FEATURES.register("graveyard_border", GraveyardBorderFeature::new);

    public static final DeferredHolder<Feature<?>, MausoleumFeature> MAUSOLEUM =
            FEATURES.register("mausoleum", MausoleumFeature::new);

    public static final DeferredHolder<Feature<?>, GraveRowsFeature> GRAVE_ROWS =
            FEATURES.register("grave_rows", GraveRowsFeature::new);

    public static final DeferredHolder<Feature<?>, SpiritwellFeature> SPIRITWELL =
            FEATURES.register("spiritwell", SpiritwellFeature::new);

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
