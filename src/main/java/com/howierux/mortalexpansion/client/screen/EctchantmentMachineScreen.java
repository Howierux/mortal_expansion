package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.menu.EctchantmentMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class EctchantmentMachineScreen
        extends AbstractContainerScreen<EctchantmentMachineMenu> {

    public EctchantmentMachineScreen(
            EctchantmentMachineMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 118;
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

        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF20242A);
        graphics.fill(x + 5, y + 5, x + imageWidth - 5, y + imageHeight - 5, 0xFF343A40);

        drawBar(
                graphics,
                x + 14,
                y + 43,
                148,
                12,
                menu.getEctoplasmicCharge(),
                menu.getMaxEctoplasmicCharge(),
                0xFFB8D7D9
        );

        drawBar(
                graphics,
                x + 14,
                y + 79,
                148,
                12,
                menu.getEnergyStored(),
                menu.getEnergyCapacity(),
                0xFFD94CFF
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 10, 9, 0xFFFFFF, false);

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.ectchantment_machine.ecto",
                        menu.getEctoplasmicCharge(),
                        menu.getMaxEctoplasmicCharge()
                ),
                14,
                30,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.ectchantment_machine.fe",
                        menu.getEnergyStored(),
                        menu.getEnergyCapacity()
                ),
                14,
                66,
                0xFFFFFF,
                false
        );
    }

    private static void drawBar(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int value,
            int max,
            int fillColor
    ) {
        graphics.fill(x, y, x + width, y + height, 0xFF15181C);

        if (max <= 0 || value <= 0) {
            return;
        }

        int filled = Math.min(width, Math.max(0, (int) ((long) width * value / max)));
        graphics.fill(x + 1, y + 1, x + filled - 1, y + height - 1, fillColor);
    }
}
