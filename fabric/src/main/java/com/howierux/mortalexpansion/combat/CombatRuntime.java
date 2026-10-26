package com.howierux.mortalexpansion.combat;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.entity.mob.*;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
public final class CombatRuntime {
 public static final ResourceKey<Enchantment> DEMI_ENCHANTMENT=ResourceKey.create(Registries.ENCHANTMENT,Catalog.id("demi_tangible"));
 public static final DataComponentType<Boolean> DEMI_COMPONENT=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("demi_tangible"),DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
 public static final AttachmentType<Boolean> PROJECTILE=AttachmentRegistry.create(Catalog.id("demi_tangible_projectile"),b->b.persistent(Codec.BOOL));
 public static void initialize(){
  ServerLivingEntityEvents.ALLOW_DAMAGE.register((target,source,amount)->CombatRules.allowed(target,source));
  ServerEntityEvents.ENTITY_LOAD.register(DemiTangibleProjectiles::spawned);
  ServerChunkEvents.CHUNK_LOAD.register((level,chunk)->{HolySaltBarrierTracker.load(level,chunk);SpiritTorchBarrierTracker.load(level,chunk);});
  ServerChunkEvents.CHUNK_UNLOAD.register((level,chunk)->{HolySaltBarrierTracker.unload(level,chunk);SpiritTorchBarrierTracker.unload(level,chunk);});
  ServerWorldEvents.UNLOAD.register((server,level)->{HolySaltBarrierTracker.clear(level);SpiritTorchBarrierTracker.clear(level);});
 }
}
