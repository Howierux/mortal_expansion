package com.howierux.mortalexpansion.network;
import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public record SpectralTracksPayload(boolean visible, BlockPos target) implements CustomPacketPayload {
    public static final Type<SpectralTracksPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"spectral_tracks"));
    public static final StreamCodec<io.netty.buffer.ByteBuf,SpectralTracksPayload> CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,SpectralTracksPayload::visible,BlockPos.STREAM_CODEC,SpectralTracksPayload::target,SpectralTracksPayload::new);
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(net.neoforged.bus.api.IEventBus bus){bus.addListener(SpectralTracksPayload::handlers);}
    private static void handlers(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event){
        event.registrar("1").playToClient(TYPE,CODEC,(payload,context)->context.enqueueWork(()->com.howierux.mortalexpansion.client.SpectralTracksClient.receive(payload)));
    }
    public static boolean send(ServerPlayer p,BlockPos target){if(!p.connection.hasChannel(TYPE))return false;PacketDistributor.sendToPlayer(p,new SpectralTracksPayload(true,target));return true;}
    public static void clear(ServerPlayer p){if(p.connection.hasChannel(TYPE))PacketDistributor.sendToPlayer(p,new SpectralTracksPayload(false,BlockPos.ZERO));}
}
