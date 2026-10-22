package com.howierux.mortalexpansion.client;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
/** Native translucent shaders discard alpha below 10%; keep the requested 1..9% visible. */
@EventBusSubscriber(modid="mortal_expansion",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class FadingShaders {
    private static ShaderInstance lowOpacity;
    public static ShaderInstance lowOpacity(){return lowOpacity;}
    @SubscribeEvent public static void register(RegisterShadersEvent event) throws java.io.IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("mortal_expansion:fading_low_opacity"),DefaultVertexFormat.NEW_ENTITY),shader->lowOpacity=shader);
    }
}
