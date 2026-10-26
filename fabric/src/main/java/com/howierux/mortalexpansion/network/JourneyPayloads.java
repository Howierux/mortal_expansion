package com.howierux.mortalexpansion.network;
import com.howierux.mortalexpansion.catalog.Catalog;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.nbt.CompoundTag;
public final class JourneyPayloads {
 public record Ui(CompoundTag data) implements CustomPacketPayload {
  public static final Type<Ui> TYPE=new Type<>(Catalog.id("journey_ui"));public static final StreamCodec<io.netty.buffer.ByteBuf,Ui> CODEC=StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG,Ui::data,Ui::new);public Type<? extends CustomPacketPayload> type(){return TYPE;}
 }
 public record Action(String action,BlockPos pos,String text) implements CustomPacketPayload {
  public static final Type<Action> TYPE=new Type<>(Catalog.id("journey_action"));public static final StreamCodec<io.netty.buffer.ByteBuf,Action> CODEC=StreamCodec.composite(ByteBufCodecs.stringUtf8(32),Action::action,BlockPos.STREAM_CODEC,Action::pos,ByteBufCodecs.stringUtf8(32),Action::text,Action::new);public Type<? extends CustomPacketPayload> type(){return TYPE;}
 }
}
