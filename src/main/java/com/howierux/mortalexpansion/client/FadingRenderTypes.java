package com.howierux.mortalexpansion.client;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.howierux.mortalexpansion.mixin.*;
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
        return type->{
            var translucent=translucentMaterial(type);
            if(translucent!=null)return new AlphaConsumer(source.getBuffer(translucent),opacity/100F);
            return source.getBuffer(CACHE.computeIfAbsent(new Key(type,opacity),key->new FadingRenderTypes(key.source,key.opacity)));
        };
    }
    /** Keep Iris on its recognized translucent entity path, with alpha in vertex data. */
    private static RenderType translucentMaterial(RenderType source){
        if(source.format()!=DefaultVertexFormat.NEW_ENTITY || !(source instanceof FadingCompositeTypeAccessor composite))return null;
        var state=(FadingCompositeStateAccessor)(Object)composite.mortalExpansion$state();
        var texture=((FadingTextureStateAccessor)(Object)state.mortalExpansion$texture()).mortalExpansion$textureLocation();
        if(texture.isEmpty())return null;
        var location=texture.get();
        // Preserve special shaders (eyes, glint, energy, outlines) in the fallback adapter.
        if(source==RenderType.entitySolid(location) || source==RenderType.entityCutout(location)
                || source==RenderType.entityCutoutNoCull(location) || source==RenderType.entityTranslucent(location)
                || source==RenderType.entityTranslucentCull(location))return RenderType.entityTranslucentCull(location);
        return null;
    }
    private record AlphaConsumer(VertexConsumer delegate,float alpha) implements VertexConsumer {
        @Override public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,Math.round(a*alpha));return this;}
        @Override public VertexConsumer setUv(float u,float v){delegate.setUv(u,v);return this;}
        @Override public VertexConsumer setUv1(int u,int v){delegate.setUv1(u,v);return this;}
        @Override public VertexConsumer setUv2(int u,int v){delegate.setUv2(u,v);return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}
    }
}

