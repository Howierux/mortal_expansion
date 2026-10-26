package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.network.SpectralTracksPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** Full-bright green square footsteps drawn on the main world target. */
public final class SpectralTracksClient {
    private static BlockPos target;private static Object world;private static long expires;
    public static void receive(SpectralTracksPayload p){var mc=Minecraft.getInstance();target=p.visible()?p.target():null;world=mc.level;expires=mc.level==null?0:mc.level.getGameTime()+60;}
    public static void render(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext event){

        var mc=Minecraft.getInstance();if(mc.level==null || mc.player==null || mc.level!=world || !mc.player.isAlive() || mc.level.getGameTime()>=expires){target=null;return;}if(target==null)return;
        Vec3 direction=new Vec3(target.getX()+0.5-mc.player.getX(),0,target.getZ()+0.5-mc.player.getZ());if(direction.lengthSqr()<1)return;direction=direction.normalize();
        Vec3 side=new Vec3(-direction.z,0,direction.x);var camera=event.camera().getPosition();
        var poses=event.matrixStack();var buffers=mc.renderBuffers().bufferSource();var type=net.minecraft.client.renderer.RenderType.debugQuads();var vertices=buffers.getBuffer(type);
        for(int step=1;step<=10;step++){
            Vec3 at=mc.player.position().add(direction.scale(step)).add(side.scale(step%2==0?0.15:-0.15));
            BlockPos pos=BlockPos.containing(at);if(!mc.level.hasChunkAt(pos))continue;
            var hit=mc.level.clip(new net.minecraft.world.level.ClipContext(
                    at.add(0,1,0),at.add(0,-4,0),net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,mc.player));
            if(hit.getType()!=net.minecraft.world.phys.HitResult.Type.BLOCK || hit.getDirection()!=net.minecraft.core.Direction.UP
                    || !mc.level.getFluidState(hit.getBlockPos()).isEmpty())continue;
            Vec3 base=hit.getLocation().add(0,0.012,0).subtract(camera);
            double fade=Math.min(1.0,(11-step)/4.0);
            square(vertices,poses.last(),base,direction,side,0.24,40,255,65,(int)(65*fade));
            square(vertices,poses.last(),base.add(0,0.003,0),direction,side,0.16,105,255,35,(int)(240*fade));
        }
        // Lightning routes into the weather target; flush this main-target draw while its world pose is active.
        buffers.endBatch(type);
    }
    private static void square(com.mojang.blaze3d.vertex.VertexConsumer v,com.mojang.blaze3d.vertex.PoseStack.Pose pose,
            Vec3 base,Vec3 d,Vec3 s,double half,int red,int green,int blue,int alpha){
        for(var p:new Vec3[]{base.add(d.scale(-half)).add(s.scale(-half)),base.add(d.scale(-half)).add(s.scale(half)),
                base.add(d.scale(half)).add(s.scale(half)),base.add(d.scale(half)).add(s.scale(-half))})
            v.addVertex(pose,(float)p.x,(float)p.y,(float)p.z).setColor(red,green,blue,alpha);
    }
}
