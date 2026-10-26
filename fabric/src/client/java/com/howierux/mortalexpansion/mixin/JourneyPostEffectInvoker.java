package com.howierux.mortalexpansion.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
/** Fabric has no public post-chain loader. Keep this one version-sensitive entry point isolated. */
@Mixin(GameRenderer.class)
public interface JourneyPostEffectInvoker {@Invoker("loadEffect") void mortalExpansion$loadJourneyEffect(ResourceLocation id);}
