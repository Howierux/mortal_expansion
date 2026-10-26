package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.haunt.*;

import com.howierux.mortalexpansion.catalog.CatalogItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Client-only presentation; readings arrive through the stack's synchronized transient component. */
public final class EmfReaderClient {
    private EmfReaderClient() {}
    public static float modelLevel(ItemStack stack, net.minecraft.world.entity.LivingEntity holder) {
        var player = holder instanceof net.minecraft.world.entity.player.Player p ? p : Minecraft.getInstance().player;
        if (player == null) return 0;
        int value = EmfReaderItem.value(stack, player);
        return value < 0 ? 0 : EmfScale.modelLevel(value);
    }
    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        var mc = Minecraft.getInstance(); var player = mc.player;
        if (player == null || mc.options.hideGui || player.isSpectator()) return;
        ItemStack stack;
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof EmfReaderItem) stack = player.getUseItem();
        else if (player.getMainHandItem().getItem() instanceof EmfReaderItem) stack = player.getMainHandItem();
        else if (player.getOffhandItem().getItem() instanceof EmfReaderItem) stack = player.getOffhandItem();
        else return;
        if (!stack.is(CatalogItems.PRECISE_EMF_READER)) return;
        boolean personal = EmfReaderItem.personal(stack, player);
        int value = EmfReaderItem.value(stack, player), lit = value < 0 ? 0 : EmfScale.litBulbs(value);
        int x = graphics.guiWidth() / 2 - 66, y = graphics.guiHeight() - 106;
        graphics.fill(x - 4, y - 3, x + 136, y + 36, 0xBB15191D);
        graphics.drawString(mc.font, Component.translatable(personal ? "hud.mortal_expansion.emf.personal" : "hud.mortal_expansion.emf.chunk"), x, y, 0xFFE0E0E0);
        for (int bulb = 0; bulb < EmfScale.BULBS; bulb++) {
            int bx = x + bulb * 15;
            graphics.fill(bx, y + 12, bx + 11, y + 23, 0xFF111111);
            graphics.fill(bx + 1, y + 13, bx + 10, y + 22, bulb < lit ? EmfScale.bulbColor(bulb, value) : 0xFF444444);
        }
        Component label = value < 0 ? Component.translatable("hud.mortal_expansion.emf.waiting")
                : Component.translatable("hud.mortal_expansion.emf.reading", stack.is(CatalogItems.PRECISE_EMF_READER)
                        ? com.howierux.mortalexpansion.haunt.EmfScale.decimal(EmfReaderItem.hundredths(stack, player)) : value,
                        EmfScale.tierLabel(value));
        graphics.drawString(mc.font, label, x, y + 26, lit == 0 ? 0xFFBBBBBB : EmfScale.bulbColor(lit - 1, value));
    }
}
