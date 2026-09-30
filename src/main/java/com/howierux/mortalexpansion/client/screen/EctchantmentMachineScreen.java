package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.blockentity.EctchantmentMachineBlockEntity;
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

        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF20242A);
        graphics.fill(x + 5, y + 5, x + imageWidth - 5, y + 83, 0xFF343A40);

        slotFrame(graphics, x + 43, y + 42);
        slotFrame(graphics, x + 79, y + 42);
        slotFrame(graphics, x + 115, y + 42);

        drawBar(
                graphics,
                x + 14,
                y + 22,
                148,
                8,
                menu.getEctoplasmicCharge(),
                menu.getMaxEctoplasmicCharge(),
                0xFFB8D7D9
        );

        drawBar(
                graphics,
                x + 14,
                y + 70,
                148,
                8,
                menu.getEnergyStored(),
                menu.getEnergyCapacity(),
                0xFFD94CFF
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 8, 0xFFFFFF, false);

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.ectchantment_machine.ecto",
                        menu.getEctoplasmicCharge(),
                        menu.getMaxEctoplasmicCharge()
                ),
                14,
                14,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.mortal_expansion.ectchantment_machine.input"),
                39,
                33,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.mortal_expansion.ectchantment_machine.fuel"),
                77,
                33,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.mortal_expansion.ectchantment_machine.output"),
                109,
                33,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                statusText(),
                14,
                61,
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
                80,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.machine.fe_input",
                        menu.getRecentFeInput()
                ),
                98,
                80,
                menu.getRecentFeInput() > 0 ? 0xFFD98CFF : 0xFFB0B0B0,
                false
        );
    }

    private Component statusText() {
        return switch (menu.getStatus()) {
            case EctchantmentMachineBlockEntity.STATUS_NEEDS_RESOURCE ->
                    Component.translatable(
                            "gui.mortal_expansion.ectchantment_machine.status.needs_resource"
                    );
            case EctchantmentMachineBlockEntity.STATUS_INVALID_INPUT ->
                    Component.translatable(
                            "gui.mortal_expansion.ectchantment_machine.status.invalid"
                    );
            case EctchantmentMachineBlockEntity.STATUS_OUTPUT_READY ->
                    Component.translatable(
                            "gui.mortal_expansion.ectchantment_machine.status.output_ready"
                    );
            default ->
                    Component.translatable(
                            "gui.mortal_expansion.ectchantment_machine.status.idle"
                    );
        };
    }

    private static void slotFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 19, y + 19, 0xFF11181C);
        graphics.fill(x, y, x + 18, y + 18, 0xFF69777D);
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

        int filled = Math.min(
                width,
                Math.max(0, (int)((long)width * value / max))
        );
        graphics.fill(
                x + 1,
                y + 1,
                x + filled - 1,
                y + height - 1,
                fillColor
        );
    }
}
