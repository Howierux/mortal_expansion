package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.Catalog;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
public record BookOpenPayload(InteractionHand hand) implements CustomPacketPayload {
 public static final Type<BookOpenPayload> TYPE=new Type<>(Catalog.id("open_ectcetera"));
 public static final StreamCodec<RegistryFriendlyByteBuf,BookOpenPayload> CODEC=StreamCodec.of((buffer,p)->buffer.writeEnum(p.hand()),buffer->new BookOpenPayload(buffer.readEnum(InteractionHand.class)));
 @Override public Type<BookOpenPayload> type(){return TYPE;}
}
