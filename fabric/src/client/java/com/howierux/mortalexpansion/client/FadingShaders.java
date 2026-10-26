package com.howierux.mortalexpansion.client;
public final class FadingShaders {
 private static net.minecraft.client.renderer.ShaderInstance lowOpacity;
 public static net.minecraft.client.renderer.ShaderInstance lowOpacity(){return lowOpacity;}
 public static void initialize(){net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.EVENT.register(context->context.register(com.howierux.mortalexpansion.catalog.Catalog.id("fading_low_opacity"),com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY,shader->lowOpacity=shader));}
}
