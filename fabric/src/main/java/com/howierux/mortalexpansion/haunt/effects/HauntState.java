package com.howierux.mortalexpansion.haunt.effects;
import com.howierux.mortalexpansion.catalog.Catalog;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.nbt.CompoundTag;
public final class HauntState {
 public static final AttachmentType<CompoundTag> STATE=AttachmentRegistry.create(Catalog.id("haunt_effect_state"),b->b.initializer(CompoundTag::new).persistent(CompoundTag.CODEC).copyOnDeath());
 public static final AttachmentType<CompoundTag> MANAGED=AttachmentRegistry.create(Catalog.id("haunt_managed_state"),b->b.initializer(CompoundTag::new).persistent(CompoundTag.CODEC));
 public static CompoundTag data(Object target){return ((AttachmentTarget)target).getAttachedOrCreate(MANAGED);}
 public static void initialize(){}
}
