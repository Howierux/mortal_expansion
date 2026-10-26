package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
public final class KnowledgeData {
 public static final DataComponentType<Integer> BOOK_SECRETS=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("ectcetera_secrets"),DataComponentType.<Integer>builder().persistent(Codec.intRange(0,127)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
 public static final DataComponentType<Integer> DAMNED_SECRETS=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("damned_secrets"),DataComponentType.<Integer>builder().persistent(Codec.intRange(0,255)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
 public static final AttachmentType<Integer> KNOWLEDGE=number("ectcetera_knowledge",127,true);
 public static final AttachmentType<Integer> BONUS=number("mortality_research_bonus",100,true);
 public static final AttachmentType<Integer> ECTO=number("mortality_research_ecto",3,true);
 public static final AttachmentType<Integer> CRAFTER=number("crafter_knowledge",127,false);
 public static final AttachmentType<Integer> HINTED=number("ectcetera_hint",1,false);
 public static final AttachmentType<Long> NEXT_INSIGHT=AttachmentRegistry.create(Catalog.id("next_insight"),b->b.initializer(()->0L).persistent(Codec.LONG).copyOnDeath());
 private static AttachmentType<Integer> number(String id,int max,boolean sync){return AttachmentRegistry.create(Catalog.id(id),b->{b.initializer(()->0).persistent(Codec.intRange(0,max)).copyOnDeath();if(sync)b.syncWith(ByteBufCodecs.VAR_INT,AttachmentSyncPredicate.targetOnly());});}
 public static int get(AttachmentTarget target,AttachmentType<Integer> type){return target.getAttachedOrCreate(type);}
 public static void initialize(){}
}
