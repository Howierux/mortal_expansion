package com.howierux.mortalexpansion.network;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.client.HauntShadowClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class HauntEffectPayloads {
    private HauntEffectPayloads() {}
    public record Shadow(boolean visible, double x, double y, double z) implements CustomPacketPayload {
        public static final Type<Shadow> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haunt_shadow"));
        public static final StreamCodec<ByteBuf, Shadow> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Shadow::visible, ByteBufCodecs.DOUBLE, Shadow::x,
                ByteBufCodecs.DOUBLE, Shadow::y, ByteBufCodecs.DOUBLE, Shadow::z, Shadow::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public static void register(IEventBus bus) { bus.addListener(HauntEffectPayloads::registerPayloads); }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(Shadow.TYPE, Shadow.CODEC,
                (payload, context) -> HauntShadowClient.handle(payload, context));
    }
    public static boolean shadow(ServerPlayer player, Vec3 pos) {
        if (!player.connection.hasChannel(Shadow.TYPE)) return false;
        PacketDistributor.sendToPlayer(player, new Shadow(true, pos.x, pos.y, pos.z));
        return true;
    }
    public static void clearShadow(ServerPlayer player) {
        if (player.connection.hasChannel(Shadow.TYPE)) PacketDistributor.sendToPlayer(player, new Shadow(false, 0, 0, 0));
    }
}
