package com.howierux.mortalexpansion.network;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class GraveNamePayloads {
    private GraveNamePayloads() {}

    public record OpenGraveName(BlockPos pos) implements CustomPacketPayload {
        public static final Type<OpenGraveName> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(
                        MortalExpansion.MOD_ID,
                        "open_grave_name"
                )
        );

        public static final StreamCodec<ByteBuf, OpenGraveName> STREAM_CODEC =
                StreamCodec.composite(
                        BlockPos.STREAM_CODEC,
                        OpenGraveName::pos,
                        OpenGraveName::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SetGraveName(BlockPos pos, String name) implements CustomPacketPayload {
        public static final Type<SetGraveName> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(
                        MortalExpansion.MOD_ID,
                        "set_grave_name"
                )
        );

        public static final StreamCodec<ByteBuf, SetGraveName> STREAM_CODEC =
                StreamCodec.composite(
                        BlockPos.STREAM_CODEC,
                        SetGraveName::pos,
                        ByteBufCodecs.STRING_UTF8,
                        SetGraveName::name,
                        SetGraveName::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(GraveNamePayloads::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenGraveName.TYPE,
                OpenGraveName.STREAM_CODEC
        );

        registrar.playToServer(
                SetGraveName.TYPE,
                SetGraveName.STREAM_CODEC,
                GraveNamePayloads::handleSetName
        );
    }

    private static void handleSetName(SetGraveName payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (player.distanceToSqr(
                payload.pos().getX() + 0.5D,
                payload.pos().getY() + 0.5D,
                payload.pos().getZ() + 0.5D
        ) > 64.0D) {
            return;
        }

        if (player.level().getBlockEntity(payload.pos()) instanceof GraveBlockEntity grave) {
            grave.trySetInscription(player, payload.name());
        }
    }
}
