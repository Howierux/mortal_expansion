package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Native potion contents retain Mojang's bottles, models, tint, tooltips, throwing, saves and packets. */
public final class ModPotions {
    private ModPotions(){}
    public static final DeferredRegister<Potion> POTIONS=DeferredRegister.create(Registries.POTION,MortalExpansion.MOD_ID);
    public static final DeferredHolder<Potion,Potion> HOLY_WATER=POTIONS.register("holy_water",()->new Potion(
            "mortal_expansion.holy_water",new MobEffectInstance(ModEffects.CONSECRATION,1)));
    public static void register(IEventBus bus){POTIONS.register(bus);}
}
