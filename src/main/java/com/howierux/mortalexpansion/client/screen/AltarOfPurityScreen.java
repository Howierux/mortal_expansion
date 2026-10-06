package com.howierux.mortalexpansion.client.screen;
import com.howierux.mortalexpansion.menu.AltarOfPurityMenu;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class AltarOfPurityScreen extends AbstractContainerScreen<AltarOfPurityMenu> {
    private float visualBurn;
    public AltarOfPurityScreen(AltarOfPurityMenu menu,Inventory inventory,Component title){super(menu,inventory,title);imageWidth=176;imageHeight=208;inventoryLabelY=114;visualBurn=menu.getBurnTicks();}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){
        int x=leftPos,y=topPos;g.fill(x,y,x+176,y+112,0xFF253731);g.fill(x+5,y+5,x+171,y+111,0xFF43594C);
        g.blit(ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png"),x,y+112,0,126,176,96);
        for(int sy:new int[]{45,79}){g.fill(x+29,y+sy,x+47,y+sy+18,0xFF15251B);g.fill(x+30,y+sy+1,x+46,y+sy+17,0xFF81937D);}
        g.fill(x+97,y+23,x+165,y+99,0xFF1C2922);
        visualBurn+=(menu.getBurnTicks()-visualBurn)*.12F;int fill=menu.getBurnTicks()==0?0:Math.max(1,(int)(60*visualBurn/AltarOfPurityBlockEntity.MAX_BURN_TICKS));
        g.enableScissor(x+103,y+85-fill,x+159,y+85);g.blitSprite(ResourceLocation.withDefaultNamespace("container/furnace/lit_progress"),x+107,y+25,48,60);g.disableScissor();
        log(g,x+131,y+90,16);log(g,x+131,y+90,-16);
    }
    private void log(GuiGraphics g,int x,int y,float angle){g.pose().pushPose();g.pose().translate(x,y,0);g.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angle));g.fill(-23,-4,23,4,0xFF6E4930);g.fill(-20,-2,20,0,0xFFA47546);g.pose().popPose();}
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){g.drawString(font,title,8,8,0xFFFFFF,false);g.drawString(font,Component.translatable("gui.mortal_expansion.altar.offering"),14,30,0xFFFFFF,false);g.drawString(font,Component.translatable("gui.mortal_expansion.altar.return"),14,66,0xFFFFFF,false);g.drawString(font,playerInventoryTitle,8,inventoryLabelY,0x404040,false);}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){super.render(g,mouseX,mouseY,partial);renderTooltip(g,mouseX,mouseY);
        if(isHovering(97,23,68,76,mouseX,mouseY)){int seconds=(menu.getBurnTicks()+19)/20;g.renderTooltip(font,Component.translatable("gui.mortal_expansion.altar.remaining",String.format(java.util.Locale.ROOT,"%d:%02d",seconds/60,seconds%60)),mouseX,mouseY);}
    }
}
