package com.howierux.mortalexpansion.fancifier;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.*;
/** Immutable chunk-local opacity map: no block entities or block identities are replaced. */
public record FadingData(Map<Integer,Integer> entries) {
    public static final FadingData EMPTY=new FadingData(Map.of());
    public FadingData { entries=Map.copyOf(entries); }
    public static final Codec<FadingData> CODEC=Codec.unboundedMap(Codec.STRING,Codec.INT).xmap(map->{
        var result=new HashMap<Integer,Integer>();map.forEach((key,value)->{int index=Integer.parseInt(key);if(index>=0 && index<1_048_576 && FancifierRules.validOpacity(value) && value<100)result.put(index,value);});return new FadingData(result);
    },data->{var result=new HashMap<String,Integer>();data.entries.forEach((key,value)->result.put(key.toString(),value));return result;});
    public static final StreamCodec<RegistryFriendlyByteBuf,FadingData> STREAM_CODEC=new StreamCodec<>() {
        public void encode(RegistryFriendlyByteBuf buf,FadingData data){buf.writeVarInt(data.entries.size());data.entries.forEach((index,opacity)->{buf.writeVarInt(index);buf.writeByte(opacity);});}
        public FadingData decode(RegistryFriendlyByteBuf buf){int size=buf.readVarInt();if(size<0 || size>1_048_576)throw new IllegalArgumentException("Invalid fading map size");var map=new HashMap<Integer,Integer>();for(int i=0;i<size;i++){int index=buf.readVarInt(),opacity=buf.readUnsignedByte();if(index<0 || index>=1_048_576 || !FancifierRules.validOpacity(opacity) || opacity==100)throw new IllegalArgumentException("Invalid fading entry");map.put(index,opacity);}return new FadingData(map);}
    };
}
