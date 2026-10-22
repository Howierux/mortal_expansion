package com.howierux.mortalexpansion.client.screen;
import com.howierux.mortalexpansion.menu.FancifierMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class FancifierScreen extends AbstractContainerScreen<FancifierMenu> {
    private Button apply;
    private final java.util.List<Button> choices = new java.util.ArrayList<>();
    public FancifierScreen(FancifierMenu menu,Inventory inventory,Component title) { super(menu,inventory,title); imageHeight=188;inventoryLabelY=94; }
    @Override protected void init() {
        super.init(); choices.clear();
        for(int i=0;i<5;i++) { final int button=i;
            choices.add(addRenderableWidget(Button.builder(Component.literal(((i+1)*20)+"%"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,button)).bounds(leftPos+8+i*32,topPos+62,31,18).build()));
        }
        choices.get(4).setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.translatable("gui.mortal_expansion.fancifier.restore")));
        apply=addRenderableWidget(Button.builder(Component.translatable("gui.mortal_expansion.fancifier.apply"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,5)).bounds(leftPos+56,topPos+34,64,20).build());
    }
    @Override protected void containerTick() { super.containerTick(); apply.active=menu.canApply(minecraft.player);for(int i=0;i<choices.size();i++)choices.get(i).active=menu.opacity()!=(i+1)*20; }
    @Override protected void renderBg(GuiGraphics g,float delta,int mouseX,int mouseY) {
        g.fill(leftPos,topPos,leftPos+176,topPos+188,0xFF283734);
        g.fill(leftPos+4,topPos+4,leftPos+172,topPos+184,0xFF586560);
        for(var slot:menu.slots) { int x=leftPos+slot.x,y=topPos+slot.y;g.fill(x-1,y-1,x+17,y+17,0xFF202B29);g.fill(x,y,x+16,y+16,0xFF909C96); }
    }
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY) {
        g.drawString(font,title,8,8,0xFFFFFF,false);
        g.drawString(font,Component.translatable("gui.mortal_expansion.fancifier.opacity",menu.opacity()),8,22,0xD0F5DD,false);
        g.drawString(font,Component.translatable("gui.mortal_expansion.fancifier.decorative"),8,84,0xD0F5DD,false);
        g.drawString(font,playerInventoryTitle,8,inventoryLabelY,0xFFFFFF,false);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta) { super.render(g,mouseX,mouseY,delta);renderTooltip(g,mouseX,mouseY); }
}
