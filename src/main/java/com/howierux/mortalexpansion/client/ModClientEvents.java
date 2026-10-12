package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.registry.ModMenus;
import com.howierux.mortalexpansion.registry.ModFluids;
import com.howierux.mortalexpansion.client.screen.EctchantmentMachineScreen;
import com.howierux.mortalexpansion.client.screen.EctoplasmicGeneratorScreen;
import com.howierux.mortalexpansion.client.screen.EnchantmentGeneratorScreen;
import com.howierux.mortalexpansion.client.screen.PurificationStationScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;

@EventBusSubscriber(modid = MortalExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientEvents {
    private ModClientEvents() {}
    @SubscribeEvent public static void emfModels(net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional event) {
        for (int lit = 0; lit <= 6; lit++) event.register(net.minecraft.client.resources.model.ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "item/precise_emf_reader_" + lit)));
    }
    @SubscribeEvent public static void emfLayers(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
        event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "emf_reader"), EmfReaderClient::render);
    }
    @SubscribeEvent public static void emfProperties(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> net.minecraft.client.renderer.item.ItemProperties.register(ModItems.EMF_READER.get(),
                ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "emf_level"),
                (stack, level, holder, seed) -> EmfReaderClient.modelLevel(stack, holder)));
    }

    @SubscribeEvent public static void garbColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> tint == 0 ? net.minecraft.world.item.component.DyedItemColor.getOrDefault(stack, 0x80C71F) : -1,
                ModItems.GHOST_GARB_HELMET.get(), ModItems.GHOST_GARB_CHESTPLATE.get(), ModItems.GHOST_GARB_LEGGINGS.get(), ModItems.GHOST_GARB_BOOTS.get(),
                ModItems.HAINT_GARB_HELMET.get(), ModItems.HAINT_GARB_CHESTPLATE.get(), ModItems.HAINT_GARB_LEGGINGS.get(), ModItems.HAINT_GARB_BOOTS.get());
    }
    @SubscribeEvent public static void registerKeys(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) {
        event.register(SpiritJourneyClient.JOURNEY_MENU);
    }

    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation WATER_FLOW =
            ResourceLocation.withDefaultNamespace("block/water_flow");
    private static final ResourceLocation WATER_OVERLAY =
            ResourceLocation.withDefaultNamespace("block/water_overlay");

    private static IClientFluidTypeExtensions fluidVisuals(int tint) {
        return new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor() {
                return tint;
            }

            @Override
            public ResourceLocation getStillTexture() {
                return WATER_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return WATER_FLOW;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return WATER_OVERLAY;
            }
        };
    }

    @SubscribeEvent
    public static void registerFluidVisuals(RegisterClientExtensionsEvent event) {
        event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions() {
            private PreciseEmfRenderer preciseRenderer;
            @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (preciseRenderer == null) preciseRenderer = new PreciseEmfRenderer();
                return preciseRenderer;
            }
            @Override public net.minecraft.client.model.HumanoidModel.ArmPose getArmPose(
                    net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.InteractionHand hand,
                    net.minecraft.world.item.ItemStack stack) {
                return com.howierux.mortalexpansion.item.EmfReaderItem.personal(stack, entity)
                        ? net.minecraft.client.model.HumanoidModel.ArmPose.BLOCK : null;
            }
            @Override public boolean applyForgeHandTransform(com.mojang.blaze3d.vertex.PoseStack pose,
                    net.minecraft.client.player.LocalPlayer player, net.minecraft.world.entity.HumanoidArm arm,
                    net.minecraft.world.item.ItemStack stack, float partialTick, float equip, float swing) {
                if (stack.is(ModItems.EMF_READER.get())) {
                    int side = arm == net.minecraft.world.entity.HumanoidArm.RIGHT ? 1 : -1;
                    boolean personal = com.howierux.mortalexpansion.item.EmfReaderItem.personal(stack, player);
                    // The bulb face is +Z. Keep it toward the camera in both hands and modes.
                    pose.translate(side * (personal ? 0.30F : 0.42F), (personal ? -0.25F : -0.35F) - equip * 0.6F,
                            personal ? -0.62F : -0.78F);
                    if (personal) pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-15.0F));
                    return true;
                }
                if (!com.howierux.mortalexpansion.item.EmfReaderItem.personal(stack, player)) return false;
                int side = arm == net.minecraft.world.entity.HumanoidArm.RIGHT ? 1 : -1;
                pose.translate(side * 0.42F, -0.35F - equip * 0.6F, -0.55F);
                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(side * 150.0F));
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-20.0F));
                return true;
            }
        }, ModItems.EMF_READER.get(), ModItems.PRECISE_EMF_READER.get());
        event.registerFluidType(
                fluidVisuals(0xFF42A83B),
                ModFluids.LIQUID_ECTOPLASM_TYPE.get()
        );
        event.registerFluidType(
                fluidVisuals(0xA8DDEEFF),
                ModFluids.HOLY_WATER_TYPE.get()
        );
        event.registerFluidType(
                fluidVisuals(0xFFC6F4FF),
                ModFluids.PURIFIED_ECTOPLASM_TYPE.get()
        );
    }

    private static int coolFoliage(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.max(0, r - 8);
        g = Math.min(255, g + 2);
        b = Math.min(255, b + 14);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(com.howierux.mortalexpansion.registry.ModBlockEntities.SPIRITWELL.get(),
                SpiritwellRenderer::new);
        event.registerEntityRenderer(EntityType.GHAST, IntangibleGhastRenderer::new);
        event.registerEntityRenderer(ModEntities.PLAYER_CORPSE.get(), PlayerCorpseRenderer::new);

        ResourceLocation zombie = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
        ResourceLocation husk = ResourceLocation.withDefaultNamespace("textures/entity/zombie/husk.png");

        event.registerEntityRenderer(ModEntities.GHOST.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> true, 0.45F));
        event.registerEntityRenderer(ModEntities.PHANTOM.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> false, 0.45F));
        event.registerEntityRenderer(ModEntities.SPIRIT.get(), SpiritVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.ELEMENTAL.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> true, 0.55F));
        event.registerEntityRenderer(ModEntities.HAUNT.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> true, 0.6F));
        event.registerEntityRenderer(ModEntities.HAINT.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> true, 0.75F));
        event.registerEntityRenderer(ModEntities.MUMMY.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, husk, mob -> false, 0.5F));
        event.registerEntityRenderer(ModEntities.PHARAOHL.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(
                        ctx,
                        husk,
                        mob -> mob.isCurrentlyIntangible(),
                        0.7F
                ));

        event.registerBlockEntityRenderer(
                com.howierux.mortalexpansion.registry.ModBlockEntities.GRAVE.get(),
                GraveBlockEntityRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ALTAR_OF_IMPURITY.get(), com.howierux.mortalexpansion.client.screen.AltarOfPurityScreen::new);
        event.register(ModMenus.MORTALITY_RESEARCH_STATION.get(),com.howierux.mortalexpansion.client.screen.MortalityResearchStationScreen::new);
        event.register(ModMenus.ALTAR_OF_PURITY.get(), com.howierux.mortalexpansion.client.screen.AltarOfPurityScreen::new);
        event.register(ModMenus.ECTCHANTMENT_MACHINE.get(), EctchantmentMachineScreen::new);
        event.register(ModMenus.ECTOPLASMIC_GENERATOR.get(), EctoplasmicGeneratorScreen::new);
        event.register(ModMenus.ENCHANTMENT_GENERATOR.get(), EnchantmentGeneratorScreen::new);
        event.register(ModMenus.PURIFICATION_STATION.get(), PurificationStationScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state,level,pos,tint)->0xFF686868,ModBlocks.HAUNT_FURNACE.get());
        event.register((state,level,pos,tint)->0xFFC4E8FF,ModBlocks.HAINT_FURNACE.get());
        event.register(
                (state, level, pos, tintIndex) -> 0xFFE7EEEE,
                ModBlocks.SPIRITWOOD_LOG.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> {
                    int base = level != null && pos != null
                            ? BiomeColors.getAverageFoliageColor(level, pos)
                            : FoliageColor.getDefaultColor();
                    return coolFoliage(base);
                },
                ModBlocks.SPIRITWOOD_LEAVES.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> 0xFFFFFFFF,
                ModBlocks.SPIRITWOOD_SAPLING.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> 0xFFFFFFFF,
                ModBlocks.GRAVE_MOSS.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> 0xFF42A83B,
                ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get()
        );
        event.register(
                (state, level, pos, tintIndex) -> 0xA8DDEEFF,
                ModBlocks.HOLY_WATER_CAULDRON.get()
        );
        event.register(
                (state, level, pos, tintIndex) -> 0xFFC6F4FF,
                ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack,tint)->0xFF686868,ModItems.HAUNT_FURNACE.get());
        event.register((stack,tint)->0xFFC4E8FF,ModItems.HAINT_FURNACE.get());
        event.register(
                (stack, tintIndex) -> 0xFFE7EEEE,
                ModItems.SPIRITWOOD_LOG.get()
        );

        event.register(
                (stack, tintIndex) -> coolFoliage(FoliageColor.getDefaultColor()),
                ModItems.SPIRITWOOD_LEAVES.get()
        );

        event.register(
                (stack, tintIndex) -> 0xFFFFFFFF,
                ModItems.SPIRITWOOD_SAPLING.get()
        );

        event.register(
                (stack, tintIndex) -> 0xFFFFFFFF,
                ModItems.GRAVE_MOSS.get()
        );


        DynamicFluidContainerModel.Colors fluidBucketColors =
                new DynamicFluidContainerModel.Colors();
        event.register(
                fluidBucketColors,
                ModItems.LIQUID_ECTOPLASM_BUCKET.get(),
                ModItems.HOLY_WATER_BUCKET.get(),
                ModItems.PURIFIED_ECTOPLASM_BUCKET.get()
        );
    }
}
