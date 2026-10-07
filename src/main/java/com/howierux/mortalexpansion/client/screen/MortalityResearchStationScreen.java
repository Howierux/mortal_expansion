package com.howierux.mortalexpansion.client.screen;
import com.howierux.mortalexpansion.menu.MortalityResearchStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class MortalityResearchStationScreen extends AbstractContainerScreen<MortalityResearchStationMenu> {
    public MortalityResearchStationScreen(MortalityResearchStationMenu menu,Inventory inventory,Component title){super(menu,inventory,title);imageWidth=176;imageHeight=208;inventoryLabelY=114;}
    @Override protected void init(){super.init();addRenderableWidget(Button.builder(Component.translatable("gui.mortal_expansion.research.study"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0)).bounds(leftPos+69,topPos+45,96,20).build());}
    @Override protected void renderBg(GuiGraphics g,float delta,int mouseX,int mouseY){g.fill(leftPos,topPos,leftPos+176,topPos+112,0xFF24213B);g.blit(ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png"),leftPos,topPos+112,0,126,176,96);g.fill(leftPos+29,topPos+44,leftPos+47,topPos+62,0xFFAAA3BA);}
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){g.drawString(font,title,8,8,0xFFFFFF,false);g.drawString(font,Component.translatable("gui.mortal_expansion.research.bonus",menu.bonus()),8,25,0xCFC4EF,false);g.drawString(font,Component.translatable("gui.mortal_expansion.research.rates"),8,78,0xCFC4EF,false);g.drawString(font,Component.translatable("gui.mortal_expansion.research.exclusive",Component.translatable("gui.mortal_expansion.research.type."+menu.ectoType())),8,92,0xCFC4EF,false);g.drawString(font,playerInventoryTitle,8,inventoryLabelY,0x404040,false);}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){super.render(g,mouseX,mouseY,delta);renderTooltip(g,mouseX,mouseY);}
}
