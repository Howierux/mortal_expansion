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
    public record State(boolean active) implements CustomPacketPayload {
        public static final Type<State> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spirit_journey_state"));
        public static final StreamCodec<ByteBuf, State> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, State::active, State::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public record WellRecovery(boolean confirm) implements CustomPacketPayload {
        public static final Type<WellRecovery> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwell_recovery"));
        public static final StreamCodec<ByteBuf, WellRecovery> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, WellRecovery::confirm, WellRecovery::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public record WellOffer(boolean available, net.minecraft.core.BlockPos pos) implements CustomPacketPayload {
        public static final Type<WellOffer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwell_offer"));
        public static final StreamCodec<ByteBuf, WellOffer> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, WellOffer::available, net.minecraft.core.BlockPos.STREAM_CODEC, WellOffer::pos, WellOffer::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(Request.TYPE, Request.STREAM_CODEC, (payload, context) -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (payload.choose()) SpiritJourneyHandler.choose(player);
            else SpiritJourneyHandler.sendEligibility(player);
        });
        registrar.playToServer(WellRecovery.TYPE, WellRecovery.STREAM_CODEC, (payload, context) -> {
            if (!(context.player() instanceof ServerPlayer player) || !SpiritJourneyHandler.isActive(player)) return;
            if (payload.confirm()) SpiritJourneyHandler.recoverAtWell(player);
            else {
                var pos = com.howierux.mortalexpansion.spiritwell.SpiritwellIndex.get(player.serverLevel()).nearest(player.serverLevel(), player.position());
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new WellOffer(pos != null, pos == null ? net.minecraft.core.BlockPos.ZERO : pos));
            }
        });
        registrar.playToClient(Eligibility.TYPE, Eligibility.STREAM_CODEC, (payload, context) -> SpiritJourneyClient.handleEligibility(payload, context));
        registrar.playToClient(WellOffer.TYPE, WellOffer.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (net.minecraft.client.Minecraft.getInstance().screen instanceof com.howierux.mortalexpansion.client.screen.SpiritJourneyScreen screen) screen.offer(payload);
        }));
        registrar.playToClient(State.TYPE, State.STREAM_CODEC, (payload, context) -> SpiritJourneyClient.handleState(payload, context));
    }
}
