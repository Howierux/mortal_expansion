package com.howierux.mortalexpansion.content;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.quest.*;
import com.howierux.mortalexpansion.network.SpiritInteractionPayloads;
import com.howierux.mortalexpansion.blockentity.*;
import com.howierux.mortalexpansion.machine.*;
import com.howierux.mortalexpansion.fancifier.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.inventory.MenuType;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
public final class ContentRuntime {
 public static final AttachmentType<SpiritQuestState> QUESTS=AttachmentRegistry.create(Catalog.id("spirit_quests"),b->b.initializer(()->SpiritQuestState.EMPTY).persistent(SpiritQuestState.CODEC).copyOnDeath());
 public static final AttachmentType<java.util.Set<String>> PYRAMIDS=AttachmentRegistry.create(Catalog.id("pharaohl_pyramids"),b->b.initializer(java.util.Set::of).persistent(com.mojang.serialization.Codec.STRING.listOf().xmap(java.util.Set::copyOf,java.util.ArrayList::new)).copyOnDeath());
 public static final DataComponentType<Integer> FADING=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("fading"),DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.intRange(1,100)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
 public static final AttachmentType<FadingData> FADING_BLOCKS=AttachmentRegistry.create(Catalog.id("fading_blocks"),b->b.initializer(()->FadingData.EMPTY).persistent(FadingData.CODEC).syncWith(FadingData.STREAM_CODEC,AttachmentSyncPredicate.all()));
 public static final AttachmentType<Integer> FADING_ENTITY=AttachmentRegistry.create(Catalog.id("fading_entity"),b->b.initializer(()->100).persistent(com.mojang.serialization.Codec.intRange(1,100)).syncWith(ByteBufCodecs.VAR_INT,AttachmentSyncPredicate.all()));
 public static final BlockEntityType<MortalGolemBaseBlockEntity> FRAME=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("mortal_golem_base"),BlockEntityType.Builder.of(MortalGolemBaseBlockEntity::new,CatalogBlocks.MORTAL_GOLEM_BASE).build(null));
 public static final BlockEntityType<WispLanternBlockEntity> LANTERN=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("wisp_o_lantern"),BlockEntityType.Builder.of(WispLanternBlockEntity::new,CatalogBlocks.WISP_O_LANTERN).build(null));
 public static final BlockEntityType<EctchantmentEntity> ECTCHANT=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("ectchantment_machine"),BlockEntityType.Builder.of(EctchantmentEntity::new,CatalogBlocks.ECTCHANTMENT_MACHINE).build(null));
 public static final net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType<EctchantmentMenu,net.minecraft.core.BlockPos> ECTCHANT_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("ectchantment_machine"),new net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType<>(EctchantmentMenu::new,net.minecraft.core.BlockPos.STREAM_CODEC));
 public static final MenuType<com.howierux.mortalexpansion.menu.FancifierMenu> FANCIFIER_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("fancifier"),new MenuType<>(com.howierux.mortalexpansion.menu.FancifierMenu::new,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
 public static void initialize(){
  PayloadTypeRegistry.playS2C().register(SpiritInteractionPayloads.Open.TYPE,SpiritInteractionPayloads.Open.CODEC);PayloadTypeRegistry.playC2S().register(SpiritInteractionPayloads.Choice.TYPE,SpiritInteractionPayloads.Choice.CODEC);
  ServerPlayNetworking.registerGlobalReceiver(SpiritInteractionPayloads.Choice.TYPE,(payload,context)->context.server().execute(()->SpiritInteraction.choose(context.player(),payload.token(),payload.action())));
  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher,lookup,env)->SpiritQuestCommands.register(dispatcher));
  net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register(SpiritQuests::killed);
  net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.addPhaseOrdering(net.fabricmc.fabric.api.event.Event.DEFAULT_PHASE,Catalog.id("formation_after_checks"));net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register(Catalog.id("formation_after_checks"),(player,world,hand,hit)->com.howierux.mortalexpansion.haunt.MortalGolemFormation.apply(player,world,hand,hit));
  ServerChunkEvents.CHUNK_LOAD.register(com.howierux.mortalexpansion.haunt.WispLanternConversion::load);ServerChunkEvents.CHUNK_UNLOAD.register(com.howierux.mortalexpansion.haunt.WispLanternConversion::unload);ServerWorldEvents.UNLOAD.register((server,level)->com.howierux.mortalexpansion.haunt.WispLanternConversion.clear(level));
  ServerTickEvents.END_WORLD_TICK.register(com.howierux.mortalexpansion.haunt.WispLanternConversion::tick);
  ServerTickEvents.END_WORLD_TICK.register(level->{for(var player:level.players())if(player.isAlive()&&!player.isSpectator()){com.howierux.mortalexpansion.entity.PharaohlPyramidHandler.tick(player);com.howierux.mortalexpansion.haunt.HaintWitchHutSpawnHandler.tick(player);}});
  team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((be,side)->be.energy,ECTCHANT);net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.registerForBlockEntity(net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage::of,ECTCHANT);
 }
}
