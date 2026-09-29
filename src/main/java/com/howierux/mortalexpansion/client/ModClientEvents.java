package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.registry.ModMenus;
import com.howierux.mortalexpansion.client.screen.EctchantmentMachineScreen;
import com.howierux.mortalexpansion.client.screen.EctoplasmicGeneratorScreen;
import com.howierux.mortalexpansion.client.screen.EnchantmentGeneratorScreen;
import com.howierux.mortalexpansion.client.screen.GraveNameScreen;
import com.howierux.mortalexpansion.network.GraveNamePayloads;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
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

@EventBusSubscriber(modid = MortalExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientEvents {
    private ModClientEvents() {}

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
    public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                GraveNamePayloads.OpenGraveName.TYPE,
                (payload, context) -> Minecraft.getInstance().setScreen(
                        new GraveNameScreen(payload.pos())
                )
        );
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ECTCHANTMENT_MACHINE.get(), EctchantmentMachineScreen::new);
        event.register(ModMenus.ECTOPLASMIC_GENERATOR.get(), EctoplasmicGeneratorScreen::new);
        event.register(ModMenus.ENCHANTMENT_GENERATOR.get(), EnchantmentGeneratorScreen::new);
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
    }
}
