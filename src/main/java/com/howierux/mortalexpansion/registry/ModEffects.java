package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.effect.SanctifiedEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    private ModEffects() {}
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, MortalExpansion.MOD_ID);
    public static final DeferredHolder<MobEffect, SanctifiedEffect> SANCTIFIED =
            EFFECTS.register("sanctified", SanctifiedEffect::new);
    public static void register(IEventBus bus) { EFFECTS.register(bus); }
}
