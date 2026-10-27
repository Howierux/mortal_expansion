package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.network.GravePortPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class GravePortScreen extends Screen {
    private final GravePortPayloads.Open options;
    private int page;
    public GravePortScreen(GravePortPayloads.Open options){super(Component.translatable("screen.mortal_expansion.grave_port.title"));this.options=options;}
    private void choose(int index){net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new GravePortPayloads.Choose(options.token(),index));minecraft.setScreen(null);}
    private int rows(){return Math.max(1,(height-132)/24);}
    @Override protected void init(){
        int rows=rows();page=Math.clamp(page,0,Math.max(0,(options.destinations().size()-1)/rows));
        for(int i=page*rows;i<Math.min(options.destinations().size(),(page+1)*rows);i++){
            int index=i;var destination=options.destinations().get(i);var pos=destination.pos();
            String dimension=switch(destination.dimension()){
                case "minecraft:overworld"->"Overworld";case "minecraft:the_nether"->"Nether";case "minecraft:the_end"->"The End";
                default->net.minecraft.resources.ResourceLocation.parse(destination.dimension()).getPath().replace('_',' ');
            };
            var label=Component.literal(dimension+"  "+pos.getX()+", "+pos.getY()+", "+pos.getZ());
            addRenderableWidget(Button.builder(label,b->choose(index)).bounds(width/2-150,72+(i-page*rows)*24,300,20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("<"),b->{page--;rebuildWidgets();}).bounds(width/2-150,height-48,30,20).build()).active=page>0;
        addRenderableWidget(Button.builder(Component.literal(">"),b->{page++;rebuildWidgets();}).bounds(width/2+120,height-48,30,20).build()).active=(page+1)*rows<options.destinations().size();
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"),b->choose(-1)).bounds(width/2-70,height-48,140,20).build());
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick){
        renderBackground(graphics,mouseX,mouseY,partialTick);super.render(graphics,mouseX,mouseY,partialTick);
        graphics.drawCenteredString(font,title,width/2,20,0xFFFFFF);
        graphics.drawWordWrap(font,Component.translatable("screen.mortal_expansion.grave_port.cost"),width/2-Math.min(300,width-32)/2,40,Math.min(300,width-32),0xD6D6D6);
        if(options.destinations().isEmpty())graphics.drawCenteredString(font,Component.translatable("screen.mortal_expansion.grave_port.empty"),width/2,74,0xD6D6D6);
    }
    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){choose(-1);}
}
