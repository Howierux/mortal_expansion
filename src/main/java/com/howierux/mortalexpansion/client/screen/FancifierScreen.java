package com.howierux.mortalexpansion.client.screen;

import com.howierux.mortalexpansion.menu.FancifierMenu;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class FancifierScreen extends AbstractContainerScreen<FancifierMenu> {
    private Button apply;
    private EditBox percentage;
    private OpacitySlider slider;
    private int selected;
    private boolean updating;
    public FancifierScreen(FancifierMenu menu,Inventory inventory,Component title){
        super(menu,inventory,title);imageWidth=230;imageHeight=228;inventoryLabelX=34;inventoryLabelY=134;
        selected=menu.opacity();
    }
    /** Empty is an editing state only; it never selects or sends an invalid percentage. */
    public static boolean validText(String text){
        if(text.isEmpty())return true;
        return text.matches("[0-9]{1,3}") && FancifierRules.validOpacity(Integer.parseInt(text));
    }
    @Override protected void init(){
        super.init();
        slider=addRenderableWidget(new OpacitySlider(leftPos+8,topPos+100,146,20));
        percentage=addRenderableWidget(new EditBox(font,leftPos+166,topPos+100,42,20,Component.translatable("gui.mortal_expansion.fancifier.percentage")));
        percentage.setMaxLength(3);percentage.setFilter(FancifierScreen::validText);
        percentage.setValue(Integer.toString(selected));
        percentage.setResponder(text->{if(!updating && !text.isEmpty())select(Integer.parseInt(text));});
        percentage.setTooltip(Tooltip.create(Component.translatable("gui.mortal_expansion.fancifier.restore")));
        apply=addRenderableWidget(Button.builder(Component.translatable("gui.mortal_expansion.fancifier.apply"),button->{
            if(!percentage.getValue().isEmpty()){
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId,selected);
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId,FancifierMenu.APPLY);
            }
        }).bounds(leftPos+86,topPos+78,58,18).build());
    }
    private void select(int opacity){
        if(!FancifierRules.validOpacity(opacity))return;
        boolean changed=selected!=opacity;selected=opacity;updating=true;
        try{
            if(percentage!=null && !percentage.getValue().equals(Integer.toString(opacity)))percentage.setValue(Integer.toString(opacity));
            if(slider!=null)slider.select(opacity);
        }finally{updating=false;}
        if(changed && minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,opacity);
    }
    @Override protected void containerTick(){
        super.containerTick();
        if(!percentage.isFocused() && percentage.getValue().isEmpty())percentage.setValue(Integer.toString(selected));
        apply.active=minecraft.player!=null && !percentage.getValue().isEmpty() && menu.canApply(minecraft.player,selected);
    }
    private final class OpacitySlider extends AbstractSliderButton {
        OpacitySlider(int x,int y,int width,int height){super(x,y,width,height,Component.empty(),(selected-1)/99D);updateMessage();}
        void select(int opacity){value=(opacity-1)/99D;updateMessage();}
        @Override protected void updateMessage(){setMessage(Component.translatable("gui.mortal_expansion.fancifier.opacity",1+(int)Math.round(value*99)));}
        @Override protected void applyValue(){FancifierScreen.this.select(1+(int)Math.round(value*99));}
    }
    @Override public boolean mouseDragged(double x,double y,int button,double dragX,double dragY){
        // Container screens handle inventory drags without forwarding them to child widgets.
        if(button==0 && isDragging() && getFocused()==slider)return slider.mouseDragged(x,y,button,dragX,dragY);
        return super.mouseDragged(x,y,button,dragX,dragY);
    }
    @Override public boolean mouseReleased(double x,double y,int button){
        if(button==0 && isDragging() && getFocused()==slider){
            slider.mouseReleased(x,y,button);setDragging(false);return true;
        }
        return super.mouseReleased(x,y,button);
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers){
        // Typing the inventory key into the field must not close the screen.
        if(percentage.isFocused()){
            if(key==256 || key==258)return super.keyPressed(key,scan,modifiers);
            if(percentage.keyPressed(key,scan,modifiers) || percentage.canConsumeInput())return true;
        }
        return super.keyPressed(key,scan,modifiers);
    }
    @Override protected void renderBg(GuiGraphics g,float delta,int mouseX,int mouseY){
        g.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xFF283734);
        g.fill(leftPos+4,topPos+4,leftPos+imageWidth-4,topPos+imageHeight-4,0xFF586560);
        for(var slot:menu.slots){int x=leftPos+slot.x,y=topPos+slot.y;g.fill(x-1,y-1,x+17,y+17,0xFF202B29);g.fill(x,y,x+16,y+16,0xFF909C96);}
        // A checkerboard makes the selected transparency visible against both light and dark.
        for(int x=0;x<8;x++)for(int y=0;y<6;y++)g.fill(leftPos+83+x*8,topPos+26+y*8,leftPos+91+x*8,topPos+34+y*8,((x+y)&1)==0?0xFF26322E:0xFFA1ACA5);
        ItemStack preview=FancifierRules.convert(menu.getSlot(0).getItem(),selected,minecraft.level==null?net.minecraft.core.RegistryAccess.EMPTY:minecraft.level.registryAccess());
        if(!preview.isEmpty()){
            g.pose().pushPose();g.pose().translate(leftPos+91,topPos+26,0);g.pose().scale(3,3,1);g.renderItem(preview.copyWithCount(1),0,0);g.pose().popPose();
        }
    }
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){
        g.drawString(font,title,8,8,0xFFFFFF,false);
        g.drawCenteredString(font,Component.translatable("gui.mortal_expansion.fancifier.preview"),115,17,0xD0F5DD);
        g.drawString(font,"%",211,106,0xFFFFFF,false);
        g.drawString(font,playerInventoryTitle,inventoryLabelX,inventoryLabelY,0xFFFFFF,false);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){super.render(g,mouseX,mouseY,delta);renderTooltip(g,mouseX,mouseY);}
}
