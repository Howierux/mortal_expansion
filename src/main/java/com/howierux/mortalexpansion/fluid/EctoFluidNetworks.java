package com.howierux.mortalexpansion.fluid;

import com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.*;
import java.util.*;

/** Cached loaded-only topology. One pass per network, never a world scan per pipe. */
public final class EctoFluidNetworks {
    private static final Map<ServerLevel,Data> LEVELS=new WeakHashMap<>();
    private record Endpoint(EctoFluidBlockEntity pipe,Direction side){}
    private record Edge(EctoFluidBlockEntity a,EctoFluidBlockEntity b){}
    private static final class Network {
        final List<EctoFluidBlockEntity> pipes=new ArrayList<>();
        final List<Endpoint> inputs=new ArrayList<>(),outputs=new ArrayList<>();
        final List<Edge> edges=new ArrayList<>();
        final Set<EctoFluidBlockEntity> nonempty=new LinkedHashSet<>();
        final Map<EctoFluidBlockEntity,Map<EctoFluidBlockEntity,EctoFluidBlockEntity>> parents=new HashMap<>();
    }
    private static final class Data {
        final Map<BlockPos,EctoFluidBlockEntity> pipes=new HashMap<>();
        final Map<EctoFluidBlockEntity,Network> owners=new HashMap<>();
        final Set<EctoFluidBlockEntity> sync=new HashSet<>();
        final List<Network> networks=new ArrayList<>();boolean dirty=true;
    }
    private EctoFluidNetworks(){}
    public static void add(EctoFluidBlockEntity pipe){
        if(!(pipe.getLevel() instanceof ServerLevel level))return;var data=LEVELS.computeIfAbsent(level,k->new Data());
        if(pipe.isPipe()){data.pipes.put(pipe.getBlockPos(),pipe);data.dirty=true;}data.sync.add(pipe);
    }
    public static void remove(EctoFluidBlockEntity pipe){
        if(!(pipe.getLevel() instanceof ServerLevel level))return;var data=LEVELS.get(level);if(data==null)return;
        if(data.pipes.remove(pipe.getBlockPos(),pipe))data.dirty=true;data.sync.remove(pipe);data.owners.remove(pipe);
    }
    public static void invalidate(EctoFluidBlockEntity pipe){if(pipe.getLevel() instanceof ServerLevel level){var d=LEVELS.get(level);if(d!=null)d.dirty=true;}}
    public static void changed(EctoFluidBlockEntity pipe){
        if(!(pipe.getLevel() instanceof ServerLevel level))return;var d=LEVELS.computeIfAbsent(level,k->new Data());d.sync.add(pipe);
        var n=d.owners.get(pipe);if(n!=null){if(pipe.tank.isEmpty())n.nonempty.remove(pipe);else n.nonempty.add(pipe);}
    }
    private static boolean loaded(ServerLevel level,BlockPos pos){return level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
    private static Direction direction(EctoFluidBlockEntity a,EctoFluidBlockEntity b){return Direction.fromDelta(
            b.getBlockPos().getX()-a.getBlockPos().getX(),b.getBlockPos().getY()-a.getBlockPos().getY(),b.getBlockPos().getZ()-a.getBlockPos().getZ());}
    private static boolean link(EctoFluidBlockEntity a,EctoFluidBlockEntity b){var side=direction(a,b);return a.mode(side)!=3 && b.mode(side.getOpposite())!=3;}
    private static boolean flow(EctoFluidBlockEntity a,EctoFluidBlockEntity b){var side=direction(a,b);return link(a,b) && a.mode(side)!=1 && b.mode(side.getOpposite())!=2;}
    private static net.neoforged.neoforge.fluids.capability.IFluidHandler endpoint(ServerLevel level,Endpoint ep){
        var pos=ep.pipe().getBlockPos().relative(ep.side());if(!loaded(level,pos))return null;
        return level.getCapability(Capabilities.FluidHandler.BLOCK,pos,ep.side().getOpposite());
    }
    private static void rebuild(ServerLevel level,Data d){
        d.dirty=false;d.networks.clear();d.owners.clear();var unseen=new HashSet<>(d.pipes.values());
        while(!unseen.isEmpty()){
            var start=unseen.iterator().next();unseen.remove(start);var n=new Network();var queue=new ArrayDeque<EctoFluidBlockEntity>();queue.add(start);
            while(!queue.isEmpty()){
                var p=queue.removeFirst();if(p.isRemoved() || !loaded(level,p.getBlockPos()))continue;
                var state=p.getBlockState();var connected=com.howierux.mortalexpansion.block.EctoPipeBlock.connections(level,p.getBlockPos(),state);
                if(!connected.equals(state))level.setBlock(p.getBlockPos(),connected,2);
                n.pipes.add(p);d.owners.put(p,n);if(!p.tank.isEmpty())n.nonempty.add(p);
                for(var side:Direction.values()){
                    var other=d.pipes.get(p.getBlockPos().relative(side));
                    if(other!=null){if(link(p,other) && unseen.remove(other))queue.add(other);}
                    else if(p.mode(side)!=3){var ep=new Endpoint(p,side);if(endpoint(level,ep)!=null){if(p.mode(side)==1)n.inputs.add(ep);else n.outputs.add(ep);}}
                }
            }
            for(var p:n.pipes)for(var side:Direction.values()){
                var other=d.pipes.get(p.getBlockPos().relative(side));if(other!=null && d.owners.get(other)==n && link(p,other)
                        && p.getBlockPos().asLong()<other.getBlockPos().asLong())n.edges.add(new Edge(p,other));
            }
            d.networks.add(n);
        }
    }
    private static Map<EctoFluidBlockEntity,EctoFluidBlockEntity> parents(Network n,EctoFluidBlockEntity sink,Data data){
        return n.parents.computeIfAbsent(sink,k->{
            var result=new HashMap<EctoFluidBlockEntity,EctoFluidBlockEntity>();var queue=new ArrayDeque<EctoFluidBlockEntity>();result.put(sink,sink);queue.add(sink);
            while(!queue.isEmpty()){
                var at=queue.removeFirst();for(var side:Direction.values()){
                    var from=data.pipes.get(at.getBlockPos().relative(side));
                    if(from!=null && data.owners.get(from)==n && flow(from,at) && !result.containsKey(from)){result.put(from,at);queue.add(from);}
                }
            }
            return result;
        });
    }
    private static void tickNetwork(ServerLevel level,Data data,Network n){
        for(var input:n.inputs){var source=endpoint(level,input);if(source!=null)FluidUtil.tryFluidTransfer(input.pipe().handler(null),source,input.pipe().rate(),true);}
        if(n.nonempty.isEmpty())return;
        // Rotate output order for branching networks without multiplying the pipe's shared tick budget.
        int offset=n.outputs.isEmpty()?0:(int)Math.floorMod(level.getGameTime(),n.outputs.size());
        for(int i=0;i<n.outputs.size();i++){
            var output=n.outputs.get((i+offset)%n.outputs.size());var target=endpoint(level,output);if(target==null)continue;
            var parents=parents(n,output.pipe(),data);
            for(var source:List.copyOf(n.nonempty)){
                if(!parents.containsKey(source) || source.tank.isEmpty())continue;
                var path=new ArrayList<EctoFluidBlockEntity>();var at=source;int limit=Integer.MAX_VALUE;
                while(true){
                    if(!at.tank.isEmpty() && !FluidStack.isSameFluidSameComponents(source.tank.getFluid(),at.tank.getFluid())){limit=0;break;}
                    path.add(at);limit=Math.min(limit,at.remaining());if(at==output.pipe())break;at=parents.get(at);
                }
                if(limit<=0)continue;
                var moved=FluidUtil.tryFluidTransfer(target,source.handler(null),limit,true);
                if(!moved.isEmpty())for(var member:path)if(member!=source)member.reserve(moved.getAmount());
            }
        }
        // Retain actual per-pipe buffers when outputs are full or absent; no pooled fluid can be lost on splits.
        for(var edge:n.edges){
            var a=edge.a();var b=edge.b();
            if((long)a.tank.getFluidAmount()*b.tank.getCapacity()<(long)b.tank.getFluidAmount()*a.tank.getCapacity()){var swap=a;a=b;b=swap;}
            if(!flow(a,b) || a.tank.isEmpty())continue;
            long imbalance=(long)a.tank.getFluidAmount()*b.tank.getCapacity()-(long)b.tank.getFluidAmount()*a.tank.getCapacity();
            int amount=(int)Math.min(a.remaining(),imbalance/(a.tank.getCapacity()+b.tank.getCapacity()));
            if(amount>0)FluidUtil.tryFluidTransfer(b.handler(null),a.handler(null),amount,true);
        }
    }
    public static void tick(ServerLevel level){
        var data=LEVELS.get(level);if(data==null)return;
        if(data.dirty)rebuild(level,data);
        for(var network:List.copyOf(data.networks))tickNetwork(level,data,network);
        if(level.getGameTime()%5==0){
            for(var tank:List.copyOf(data.sync))if(!tank.isRemoved() && loaded(level,tank.getBlockPos())){
                var state=tank.getBlockState();level.sendBlockUpdated(tank.getBlockPos(),state,state,2);
            }
            data.sync.clear();
        }
        // Capability providers may appear without a block-state change; refresh topology periodically, not per pipe per tick.
        if(level.getGameTime()%20==0)data.dirty=true;
    }
    @SubscribeEvent public static void levelTick(LevelTickEvent.Post event){if(event.getLevel() instanceof ServerLevel level)tick(level);}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level)LEVELS.remove(level);}
}
