package com.howierux.mortalexpansion.client;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.util.Arrays;

/** Development-only: copy to client source, run with -Dmortal_expansion.overlapCheck=true, then remove. */
@EventBusSubscriber(modid="mortal_expansion",value=Dist.CLIENT)
public final class FadingOverlapCheck {
    private static int ticks;
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        if(!Boolean.getBoolean("mortal_expansion.overlapCheck") || ++ticks!=120)return;
        for(float shift:new float[]{0,3.14F,-41.25F,128.5F}){
            verify(shift,true);verify(shift,false);
        }
        nativeModels();
        // Exercise the real BufferSource -> sortQuads injection and GPU upload with a faded material.
        var buffers=Minecraft.getInstance().renderBuffers().bufferSource();
        var layer=FadingRenderTypes.material(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
        quad(buffers.getBuffer(layer),0,1,0,1,0);quad(buffers.getBuffer(layer),0,1,0.5F,1.5F,0);
        buffers.endBatch(layer);
        org.slf4j.LoggerFactory.getLogger("FadingOverlapCheck").info("FADING_OVERLAP_CHECK_OK: moving-camera order, native single/double chest and dust models, bounded separation, untouched nonoverlap, real buffer upload");
        Minecraft.getInstance().stop();
    }
    private static void nativeModels(){
        for(var layer:java.util.List.of(
                net.minecraft.client.renderer.blockentity.ChestRenderer.createSingleBodyLayer(),
                net.minecraft.client.renderer.blockentity.ChestRenderer.createDoubleBodyLeftLayer(),
                net.minecraft.client.renderer.blockentity.ChestRenderer.createDoubleBodyRightLayer())){
            try(var allocation=new ByteBufferBuilder(8192)){
                var builder=new BufferBuilder(allocation,VertexFormat.Mode.QUADS,DefaultVertexFormat.NEW_ENTITY);
                var root=layer.bakeRoot();var poses=new PoseStack();
                root.getChild("lid").render(poses,builder,15728880,0);
                root.getChild("bottom").render(poses,builder,15728880,0);
                try(var mesh=builder.buildOrThrow()){check(FadingOverlap.prepare(mesh,VertexSorting.DISTANCE_TO_ORIGIN)!=VertexSorting.DISTANCE_TO_ORIGIN,"Native chest overlap missed");}
            }
        }
        try(var allocation=new ByteBufferBuilder(8192)){
            var builder=new BufferBuilder(allocation,VertexFormat.Mode.QUADS,DefaultVertexFormat.NEW_ENTITY);
            var state=net.minecraft.world.level.block.Blocks.REDSTONE_WIRE.defaultBlockState()
                .setValue(net.minecraft.world.level.block.RedStoneWireBlock.NORTH,net.minecraft.world.level.block.state.properties.RedstoneSide.SIDE)
                .setValue(net.minecraft.world.level.block.RedStoneWireBlock.SOUTH,net.minecraft.world.level.block.state.properties.RedstoneSide.SIDE)
                .setValue(net.minecraft.world.level.block.RedStoneWireBlock.EAST,net.minecraft.world.level.block.state.properties.RedstoneSide.SIDE)
                .setValue(net.minecraft.world.level.block.RedStoneWireBlock.WEST,net.minecraft.world.level.block.state.properties.RedstoneSide.SIDE);
            var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            for(var quad:model.getQuads(state,null,net.minecraft.util.RandomSource.create(0),net.neoforged.neoforge.client.model.data.ModelData.EMPTY,RenderType.cutout()))
                builder.putBulkData(new PoseStack().last(),quad,1,1,1,0.2F,15728880,0);
            try(var mesh=builder.buildOrThrow()){check(FadingOverlap.prepare(mesh,VertexSorting.DISTANCE_TO_ORIGIN)!=VertexSorting.DISTANCE_TO_ORIGIN,"Native dust overlap missed");}
        }
    }
    private static void verify(float shift,boolean overlap){
        try(var allocation=new ByteBufferBuilder(4096)){
            var builder=new BufferBuilder(allocation,VertexFormat.Mode.QUADS,DefaultVertexFormat.NEW_ENTITY);
            quad(builder,shift,shift+1,0,1,0);
            quad(builder,shift,shift+1,overlap?0.5F:1,overlap?1.5F:2,0);
            quad(builder,shift,shift+1,0,1,2);
            try(var mesh=builder.buildOrThrow()){
                // Force a camera-dependent swap that must be overridden only for the overlapping pair.
                VertexSorting descending=centers->new int[]{2,1,0};
                var sorting=FadingOverlap.prepare(mesh,descending);
                var centers=new org.joml.Vector3f[]{new org.joml.Vector3f(),new org.joml.Vector3f(),new org.joml.Vector3f()};
                int[] result=sorting.sort(centers);
                check(Arrays.equals(result,overlap?new int[]{2,0,1}:new int[]{2,1,0}),"Unstable face order");
                int stride=DefaultVertexFormat.NEW_ENTITY.getVertexSize();
                float separation=mesh.vertexBuffer().getFloat(4*stride+8);
                check(overlap?separation>0 && separation<=1F/256F:separation==0,"Incorrect overlap separation");
                check(mesh.vertexBuffer().getFloat(8*stride+8)==2,"Unrelated face moved");
            }
        }
    }
    private static void quad(VertexConsumer consumer,float minX,float maxX,float minY,float maxY,float z){
        vertex(consumer,minX,minY,z,0,0);vertex(consumer,maxX,minY,z,1,0);
        vertex(consumer,maxX,maxY,z,1,1);vertex(consumer,minX,maxY,z,0,1);
    }
    private static void vertex(VertexConsumer consumer,float x,float y,float z,float u,float v){
        consumer.addVertex(x,y,z).setColor(255,255,255,51).setUv(u,v).setUv1(0,0).setUv2(240,240).setNormal(0,0,1);
    }
}
