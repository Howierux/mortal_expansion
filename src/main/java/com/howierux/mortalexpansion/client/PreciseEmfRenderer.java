package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.EmfScale;
import com.howierux.mortalexpansion.item.EmfReaderItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Supported custom item renderer: baked bulb body plus live numeric text on the device face. */
public final class PreciseEmfRenderer extends BlockEntityWithoutLevelRenderer {
    public PreciseEmfRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay) {
        var mc = Minecraft.getInstance(); var player = mc.player;
        int units = player == null ? -1 : EmfReaderItem.hundredths(stack, player);
        int lit = units < 0 ? 0 : EmfScale.litBulbs(units / 100);
        var model = mc.getModelManager().getModel(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "item/precise_emf_reader_" + lit)));
        mc.getItemRenderer().renderModelLists(model, stack, light, overlay, pose,
                ItemRenderer.getFoilBufferDirect(buffers, RenderType.solid(), true, stack.hasFoil()));
        // Coordinates are in the model's 0..1 space; sit just ahead of the iron case below the lamps.
        String text = EmfScale.decimal(units);
        drawNumber(text, pose, buffers, 9.05F / 16.0F, false);
        // Also readable while the inward use pose turns the opposite face toward the camera.
        drawNumber(text, pose, buffers, 5.95F / 16.0F, true);
    }
    private static void drawNumber(String text, PoseStack pose, MultiBufferSource buffers, float z, boolean rear) {
        var mc = Minecraft.getInstance();
        pose.pushPose();
        pose.translate(0.5F, 0.49F, z);
        if (rear) pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
        pose.scale(0.008F, -0.008F, 0.008F);
        mc.font.drawInBatch(text, -mc.font.width(text) / 2.0F, 0, 0xFF103B24, false,
                pose.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        pose.popPose();
    }
}
