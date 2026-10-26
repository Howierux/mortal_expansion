package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.entity.CombatEntities;
import com.howierux.mortalexpansion.entity.mob.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
public final class CombatClient {
 private static net.minecraft.client.multiplayer.ClientLevel indexedLevel;
 public static void initialize(){
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client->{if(indexedLevel!=client.level){if(indexedLevel!=null){HolySaltBarrierTracker.clear(indexedLevel);SpiritTorchBarrierTracker.clear(indexedLevel);}indexedLevel=client.level;}});
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{if(client.level!=null){HolySaltBarrierTracker.clear(client.level);SpiritTorchBarrierTracker.clear(client.level);}});
  for(var sin:SinAffinity.values())EntityModelLayerRegistry.registerModelLayer(DemonModel.layer(sin),()->DemonModel.createBodyLayer(sin));
  EntityModelLayerRegistry.registerModelLayer(MortalGolemModel.LAYER,MortalGolemModel::createBodyLayer);
  EntityRendererRegistry.register(CombatEntities.SPECTRAL_BOLT,net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
  EntityRendererRegistry.register(CombatEntities.GHOST,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/ghost.png"),m->true,.45F));
  EntityRendererRegistry.register(CombatEntities.PHANTOM,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/phantom.png"),m->false,.45F));
  EntityRendererRegistry.register(CombatEntities.HAUNT,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/haunt.png"),m->true,.6F));
  EntityRendererRegistry.register(CombatEntities.HAINT,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/haint.png"),m->true,.75F));
  EntityRendererRegistry.register(CombatEntities.ELEMENTAL,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/elemental.png"),m->true,.55F));
  EntityRendererRegistry.register(CombatEntities.MUMMY,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/mummy.png"),m->false,.5F));
  EntityRendererRegistry.register(CombatEntities.PHARAOHL,c->new SpectralMobRenderer<>(c,Catalog.id("textures/entity/pharaohl.png"),PharaohlEntity::isCurrentlyIntangible,.65F));
  EntityRendererRegistry.register(CombatEntities.SPIRIT,SpiritVillagerRenderer::new);EntityRendererRegistry.register(CombatEntities.MORTAL_GOLEM,MortalGolemRenderer::new);EntityRendererRegistry.register(CombatEntities.DEMON,DemonRenderer::new);
  EntityRendererRegistry.register(net.minecraft.world.entity.EntityType.PHANTOM,IntangiblePhantomRenderer::new);
  EntityRendererRegistry.register(net.minecraft.world.entity.EntityType.GHAST,IntangibleGhastRenderer::new);
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register((level,chunk)->{HolySaltBarrierTracker.load(level,chunk);SpiritTorchBarrierTracker.load(level,chunk);});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD.register((level,chunk)->{HolySaltBarrierTracker.unload(level,chunk);SpiritTorchBarrierTracker.unload(level,chunk);});
 }
}
