package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.*;
public final class FancifiedRenderer {
    public static void renderWorldModel(BlockState state,int opacity,net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();var offset=state.getOffset(level,pos);poses.translate(offset.x,offset.y,offset.z);
        var minecraft=Minecraft.getInstance();
        var model=minecraft.getBlockRenderer().getBlockModel(state);
        var random=net.minecraft.util.RandomSource.create();long seed=state.getSeed(pos);
        var consumer=new AlphaConsumer(FadingRenderTypes.buffer(buffers,FadingRenderTypes.material(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS),opacity)),opacity/100F);
        var view=new com.howierux.mortalexpansion.fancifier.FancifierFaces(level,opacity);
        random.setSeed(seed);
        {
            for(var face:net.minecraft.core.Direction.values()){
                if(!net.minecraft.world.level.block.Block.shouldRenderFace(state,view,pos,face,pos.relative(face)))continue;
                random.setSeed(seed);renderQuads(model.getQuads(state,face,random),state,level,pos,poses,consumer,light,overlay);
            }
            random.setSeed(seed);renderQuads(model.getQuads(state,null,random),state,level,pos,poses,consumer,light,overlay);
        }
        poses.popPose();
    }
    private static void renderQuads(java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads,
            BlockState state,net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos,PoseStack poses,VertexConsumer consumer,int light,int overlay) {
        for(var quad:quads){
            int color=quad.isTinted()?Minecraft.getInstance().getBlockColors().getColor(state,level,pos,quad.getTintIndex()):0xFFFFFF;
            consumer.putBulkData(poses.last(),quad,((color>>16)&255)/255F,((color>>8)&255)/255F,(color&255)/255F,1F,light,overlay);
        }
    }
    /** Keep baked vertex colors, UVs and lighting; multiply only alpha. */
    private record AlphaConsumer(VertexConsumer delegate,float alpha) implements VertexConsumer {
        @Override public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,Math.round(a*alpha));return this;}
        @Override public VertexConsumer setUv(float u,float v){delegate.setUv(u,v);return this;}
        @Override public VertexConsumer setUv1(int u,int v){delegate.setUv1(u,v);return this;}
        @Override public VertexConsumer setUv2(int u,int v){delegate.setUv2(u,v);return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}
    }
}
