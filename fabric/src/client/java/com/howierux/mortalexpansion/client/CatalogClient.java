package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.*;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;

/** Client-only render setup; server registration never references rendering classes. */
public final class CatalogClient {
 private static void colors(net.minecraft.world.level.block.Block block,int color) {
  net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register((state,world,pos,tint) -> color,block);
  if(block.asItem()!=net.minecraft.world.item.Items.AIR) net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack,tint) -> color,block.asItem());
 }
 private static int coolFoliage(int color) {
  int r=Math.max(0,((color>>16)&255)-8),g=Math.min(255,((color>>8)&255)+2),b=Math.min(255,(color&255)+14);
  return 0xFF000000|(r<<16)|(g<<8)|b;
 }
 public static void initialize() {
  CombatClient.initialize();
  net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(CatalogBlocks.FANCIFIER,net.minecraft.client.renderer.RenderType.translucent());
  FadingShaders.initialize();FadingNativeClient.fabricInitialize();
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(com.howierux.mortalexpansion.network.SpiritInteractionPayloads.Open.TYPE,(payload,context)->context.client().execute(()->context.client().setScreen(new SpiritOptionsScreen(payload))));
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.content.ContentRuntime.FANCIFIER_MENU,com.howierux.mortalexpansion.client.screen.FancifierScreen::new);
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.content.ContentRuntime.ECTCHANT_MENU,com.howierux.mortalexpansion.client.screen.EctchantmentMachineScreen::new); JourneyClient.initialize();
  net.minecraft.client.renderer.item.ItemProperties.register(CatalogItems.EMF_READER,Catalog.id("emf_level"),(stack,world,holder,seed)->EmfReaderClient.modelLevel(stack,holder)/12.0F);
  net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register(EmfReaderClient::render);
  net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(CatalogItems.PRECISE_EMF_READER,new PreciseEmfRenderer());
  net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.register(context->{for(int i=0;i<=12;i++)context.addModels(Catalog.id("item/precise_emf_reader_"+i));});
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(com.howierux.mortalexpansion.guide.BookOpenPayload.TYPE,(payload,context)->context.client().execute(()->EctceteraBookScreen.open(payload.hand())));
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.machine.Machines.RESEARCH_MENU,com.howierux.mortalexpansion.client.screen.MortalityResearchStationScreen::new);
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.machine.Machines.PURITY_MENU,com.howierux.mortalexpansion.client.screen.AltarScreen::new);
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.machine.Machines.IMPURITY_MENU,com.howierux.mortalexpansion.client.screen.AltarScreen::new);
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.machine.Machines.STATION_MENU,com.howierux.mortalexpansion.client.screen.PurificationStationScreen::new);
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.machine.Machines.ECTO_MENU,com.howierux.mortalexpansion.client.screen.EctoplasmicGeneratorScreen::new);
  net.minecraft.client.gui.screens.MenuScreens.register(com.howierux.mortalexpansion.machine.Machines.ENCHANT_MENU,com.howierux.mortalexpansion.client.screen.EnchantmentGeneratorScreen::new);
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(com.howierux.mortalexpansion.machine.Machines.FLUID,EctankRenderer::new);

  net.minecraft.client.renderer.item.ItemProperties.register(CatalogItems.BOOK_OF_ECTCETERA,Catalog.id("book_tier"),(stack,level,entity,seed) -> stack.getOrDefault(Catalog.BOOK_TIER,0)/4.0F);
  net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register((state,world,pos,tint) -> coolFoliage(world!=null&&pos!=null?net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(world,pos):net.minecraft.world.level.FoliageColor.getDefaultColor()),CatalogBlocks.SPIRITWOOD_LEAVES);
  net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack,tint) -> coolFoliage(net.minecraft.world.level.FoliageColor.getDefaultColor()),CatalogItems.SPIRITWOOD_LEAVES);

  colors(CatalogBlocks.HAUNT_FURNACE,0xFF686868); colors(CatalogBlocks.HAINT_FURNACE,0xFFC4E8FF);
  colors(CatalogBlocks.SPIRITWOOD_LOG,0xFFE7EEEE); colors(CatalogBlocks.GRAVE_MOSS,0xFFFFFFFF);
  colors(CatalogBlocks.LIQUID_ECTOPLASM_CAULDRON,0xFF42A83B); colors(CatalogBlocks.HOLY_WATER_CAULDRON,0xFFDDEEFF); colors(CatalogBlocks.PURIFIED_ECTOPLASM_CAULDRON,0xFFC6F4FF);

  CatalogBlocks.ALL.forEach((id,block) -> {
   if(id.contains("ectank")||id.contains("ecto_pipe")||id.equals("fancifier"))
    BlockRenderLayerMap.INSTANCE.putBlock(block,RenderType.translucent());
   else if(id.contains("leaves")||id.contains("sapling")||id.contains("dreamcatcher")||id.contains("torch")||id.equals("grave_moss")||id.endsWith("door")||id.equals("holy_salt"))
    BlockRenderLayerMap.INSTANCE.putBlock(block,RenderType.cutout());
  });
  fluid(CatalogFluids.LIQUID_ECTOPLASM,0x42A83B); fluid(CatalogFluids.HOLY_WATER,0xDDEEFF); fluid(CatalogFluids.PURIFIED_ECTOPLASM,0xC6F4FF);
  net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack,tint) -> 0x42A83B,CatalogItems.LIQUID_ECTOPLASM_BUCKET);
  net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack,tint) -> 0xDDEEFF,CatalogItems.HOLY_WATER_BUCKET);
  net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack,tint) -> 0xC6F4FF,CatalogItems.PURIFIED_ECTOPLASM_BUCKET);
  ItemTooltipCallback.EVENT.register((stack,context,type,lines) -> {
   var id=BuiltInRegistries.ITEM.getKey(stack.getItem());
   if(id.getNamespace().equals("mortal_expansion") && pending(id.getPath()))
    lines.add(Component.translatable("tooltip.mortal_expansion.fabric.pending").withStyle(ChatFormatting.DARK_GRAY));
  });
 }
 private static void fluid(CatalogFluids.Pair pair,int color) {
  FluidRenderHandlerRegistry.INSTANCE.register(pair.source,pair.flowing,new SimpleFluidRenderHandler(SimpleFluidRenderHandler.WATER_STILL,SimpleFluidRenderHandler.WATER_FLOWING,color));
  BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),pair.source,pair.flowing);
 }
 private static boolean pending(String id) {
  return !(java.util.Set.of("spiritwood_sapling","emf_reader","precise_emf_reader","mortality_research_station","book_of_ectcetera","ritual_star","altar_of_purity","altar_of_impurity","haunt_furnace","haint_furnace","purification_station","purification_ward","ecto_pipe","purified_ecto_pipe","ectank","purified_ectank","ectoplasmic_generator","enchantment_generator","ectchantment_machine","fancifier","wisp_o_lantern","mortal_golem_base").contains(id)||id.equals("bloodstone")||id.equals("grave")||id.equals("spiritwell")||id.equals("spirit_boon")||id.equals("pharaohl_scepter")||id.startsWith("bloodstone_")||id.startsWith("spectralite_")||id.startsWith("ghost_garb_")||id.startsWith("haint_garb_")||id.equals("gravestone_sword")||id.equals("purified_gravestone_sword")||id.equals("demonic_ash")||id.endsWith("ectoplasm")||id.endsWith("ectothread")||id.equals("gravestone")||id.equals("purified_gravestone")||id.startsWith("spiritwood_")&&!id.endsWith("sapling")||id.startsWith("purified_spiritwood_")||id.equals("stone_fence")||id.startsWith("bloodstone_ore"));
 }
}
