package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.network.SpectralTracksPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** Procedural translucent bootprints; no texture downloads or extra entities. */
@EventBusSubscriber(modid=MortalExpansion.MOD_ID,value=Dist.CLIENT)
public final class SpectralTracksClient {
    private static BlockPos target;private static Object world;private static long expires;
    public static void receive(SpectralTracksPayload p){var mc=Minecraft.getInstance();target=p.visible()?p.target():null;world=mc.level;expires=mc.level==null?0:mc.level.getGameTime()+25;}
    @SubscribeEvent public static void render(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_ENTITIES)return;
        var mc=Minecraft.getInstance();if(mc.level==null || mc.player==null || mc.level!=world || !mc.player.isAlive() || mc.level.getGameTime()>=expires){target=null;return;}if(target==null)return;
        Vec3 direction=new Vec3(target.getX()+0.5-mc.player.getX(),0,target.getZ()+0.5-mc.player.getZ());if(direction.lengthSqr()<1)return;direction=direction.normalize();
        Vec3 side=new Vec3(-direction.z,0,direction.x);var camera=event.getCamera().getPosition();
        var poses=event.getPoseStack();var vertices=mc.renderBuffers().bufferSource().getBuffer(net.minecraft.client.renderer.RenderType.lightning());
        for(int step=1;step<=12;step++){
            Vec3 at=mc.player.position().add(direction.scale(step)).add(side.scale(step%2==0?0.15:-0.15));
            BlockPos pos=BlockPos.containing(at);if(!mc.level.hasChunkAt(pos))continue;
            double surface=Double.NaN;
            for(int dy=2;dy>=-2;dy--){var below=pos.offset(0,dy,0);var shape=mc.level.getBlockState(below).getCollisionShape(mc.level,below);
                if(shape.isEmpty() || !mc.level.getFluidState(below).isEmpty())continue;
                double y=below.getY()+shape.max(net.minecraft.core.Direction.Axis.Y);
                if(!mc.level.getBlockState(BlockPos.containing(at.x,y+0.1,at.z)).getCollisionShape(mc.level,BlockPos.containing(at.x,y+0.1,at.z)).isEmpty())continue;
                surface=y+0.015;break;}
            if(Double.isNaN(surface))continue;
            Vec3 base=new Vec3(at.x,surface,at.z).subtract(camera);
            quad(vertices,poses.last(),base,direction,side,-0.18,-0.03,0.09);
            quad(vertices,poses.last(),base,direction,side,0.01,0.22,0.12);
        }
    }
    private static void quad(com.mojang.blaze3d.vertex.VertexConsumer v,com.mojang.blaze3d.vertex.PoseStack.Pose pose,Vec3 base,Vec3 d,Vec3 s,double rear,double front,double width){
        for(var p:new Vec3[]{base.add(d.scale(rear)).add(s.scale(-width)),base.add(d.scale(front)).add(s.scale(-width)),base.add(d.scale(front)).add(s.scale(width)),base.add(d.scale(rear)).add(s.scale(width))})
            v.addVertex(pose,(float)p.x,(float)p.y,(float)p.z).setColor(135,235,225,170);
    }
}
