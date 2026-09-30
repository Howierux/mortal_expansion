package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.menu.PurificationStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class PurificationStationScreen
        extends AbstractContainerScreen<PurificationStationMenu> {

    public PurificationStationScreen(
            PurificationStationMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 178;
        this.inventoryLabelY = 84;
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

        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF20282D);
        graphics.fill(x + 5, y + 5, x + imageWidth - 5, y + 83, 0xFF35434A);

        // Machine slot frames.
        slotFrame(graphics, x + 43, y + 34);
        slotFrame(graphics, x + 43, y + 58);
        slotFrame(graphics, x + 115, y + 46);
        slotFrame(graphics, x + 67, y + 28);

        // Water tank.
        drawVerticalBar(
                graphics,
                x + 15,
                y + 28,
                14,
                48,
                menu.getWater(),
                menu.getWaterCapacity(),
                0xFF4D8DFF
        );

        // FE.
        drawVerticalBar(
                graphics,
                x + 147,
                y + 28,
                14,
                48,
                menu.getEnergy(),
                menu.getEnergyCapacity(),
                0xFFD34BFF
        );

        // Process bar.
        drawHorizontalBar(
                graphics,
                x + 68,
                y + 48,
                35,
                10,
                menu.getProgress(),
                menu.getMaxProgress(),
                0xFFE8F7F2
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 8, 0xFFFFFF, false);

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.purification_station.water",
                        menu.getWater(),
                        menu.getWaterCapacity()
                ),
                8,
                18,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.purification_station.fe",
                        menu.getEnergy(),
                        menu.getEnergyCapacity()
                ),
                92,
                18,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.purification_station.progress",
                        menu.getProgress(),
                        menu.getMaxProgress()
                ),
                62,
                64,
                0xFFFFFF,
                false
        );
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
