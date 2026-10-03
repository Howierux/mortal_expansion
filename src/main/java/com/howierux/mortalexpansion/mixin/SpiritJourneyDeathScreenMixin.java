package com.howierux.mortalexpansion.mixin;

import com.howierux.mortalexpansion.client.SpiritJourneyDeathScreen;
import com.howierux.mortalexpansion.network.SpiritJourneyPayloads;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class SpiritJourneyDeathScreenMixin extends Screen implements SpiritJourneyDeathScreen {
    @Shadow private int delayTicker;
    @Unique private Button mortalExpansion$journey;
    @Unique private boolean mortalExpansion$chosen;
    protected SpiritJourneyDeathScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void mortalExpansion$queryJourney(CallbackInfo ci) {
        mortalExpansion$journey = null;
        mortalExpansion$chosen = false;
        PacketDistributor.sendToServer(new SpiritJourneyPayloads.Request(false));
    }

    @Override public void mortalExpansion$setJourneyAvailable(boolean available) {
        if (!available) {
            if (mortalExpansion$journey != null) {
                removeWidget(mortalExpansion$journey);
                mortalExpansion$journey = null;
            }
            return;
        }
        if (mortalExpansion$journey != null) return;
        mortalExpansion$journey = addRenderableWidget(Button.builder(
                Component.translatable("button.mortal_expansion.spirit_journey"), button -> {
                    mortalExpansion$chosen = true;
                    button.active = false;
                    PacketDistributor.sendToServer(new SpiritJourneyPayloads.Request(true));
                }).bounds(width / 2 - 100, height / 4 + 120, 200, 20).build());
        mortalExpansion$journey.active = delayTicker >= 20;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mortalExpansion$delayButton(CallbackInfo ci) {
        if (mortalExpansion$journey != null) mortalExpansion$journey.active = delayTicker >= 20 && !mortalExpansion$chosen;
    }
}
