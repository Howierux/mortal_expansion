package com.howierux.mortalexpansion.discovery;

import com.howierux.mortalexpansion.spiritwell.SpiritwellIndex;
import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class SpectralDiscovery {
 public static final net.fabricmc.fabric.api.attachment.v1.AttachmentType<java.util.List<SpectralSite>> DISCOVERED=net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(com.howierux.mortalexpansion.catalog.Catalog.id("discovered_sites"),b->b.initializer(java.util.List::of).persistent(SpectralSite.CODEC.listOf()).copyOnDeath());
 public static java.util.List<SpectralSite> knownSites(ServerPlayer p){return ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)p).getAttachedOrCreate(DISCOVERED);}
 public static void initialize(){
  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(com.howierux.mortalexpansion.network.SpectralTracksPayload.TYPE,com.howierux.mortalexpansion.network.SpectralTracksPayload.CODEC);
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(SpectralDiscovery::tick);
  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher,lookup,env)->dispatcher.register(net.minecraft.commands.Commands.literal("spectralsites").executes(c->{var p=c.getSource().getPlayerOrException();for(var site:knownSites(p))p.sendSystemMessage(net.minecraft.network.chat.Component.literal(site.name()+" — "+site.dimension()+" "+site.anchor().toShortString()));return knownSites(p).size();}).then(net.minecraft.commands.Commands.literal("trail").requires(source->source.hasPermission(2)).executes(c->startTracks(c.getSource().getPlayerOrException())?1:0))));
 }

    private SpectralDiscovery() {}
    public static void refresh(ServerLevel level) {
        var index=SpectralSiteIndex.get(level);var dim=level.dimension().location();
        for(var site:index.sites())if(site.kind().equals("spiritwell")&&level.hasChunkAt(site.anchor())&&!level.getBlockState(site.anchor()).is(com.howierux.mortalexpansion.catalog.CatalogBlocks.SPIRITWELL))index.remove(site.id());
        for(var pos:SpiritwellIndex.get(level).knownPositions()) {
            var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
            var site=SpectralSite.create("spiritwell",dim,pos,new BoundingBox(pos),level.getSeed());
            if(chunk!=null && !chunk.getBlockState(pos).is(com.howierux.mortalexpansion.catalog.CatalogBlocks.SPIRITWELL)) {index.remove(site.id());SpiritwellIndex.get(level).remove(pos);continue;}
            if(index.find(site.id())==null)index.add(site);
        }
        for(var p:level.players()) for(int x=p.chunkPosition().x-5;x<=p.chunkPosition().x+5;x++) for(int z=p.chunkPosition().z-5;z<=p.chunkPosition().z+5;z++) {
            var chunk=level.getChunkSource().getChunkNow(x,z);if(chunk==null)continue;
            for(var start:chunk.getAllStarts().values()) {
                if(!start.isValid() || !(start.getStructure() instanceof com.howierux.mortalexpansion.worldgen.structure.GraveyardCemeteryStructure))continue;
                var box=start.getBoundingBox();int cx=Math.floorDiv(box.minX()+box.maxX(),2),cz=Math.floorDiv(box.minZ()+box.maxZ(),2);
                var center=level.getChunkSource().getChunkNow(cx>>4,cz>>4);if(center==null)continue;
                int y=center.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,cx&15,cz&15)+1;
                var anchor=new BlockPos(cx,y,cz);var bounds=new BoundingBox(box.minX(),y,box.minZ(),box.maxX(),y,box.maxZ());
                var site=SpectralSite.create("cemetery",dim,anchor,bounds,level.getSeed());
                if(index.find(site.id())==null)index.add(site);
            }
        }
        // missing function: locate sites in never-generated terrain without forced chunk generation.
    }
    public static boolean known(ServerPlayer p,SpectralSite site) { return knownSites(p).stream().anyMatch(s->s.id().equals(site.id())); }
    public static SpectralSite nearestUndiscovered(ServerPlayer p) {
        return SpectralSiteIndex.get(p.serverLevel()).sites().stream().filter(s->s.dimension().equals(p.level().dimension().location()) && !known(p,s))
                .min(java.util.Comparator.comparingDouble((SpectralSite s)->s.distanceSquared(p.position())).thenComparing(SpectralSite::id)).orElse(null);
    }
    public static boolean discover(ServerPlayer p,SpectralSite site) {
        if(!site.dimension().equals(p.level().dimension().location()) || site.distanceSquared(p.position())>400 || known(p,site)) return false;
        var records=new java.util.ArrayList<>(knownSites(p));records.add(site);
        ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)p).setAttached(DISCOVERED,java.util.List.copyOf(records));
        p.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.site.discovered",site.name()),false);
        return true;
    }
    /** Future TP hook: only returns registered discoveries; travel cost and safe landing are still unimplemented. */
    public static SpectralSite registeredDestination(ServerPlayer p,String id) {
        // missing function: teleport permissions, costs and destination safety validation.
        return knownSites(p).stream().filter(s->s.id().equals(id)).findFirst().orElse(null);
    }
    public static boolean startTracks(ServerPlayer p) {
        refresh(p.serverLevel());var target=nearestUndiscovered(p);if(target==null)return false;
        if(!com.howierux.mortalexpansion.network.SpectralTracksPayload.send(p,target.anchor()))return false;
        var state=HauntProtection.state(p);state.putLong("TracksUntil",HauntProtection.now(p)+600);
        state.putString("TracksSite",target.id());
        state.putString("TracksDimension",p.level().dimension().location().toString());HauntProtection.state(p,state);
        return true;
    }
    public static void tick(ServerLevel level) {
        if(level.players().isEmpty()||level.getGameTime()%20!=0)return;
        boolean updateTrail=level.getGameTime()%20==0;
        if(updateTrail)refresh(level);
        var sites=SpectralSiteIndex.get(level).sites();
        for(var p:level.players()) {
            if(!p.isAlive())continue;
            for(var site:sites)discover(p,site);
            if(!updateTrail)continue;
            var state=HauntProtection.state(p);
            if(state.getLong("TracksUntil")<=HauntProtection.now(p))continue;
            boolean reached=knownSites(p).stream().anyMatch(site->site.id().equals(state.getString("TracksSite")));
            var target=nearestUndiscovered(p);
            if(reached || target==null || !state.getString("TracksDimension").equals(level.dimension().location().toString())) {
                state.remove("TracksUntil");HauntProtection.state(p,state);com.howierux.mortalexpansion.network.SpectralTracksPayload.clear(p);
            } else {
                state.putString("TracksSite",target.id());HauntProtection.state(p,state);
                com.howierux.mortalexpansion.network.SpectralTracksPayload.send(p,target.anchor());
            }
        }
    }
}

