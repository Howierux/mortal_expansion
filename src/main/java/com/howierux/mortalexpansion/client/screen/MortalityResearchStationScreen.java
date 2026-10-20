package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.menu.MortalityResearchStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class MortalityResearchStationScreen extends AbstractContainerScreen<MortalityResearchStationMenu> {
    private static final int TEXT_COLOR = 0xCFC4EF;

    public MortalityResearchStationScreen(MortalityResearchStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 208;
        inventoryLabelY = 114;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("gui.mortal_expansion.research.study"),
                button -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0))
                .bounds(leftPos + 69, topPos + 45, 96, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + 176, topPos + 112, 0xFF24213B);
        graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png"),
                leftPos, topPos + 112, 0, 126, 176, 96);
        graphics.fill(leftPos + 29, topPos + 44, leftPos + 47, topPos + 62, 0xFFAAA3BA);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 8, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("gui.mortal_expansion.research.bonus", menu.bonus()),
                8, 25, TEXT_COLOR, false);
        graphics.drawString(font, Component.translatable("gui.mortal_expansion.research.rates"),
                8, 78, TEXT_COLOR, false);
        drawContribution(graphics, "gui.mortal_expansion.research.raw", 8, 92, (menu.ectoType() & 1) != 0);
        drawContribution(graphics, "gui.mortal_expansion.research.purified", 88, 92, (menu.ectoType() & 2) != 0);
        graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
    }

    /** Read-only indicators follow the synchronized pending bonus; they are not selection buttons. */
    private void drawContribution(GuiGraphics graphics, String key, int x, int y, boolean contributed) {
        Component label = Component.translatable(key);
        graphics.drawString(font, label, x, y, TEXT_COLOR, false);
        int boxX = x + font.width(label) + 4;
        graphics.fill(boxX, y, boxX + 8, y + 8, 0xFFCFC4EF);
        graphics.fill(boxX + 1, y + 1, boxX + 7, y + 7, 0xFF24213B);
        if (contributed) {
            graphics.fill(boxX + 2, y + 4, boxX + 3, y + 5, 0xFF9AE8AD);
            graphics.fill(boxX + 3, y + 5, boxX + 4, y + 6, 0xFF9AE8AD);
            graphics.fill(boxX + 4, y + 4, boxX + 5, y + 5, 0xFF9AE8AD);
            graphics.fill(boxX + 5, y + 3, boxX + 6, y + 4, 0xFF9AE8AD);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
