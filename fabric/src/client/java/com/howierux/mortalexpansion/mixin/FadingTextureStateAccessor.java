package com.howierux.mortalexpansion.mixin;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(RenderStateShard.EmptyTextureStateShard.class)
public interface FadingTextureStateAccessor {
    @Invoker("cutoutTexture") Optional<ResourceLocation> mortalExpansion$textureLocation();
}
