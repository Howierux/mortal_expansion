package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.machine.PurificationStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class PurificationStationScreen
        extends AbstractContainerScreen<PurificationStationMenu> {

    private static final int MACHINE_HEIGHT = PurificationStationMenu.MACHINE_HEIGHT;
    private static final float LABEL_SCALE = 0.9F;
    private static final ResourceLocation INVENTORY_BACKGROUND =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/container/generic_54.png"
            );

    public PurificationStationScreen(
            PurificationStationMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = MACHINE_HEIGHT + 96;
        this.inventoryLabelY = MACHINE_HEIGHT + 2;
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int x = leftPos;
        int y = topPos;

        graphics.fill(x, y, x + imageWidth, y + MACHINE_HEIGHT, 0xFF20282D);
        graphics.fill(x + 5, y + 5, x + imageWidth - 5, y + MACHINE_HEIGHT - 1, 0xFF35434A);

        graphics.blit(
                INVENTORY_BACKGROUND,
                x,
                y + MACHINE_HEIGHT,
                0,
                126,
                imageWidth,
                96
        );

        slotFrame(graphics, x + 43, y + 49);
        slotFrame(graphics, x + 43, y + 73);
        slotFrame(graphics, x + 115, y + 61);
        slotFrame(graphics, x + 12, y + 87);

        drawVerticalBar(
                graphics, x + 15, y + 44, 14, 41,
                menu.getWater(), menu.getWaterCapacity(), 0xFF4D8DFF
        );

        drawVerticalBar(
                graphics, x + 147, y + 44, 14, 41,
                menu.getEnergy(), menu.getEnergyCapacity(), 0xFFD34BFF
        );

        drawHorizontalBar(
                graphics, x + 20, y + 124, 136, 8,
                menu.getProgress(), menu.getMaxProgress(), 0xFFE8F7F2
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        drawMachineLabel(graphics, title, 8, 8);
        drawMachineLabel(graphics, Component.translatable(
                "gui.mortal_expansion.purification_station.water",
                menu.getWater(), menu.getWaterCapacity()), 8, 20);
        drawMachineLabel(graphics, Component.translatable(
                "gui.mortal_expansion.purification_station.fe",
                menu.getEnergy(), menu.getEnergyCapacity()), 8, 32);
        drawMachineLabel(graphics, Component.translatable(
                "gui.mortal_expansion.purification_station.progress",
                menu.getProgress(), menu.getMaxProgress()), 8, 112);

        graphics.drawString(
                font,
                playerInventoryTitle,
                inventoryLabelX,
                inventoryLabelY,
                0x404040,
                false
        );
    }

    private void drawMachineLabel(GuiGraphics graphics, Component text, int x, int y) {
        float scale = Math.min(LABEL_SCALE, (imageWidth - x - 8F) / Math.max(1, font.width(text)));
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.drawString(font, text, 0, 0, 0xFFFFFF, false);
        graphics.pose().popPose();
    }

    private static void slotFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 19, y + 19, 0xFF11181C);
        graphics.fill(x, y, x + 18, y + 18, 0xFF69777D);
    }

    private static void drawVerticalBar(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int value,
            int max,
            int fillColor
    ) {
        graphics.fill(x, y, x + width, y + height, 0xFF11181C);
        if (max <= 0 || value <= 0) return;

        int filled = Math.min(height, Math.max(0, (int)((long)height * value / max)));
        graphics.fill(
                x + 1,
                y + height - filled,
                x + width - 1,
                y + height - 1,
                fillColor
        );
    }

    private static void drawHorizontalBar(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int value,
            int max,
            int fillColor
    ) {
        graphics.fill(x, y, x + width, y + height, 0xFF11181C);
        if (max <= 0 || value <= 0) return;

        int filled = Math.min(width, Math.max(0, (int)((long)width * value / max)));
        graphics.fill(x + 1, y + 1, x + filled - 1, y + height - 1, fillColor);
    }
}
