package com.howierux.mortalexpansion.client;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.*;
import java.util.*;
/** Preserve each renderer's textures, shader, vertex format and geometry; add alpha blending. */
public final class FadingRenderTypes extends RenderType {
    private record Key(RenderType source,int opacity){}
    private static final Map<Key,RenderType> CACHE=new HashMap<>();
    private FadingRenderTypes(RenderType source,int opacity){this(source,opacity,new float[4]);}
    private FadingRenderTypes(RenderType source,int opacity,float[] saved){
        super("mortal_expansion_fading",source.format(),source.mode(),source.bufferSize(),source.affectsCrumbling(),source.mode()==VertexFormat.Mode.QUADS,
            ()->{source.setupRenderState();System.arraycopy(RenderSystem.getShaderColor(),0,saved,0,4);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShaderColor(saved[0],saved[1],saved[2],saved[3]*opacity/100F);},
            ()->{source.clearRenderState();RenderSystem.disableBlend();RenderSystem.setShaderColor(saved[0],saved[1],saved[2],saved[3]);});
    }
    public static MultiBufferSource buffers(MultiBufferSource source,int opacity){
        if(opacity>=100)return source;
        return type->source.getBuffer(CACHE.computeIfAbsent(new Key(type,opacity),key->new FadingRenderTypes(key.source,key.opacity)));
    }
}
