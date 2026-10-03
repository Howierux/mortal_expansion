package com.howierux.mortalexpansion.network;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.client.SpiritJourneyClient;
import com.howierux.mortalexpansion.respawn.SpiritJourneyHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class SpiritJourneyPayloads {
    private SpiritJourneyPayloads() {}

    public record Request(boolean choose) implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spirit_journey_request"));
        public static final StreamCodec<ByteBuf, Request> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, Request::choose, Request::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record Eligibility(boolean available, boolean entered) implements CustomPacketPayload {
        public static final Type<Eligibility> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spirit_journey_eligibility"));
        public static final StreamCodec<ByteBuf, Eligibility> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Eligibility::available, ByteBufCodecs.BOOL, Eligibility::entered, Eligibility::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static void register(IEventBus bus) { bus.addListener(SpiritJourneyPayloads::registerPayloads); }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(Request.TYPE, Request.STREAM_CODEC, (payload, context) -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (payload.choose()) SpiritJourneyHandler.choose(player);
            else SpiritJourneyHandler.sendEligibility(player);
        });
        registrar.playToClient(Eligibility.TYPE, Eligibility.STREAM_CODEC, (payload, context) -> SpiritJourneyClient.handleEligibility(payload, context));
    }
}
