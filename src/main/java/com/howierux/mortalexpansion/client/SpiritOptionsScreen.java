package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.network.SpiritInteractionPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class SpiritOptionsScreen extends Screen {
    private final SpiritInteractionPayloads.Open options;
    public SpiritOptionsScreen(SpiritInteractionPayloads.Open options){super(options.title());this.options=options;}
    public static void handle(SpiritInteractionPayloads.Open options,IPayloadContext context){
        context.enqueueWork(()->Minecraft.getInstance().setScreen(new SpiritOptionsScreen(options)));
    }
    private void choose(int action){PacketDistributor.sendToServer(new SpiritInteractionPayloads.Choice(options.token(),action));minecraft.setScreen(null);}
    private void button(String label,int y,int action){addRenderableWidget(Button.builder(Component.translatable("screen.mortal_expansion.spirit."+label),b->choose(action)).bounds(width/2-100,y,200,20).build());}
    @Override protected void init(){
        int y=Math.max(height/2+25,65+font.split(options.body(),Math.min(300,width-32)).size()*font.lineHeight);
        y=Math.min(y,height-82);
        if(options.mode()==0){button("trade",y,0);button("business",y+24,1);button("leave",y+48,5);}
        else if(options.mode()==1){button("accept",y,2);button("decline",y+24,3);button("back",y+48,4);}
        else button("back",y+24,4);
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick){
        renderBackground(graphics,mouseX,mouseY,partialTick);super.render(graphics,mouseX,mouseY,partialTick);graphics.drawCenteredString(font,title,width/2,25,0xFFFFFF);
        graphics.drawWordWrap(font,options.body(),Math.max(16,width/2-150),55,Math.min(300,width-32),0xD6D6D6);
    }
    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){choose(5);}
}
