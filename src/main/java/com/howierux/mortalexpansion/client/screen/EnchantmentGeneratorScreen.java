package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.menu.EnchantmentGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class EnchantmentGeneratorScreen extends AbstractContainerScreen<EnchantmentGeneratorMenu> {
    public EnchantmentGeneratorScreen(EnchantmentGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176; imageHeight = 208; inventoryLabelY = 114;
    }
    @Override protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        graphics.fill(x, y, x + 176, y + 112, 0xFF201D2A);
        graphics.fill(x + 5, y + 5, x + 171, y + 111, 0xFF383247);
        graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png"), x, y + 112, 0, 126, 176, 96);
        for (int slotX : new int[]{43,115}) {
            graphics.fill(x + slotX, y + 51, x + slotX + 18, y + 69, 0xFF11181C);
            graphics.fill(x + slotX + 1, y + 52, x + slotX + 17, y + 68, 0xFF69777D);
        }
        graphics.fill(x + 14, y + 31, x + 162, y + 39, 0xFF15131C);
        int fill = menu.getEnergyCapacity() <= 0 ? 0 : (int)(146L * menu.getEnergyStored() / menu.getEnergyCapacity());
        if (fill > 0) graphics.fill(x + 15, y + 32, x + 15 + Math.min(146, fill), y + 38, 0xFFB85CFF);
        graphics.drawString(font, ">", x + 84, y + 56, 0xFFD9C8ED, false);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 8, 0xFFFFFF, false);
        // Smaller status labels keep large FE values inside the machine panel.
        graphics.pose().pushPose(); graphics.pose().scale(0.85F, 0.85F, 1);
        label(graphics, Component.translatable("gui.mortal_expansion.enchantment_generator.fe", menu.getEnergyStored(), menu.getEnergyCapacity()), 14, 20);
        label(graphics, Component.translatable("gui.mortal_expansion.enchantment_generator.input"), 35, 42);
        label(graphics, Component.translatable("gui.mortal_expansion.enchantment_generator.result"), 112, 42);
        label(graphics, Component.translatable("gui.mortal_expansion.enchantment_generator.remaining", menu.getRemainingEnergyToGenerate()), 14, 78);
        label(graphics, Component.translatable("gui.mortal_expansion.enchantment_generator.output", menu.getGenerationPerTick()), 14, 89);
        label(graphics, Component.translatable(menu.hasFinishedItem() ? "gui.mortal_expansion.enchantment_generator.ready" : "gui.mortal_expansion.enchantment_generator.help"), 14, 100);
        graphics.pose().popPose();
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
    private void label(GuiGraphics graphics, Component text, int x, int y) {
        graphics.drawString(font, text, Math.round(x / 0.85F), Math.round(y / 0.85F), 0xFFFFFF, false);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta); renderTooltip(graphics, mouseX, mouseY);
    }
}
