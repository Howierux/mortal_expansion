package com.howierux.mortalexpansion.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/** Version-independent objective/reward tier snapshot; no live Spirit reference is required. */
public record SpiritQuest(String id,ResourceLocation target,int required,int progress,int difficulty,String giver,long expires) {
    public static final Codec<SpiritQuest> CODEC=RecordCodecBuilder.create(i->i.group(
        Codec.STRING.fieldOf("id").forGetter(SpiritQuest::id),ResourceLocation.CODEC.fieldOf("target").forGetter(SpiritQuest::target),
        Codec.intRange(1,16).fieldOf("required").forGetter(SpiritQuest::required),Codec.intRange(0,16).fieldOf("progress").forGetter(SpiritQuest::progress),
        Codec.intRange(1,3).fieldOf("difficulty").forGetter(SpiritQuest::difficulty),Codec.STRING.fieldOf("giver").forGetter(SpiritQuest::giver),
        Codec.LONG.fieldOf("expires").forGetter(SpiritQuest::expires)).apply(i,SpiritQuest::new));
    public SpiritQuest advance(){return new SpiritQuest(id,target,required,Math.min(required,progress+1),difficulty,giver,expires);}
}
