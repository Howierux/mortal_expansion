package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.network.SpiritJourneyPayloads;
import com.howierux.mortalexpansion.client.SpiritJourneyClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SpiritJourneyScreen extends Screen {
    private Button recover;
    private Component destination = Component.translatable("screen.mortal_expansion.journey.searching");
    public SpiritJourneyScreen() { super(Component.translatable("screen.mortal_expansion.journey.title")); }
    @Override protected void init() {
        recover = addRenderableWidget(Button.builder(Component.translatable("screen.mortal_expansion.journey.recover"), button -> {
            button.active = false;
            PacketDistributor.sendToServer(new SpiritJourneyPayloads.WellRecovery(true));
            onClose();
        }).bounds(width / 2 - 110, height / 2 + 12, 220, 20).build());
        recover.active = false;
        addRenderableWidget(Button.builder(Component.translatable("screen.mortal_expansion.journey.continue"), button -> onClose())
                .bounds(width / 2 - 110, height / 2 + 38, 220, 20).build());
        PacketDistributor.sendToServer(new SpiritJourneyPayloads.WellRecovery(false));
    }
    public void offer(SpiritJourneyPayloads.WellOffer offer) {
        recover.active = offer.available();
        destination = offer.available() ? Component.translatable("screen.mortal_expansion.journey.destination", offer.pos().getX(), offer.pos().getY(), offer.pos().getZ())
                : Component.translatable("screen.mortal_expansion.journey.none");
    }
    @Override public void tick() { if (!SpiritJourneyClient.isActive()) onClose(); }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(width / 2 - 175, height / 2 - 80, width / 2 + 175, height / 2 + 70, 0xE0101824);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 65, 0xFFFFFF);
        graphics.drawCenteredString(font, destination, width / 2, height / 2 - 43, 0xB8FFFF);
        graphics.drawCenteredString(font, Component.translatable("screen.mortal_expansion.journey.cost", 10, 10), width / 2, height / 2 - 23, 0xFFBB88);
        graphics.drawCenteredString(font, Component.translatable("screen.mortal_expansion.journey.body"), width / 2, height / 2 - 8, 0xCCCCCC);
    }
}
