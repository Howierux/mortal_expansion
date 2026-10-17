package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
public final class ModSoundsProvider extends SoundDefinitionsProvider {
    public ModSoundsProvider(PackOutput output,ExistingFileHelper helper){super(output,MortalExpansion.MOD_ID,helper);}
    @Override public void registerSounds(){
        add(ModSounds.HAUNT_TAPPING.get(),definition().subtitle("subtitle.mortal_expansion.haunt_tapping")
            .with(sound(ResourceLocation.withDefaultNamespace("dig/wood1")),
                  sound(ResourceLocation.withDefaultNamespace("dig/wood2")),
                  sound(ResourceLocation.withDefaultNamespace("dig/wood3")),
                  sound(ResourceLocation.withDefaultNamespace("dig/wood4"))));
    }
}
