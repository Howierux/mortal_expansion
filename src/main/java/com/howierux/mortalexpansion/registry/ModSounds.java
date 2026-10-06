package com.howierux.mortalexpansion.registry;
import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.*;
import net.neoforged.bus.api.IEventBus;
public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS=DeferredRegister.create(Registries.SOUND_EVENT,MortalExpansion.MOD_ID);
    public static final DeferredHolder<SoundEvent,SoundEvent> HAUNT_TAPPING=SOUNDS.register("haunt_tapping",()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"haunt_tapping")));
    private ModSounds(){}
    public static void register(IEventBus bus){SOUNDS.register(bus);}
}
