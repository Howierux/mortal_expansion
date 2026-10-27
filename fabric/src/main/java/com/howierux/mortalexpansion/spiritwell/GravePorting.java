package com.howierux.mortalexpansion.spiritwell;

import com.howierux.mortalexpansion.blockentity.*;
import com.howierux.mortalexpansion.haunt.PlayerHauntManager;
import com.howierux.mortalexpansion.network.GravePortPayloads;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.resources.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import java.util.*;

/** Loaded-only destinations; no saved travel index or terrain generation. */
public final class GravePorting {
    private static final Map<ServerLevel,Set<BlockPos>> GRAVES=new WeakHashMap<>();
    private record Session(ResourceKey<Level> source,BlockPos well,String token,List<GravePortPayloads.Destination> destinations,long expires){}
    private static final Map<ServerPlayer,Session> SESSIONS=new WeakHashMap<>();
    private GravePorting(){}
    public static void loaded(ServerLevel level,BlockPos pos){GRAVES.computeIfAbsent(level,k->new HashSet<>()).add(pos.immutable());}
    public static void unloaded(ServerLevel level,BlockPos pos){var positions=GRAVES.get(level);if(positions!=null)positions.remove(pos);}
    private static boolean loadedAt(ServerLevel level,BlockPos pos){return level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
    public static BlockPos frame(ServerLevel level,BlockPos grave){
        var candidates=new ArrayList<BlockPos>();
        for(var pos:BlockPos.betweenClosed(grave.offset(-3,-3,-3),grave.offset(3,3,3))){
            if(pos.distSqr(grave)<=9 && loadedAt(level,pos)
                    && level.getBlockEntity(pos) instanceof MortalGolemBaseBlockEntity base && base.flesh()==64)candidates.add(pos.immutable());
        }
        return candidates.stream().min(Comparator.comparingDouble((BlockPos pos)->pos.distSqr(grave)).thenComparingLong(BlockPos::asLong)).orElse(null);
    }
    public static boolean eligible(ServerPlayer player,ServerLevel level,BlockPos pos){
        return loadedAt(level,pos) && level.getBlockEntity(pos) instanceof com.howierux.mortalexpansion.travel.GraveEntity grave
                && grave.getBlockState().getValue(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVE_NATURAL) && grave.owned(player)
                && grave.inscription().equalsIgnoreCase(player.getGameProfile().getName()) && frame(level,pos)!=null;
    }
    public static List<GravePortPayloads.Destination> destinations(ServerPlayer player){
        var result=new ArrayList<GravePortPayloads.Destination>();
        for(var level:player.getServer().getAllLevels())for(var pos:List.copyOf(GRAVES.getOrDefault(level,Set.of()))) {
            if(eligible(player,level,pos))result.add(new GravePortPayloads.Destination(level.dimension().location().toString(),pos));
        }
        result.sort(Comparator.comparing(GravePortPayloads.Destination::dimension).thenComparingLong(d->d.pos().asLong()));
        return List.copyOf(result);
    }
    private static boolean available(ServerPlayer player,ServerLevel source,BlockPos well){
        return player.isAlive() && !player.isSpectator() && player.serverLevel()==source && !player.isPassenger()
                && loadedAt(source,well) && source.getBlockState(well).is(com.howierux.mortalexpansion.catalog.CatalogBlocks.SPIRITWELL)
                && player.position().distanceToSqr(well.getCenter())<=Math.pow(player.blockInteractionRange()+1,2);
    }
    public static String open(ServerPlayer player,BlockPos well){
        if(!available(player,player.serverLevel(),well))return "";
        var destinations=destinations(player);String token=UUID.randomUUID().toString();
        SESSIONS.put(player,new Session(player.serverLevel().dimension(),well.immutable(),token,destinations,player.getServer().overworld().getGameTime()+1200));
        GravePortPayloads.send(player,token,destinations);return token;
    }
    public static Vec3 landing(ServerPlayer player,ServerLevel level,BlockPos grave){
        for(int dy=0;dy<=2;dy++)for(int radius=0;radius<=2;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
            if(Math.max(Math.abs(dx),Math.abs(dz))!=radius)continue;
            var feet=grave.offset(dx,dy,dz);if(!loadedAt(level,feet) || !loadedAt(level,feet.below()))continue;
            var at=Vec3.atBottomCenterOf(feet);var box=player.getBoundingBox().move(at.subtract(player.position()));
            if(box.minY<level.getMinBuildHeight() || box.maxY>level.getMaxBuildHeight() || !level.getWorldBorder().isWithinBounds(box))continue;
            boolean safe=true;
            for(var p:BlockPos.betweenClosed(BlockPos.containing(box.minX,box.minY,box.minZ),BlockPos.containing(box.maxX,box.maxY,box.maxZ))){
                if(!loadedAt(level,p) || !level.getBlockState(p).getFluidState().isEmpty()
                        || level.getBlockState(p).is(net.minecraft.world.level.block.Blocks.FIRE)
                        || level.getBlockState(p).is(net.minecraft.world.level.block.Blocks.SOUL_FIRE)) {safe=false;break;}
            }
            if(safe && level.getBlockState(feet.below()).isFaceSturdy(level,feet.below(),net.minecraft.core.Direction.UP)
                    && level.noCollision(player,box) && level.getEntities(player,box,e->e.isAlive() && e.isPickable()).isEmpty())return at;
        }
        return null;
    }
    public static boolean choose(ServerPlayer player,String token,int index){
        var session=SESSIONS.get(player);if(session==null || !session.token().equals(token))return false;
        SESSIONS.remove(player);if(index==-1)return true;
        var source=player.getServer().getLevel(session.source());
        if(source==null || session.expires()<player.getServer().overworld().getGameTime() || !available(player,source,session.well())
                || index<0 || index>=session.destinations().size())return fail(player);
        var destination=session.destinations().get(index);var level=player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION,ResourceLocation.parse(destination.dimension())));
        if(level==null || !eligible(player,level,destination.pos()))return fail(player);
        var frame=frame(level,destination.pos());var at=landing(player,level,destination.pos());if(frame==null || at==null)return fail(player);
        player.teleportTo(level,at.x,at.y,at.z,player.getYRot(),player.getXRot());
        if(player.serverLevel()!=level || player.position().distanceToSqr(at)>0.01)return fail(player);
        level.removeBlock(frame,false); // Consume the whole fleshed frame, without bone/flesh drops.
        PlayerHauntManager.addPoints(player,1);player.setDeltaMovement(Vec3.ZERO);player.fallDistance=0;
        player.displayClientMessage(Component.translatable("message.mortal_expansion.grave_port.success"),true);return true;
    }
    private static boolean fail(ServerPlayer player){player.displayClientMessage(Component.translatable("message.mortal_expansion.grave_port.failed"),true);return false;}
}
