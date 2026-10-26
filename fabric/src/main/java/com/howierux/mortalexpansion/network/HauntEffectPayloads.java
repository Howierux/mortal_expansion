package com.howierux.mortalexpansion.network;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import com.howierux.mortalexpansion.catalog.Catalog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
public final class HauntEffectPayloads {
 public record Shadow(boolean visible,double x,double y,double z) implements CustomPacketPayload {
  public static final Type<Shadow> TYPE=new Type<>(Catalog.id("haunt_shadow"));
  public static final StreamCodec<io.netty.buffer.ByteBuf,Shadow> CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,Shadow::visible,ByteBufCodecs.DOUBLE,Shadow::x,ByteBufCodecs.DOUBLE,Shadow::y,ByteBufCodecs.DOUBLE,Shadow::z,Shadow::new);
  public Type<? extends CustomPacketPayload> type(){return TYPE;}
 }
 public static boolean shadow(ServerPlayer p,Vec3 at){if(!ServerPlayNetworking.canSend(p,Shadow.TYPE))return false;ServerPlayNetworking.send(p,new Shadow(true,at.x,at.y,at.z));return true;}
 public static void clearShadow(ServerPlayer p){if(ServerPlayNetworking.canSend(p,Shadow.TYPE))ServerPlayNetworking.send(p,new Shadow(false,0,0,0));}
}
