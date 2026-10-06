package com.howierux.mortalexpansion.quest;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
public record SpiritQuestState(Optional<SpiritQuest> offer,Optional<SpiritQuest> active,long offerAfter) {
    public static final SpiritQuestState EMPTY=new SpiritQuestState(Optional.empty(),Optional.empty(),0);
    public static final Codec<SpiritQuestState> CODEC=RecordCodecBuilder.create(i->i.group(
        SpiritQuest.CODEC.optionalFieldOf("offer").forGetter(SpiritQuestState::offer),
        SpiritQuest.CODEC.optionalFieldOf("active").forGetter(SpiritQuestState::active),
        Codec.LONG.optionalFieldOf("offer_after",0L).forGetter(SpiritQuestState::offerAfter)).apply(i,SpiritQuestState::new));
}
