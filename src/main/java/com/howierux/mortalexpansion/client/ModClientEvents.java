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
import net.minecraft.world.level.block.RedStoneWireBlock;
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
        event.registerEntityRenderer(EntityType.GHAST, IntangibleGhastRenderer::new);

        ResourceLocation zombie = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
        ResourceLocation husk = ResourceLocation.withDefaultNamespace("textures/entity/zombie/husk.png");

        event.registerEntityRenderer(ModEntities.GHOST.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> true, 0.45F));
        event.registerEntityRenderer(ModEntities.PHANTOM.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> false, 0.45F));
        event.registerEntityRenderer(ModEntities.SPIRIT.get(),
                ctx -> new PlaceholderHumanoidRenderer<>(ctx, zombie, mob -> true, 0.4F));
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
        event.register(ModMenus.ECTCHANTMENT_MACHINE.get(), EctchantmentMachineScreen::new);
        event.register(ModMenus.ECTOPLASMIC_GENERATOR.get(), EctoplasmicGeneratorScreen::new);
        event.register(ModMenus.ENCHANTMENT_GENERATOR.get(), EnchantmentGeneratorScreen::new);
        event.register(ModMenus.PURIFICATION_STATION.get(), PurificationStationScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
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
                (state, level, pos, tintIndex) -> 0xFFE8FFE8,
                ModBlocks.SPIRITWOOD_SAPLING.get()
        );

        event.register(
                (state, level, pos, tintIndex) -> 0xFF3FA63A,
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
        event.register(
                (state, level, pos, tintIndex) -> {
                    int power = state.getValue(RedStoneWireBlock.POWER);
                    int shade = 150 + (int)(105.0F * power / 15.0F);
                    return (shade << 16) | (shade << 8) | shade;
                },
                ModBlocks.HOLY_SALT.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> 0xFFE7EEEE,
                ModItems.SPIRITWOOD_LOG.get()
        );

        event.register(
                (stack, tintIndex) -> coolFoliage(FoliageColor.getDefaultColor()),
                ModItems.SPIRITWOOD_LEAVES.get()
        );

        event.register(
                (stack, tintIndex) -> 0xFFE8FFE8,
                ModItems.SPIRITWOOD_SAPLING.get()
        );

        event.register(
                (stack, tintIndex) -> 0xFF3FA63A,
                ModItems.GRAVE_MOSS.get()
        );

        event.register(
                (stack, tintIndex) -> 0xFFFFFFFF,
                ModItems.HOLY_SALT.get()
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
