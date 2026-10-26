package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.machine.EctchantmentEntity;
import com.howierux.mortalexpansion.machine.EctchantmentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class EctchantmentMachineScreen
        extends AbstractContainerScreen<EctchantmentMenu> {

    private static final int MACHINE_HEIGHT = 112;
    private static final ResourceLocation INVENTORY_BACKGROUND =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/container/generic_54.png"
            );

    public EctchantmentMachineScreen(
            EctchantmentMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 208;
        this.inventoryLabelY = 114;
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

        graphics.fill(x, y, x + imageWidth, y + MACHINE_HEIGHT, 0xFF20242A);
        graphics.fill(x + 5, y + 5, x + imageWidth - 5, y + MACHINE_HEIGHT - 1, 0xFF343A40);

        graphics.blit(
                INVENTORY_BACKGROUND,
                x,
                y + MACHINE_HEIGHT,
                0,
                126,
                imageWidth,
                96
        );

        slotFrame(graphics, x + 43, y + 57);
        slotFrame(graphics, x + 79, y + 57);
        slotFrame(graphics, x + 115, y + 57);

        drawBar(
                graphics,
                x + 14,
                y + 31,
                148,
                8,
                menu.getEctoplasmicCharge(),
                menu.getMaxEctoplasmicCharge(),
                0xFFB8D7D9
        );

        drawBar(
                graphics,
                x + 14,
                y + 102,
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
                        "gui.mortal_expansion.ectchantment_machine.ecto_charge",
                        menu.getEctoplasmicCharge(),
                        menu.getMaxEctoplasmicCharge()
                ),
                14, 20, 0xFFFFFF, false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.mortal_expansion.ectchantment_machine.input"),
                39, 46, 0xFFFFFF, false
        );
        graphics.drawString(
                font,
                Component.translatable("gui.mortal_expansion.ectchantment_machine.ecto_slot"),
                76, 46, 0xFFFFFF, false
        );
        graphics.drawString(
                font,
                Component.translatable("gui.mortal_expansion.ectchantment_machine.output"),
                109, 46, 0xFFFFFF, false
        );

        graphics.drawString(font, statusText(), 14, 80, 0xFFFFFF, false);

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.mortal_expansion.ectchantment_machine.fe",
                        menu.getEnergyStored(),
                        menu.getEnergyCapacity()
                ),
                14, 91, 0xFFFFFF, false
        );

        graphics.drawString(
                font,
                playerInventoryTitle,
                inventoryLabelX,
                inventoryLabelY,
                0x404040,
                false
        );
    }

    private Component statusText() {
        return switch (menu.getStatus()) {
            case EctchantmentEntity.STATUS_NEEDS_RESOURCE ->
                    Component.translatable(
                            "gui.mortal_expansion.ectchantment_machine.status.needs_resource"
                    );
            case EctchantmentEntity.STATUS_INVALID_INPUT ->
                    Component.translatable(
                            "gui.mortal_expansion.ectchantment_machine.status.invalid"
                    );
            case EctchantmentEntity.STATUS_OUTPUT_READY ->
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
        if (max <= 0 || value <= 0) return;

        int filled = Math.min(width, Math.max(0, (int)((long)width * value / max)));
        graphics.fill(x + 1, y + 1, x + filled - 1, y + height - 1, fillColor);
    }
}
