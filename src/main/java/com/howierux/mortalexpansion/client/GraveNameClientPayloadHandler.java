package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.client.screen.GraveNameScreen;
import com.howierux.mortalexpansion.network.GraveNamePayloads;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class GraveNameClientPayloadHandler {
    private GraveNameClientPayloadHandler() {}

    public static void handle(
            GraveNamePayloads.OpenGraveName payload,
            IPayloadContext context
    ) {
        Minecraft.getInstance().setScreen(
                new GraveNameScreen(payload.pos())
        );
    }
}
