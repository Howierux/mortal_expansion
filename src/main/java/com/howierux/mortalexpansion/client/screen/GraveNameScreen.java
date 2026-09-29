package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.network.GraveNamePayloads;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class GraveNameScreen extends Screen {
    private static final int MAX_NAME_LENGTH = 32;

    private final BlockPos gravePos;
    private EditBox nameBox;

    public GraveNameScreen(BlockPos gravePos) {
        super(Component.translatable("gui.mortal_expansion.grave_name.title"));
        this.gravePos = gravePos;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        nameBox = new EditBox(
                font,
                centerX - 100,
                centerY - 20,
                200,
                20,
                Component.translatable("gui.mortal_expansion.grave_name.field")
        );
        nameBox.setMaxLength(MAX_NAME_LENGTH);
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);

        addRenderableWidget(Button.builder(
                Component.translatable("gui.mortal_expansion.grave_name.save"),
                button -> saveAndClose()
        ).bounds(centerX - 100, centerY + 12, 96, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.mortal_expansion.grave_name.skip"),
                button -> onClose()
        ).bounds(centerX + 4, centerY + 12, 96, 20).build());
    }

    private void saveAndClose() {
        String value = nameBox.getValue().trim();
        if (!value.isEmpty()) {
            PacketDistributor.sendToServer(
                    new GraveNamePayloads.SetGraveName(gravePos, value)
            );
        }
        onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(
                font,
                title,
                width / 2,
                height / 2 - 55,
                0xFFFFFF
        );

        graphics.drawCenteredString(
                font,
                Component.translatable("gui.mortal_expansion.grave_name.prompt"),
                width / 2,
                height / 2 - 40,
                0xC0C0C0
        );
    }
}
