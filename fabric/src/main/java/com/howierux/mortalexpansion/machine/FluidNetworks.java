package com.howierux.mortalexpansion.machine;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import java.util.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/** Cached loaded-only topology. One pass per network, never a world scan per pipe. */
public final class FluidNetworks {
    private static final Map<ServerLevel,Data> LEVELS=new WeakHashMap<>();
    private record Endpoint(FluidMachineEntity pipe,Direction side){}
    private record Edge(FluidMachineEntity a,FluidMachineEntity b){}
    private static final class Network {
        final List<FluidMachineEntity> pipes=new ArrayList<>();
        final List<Endpoint> inputs=new ArrayList<>(),outputs=new ArrayList<>();
        final List<Edge> edges=new ArrayList<>();
        final Set<FluidMachineEntity> nonempty=new LinkedHashSet<>();
        final Map<FluidMachineEntity,Map<FluidMachineEntity,FluidMachineEntity>> parents=new HashMap<>();
    }
    private static final class Data {
        final Map<BlockPos,FluidMachineEntity> pipes=new HashMap<>();
        final Map<FluidMachineEntity,Network> owners=new HashMap<>();
        final Set<FluidMachineEntity> sync=new HashSet<>();
        final List<Network> networks=new ArrayList<>();boolean dirty=true;
    }
    private FluidNetworks(){}
    public static void add(FluidMachineEntity pipe){
        if(!(pipe.getLevel() instanceof ServerLevel level))return;var data=LEVELS.computeIfAbsent(level,k->new Data());
        if(pipe.pipe()){data.pipes.put(pipe.getBlockPos(),pipe);data.dirty=true;}data.sync.add(pipe);
    }
    public static void remove(FluidMachineEntity pipe){
        if(!(pipe.getLevel() instanceof ServerLevel level))return;var data=LEVELS.get(level);if(data==null)return;
        if(data.pipes.remove(pipe.getBlockPos(),pipe))data.dirty=true;data.sync.remove(pipe);data.owners.remove(pipe);
    }
    public static void invalidate(FluidMachineEntity pipe){if(pipe.getLevel() instanceof ServerLevel level){var d=LEVELS.get(level);if(d!=null)d.dirty=true;}}
    public static void changed(FluidMachineEntity pipe){
        if(!(pipe.getLevel() instanceof ServerLevel level))return;var d=LEVELS.computeIfAbsent(level,k->new Data());d.sync.add(pipe);
        var n=d.owners.get(pipe);if(n!=null){if(pipe.tank.isResourceBlank())n.nonempty.remove(pipe);else n.nonempty.add(pipe);}
    }
    private static boolean loaded(ServerLevel level,BlockPos pos){return level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null;}
    private static Direction direction(FluidMachineEntity a,FluidMachineEntity b){return Direction.fromDelta(
            b.getBlockPos().getX()-a.getBlockPos().getX(),b.getBlockPos().getY()-a.getBlockPos().getY(),b.getBlockPos().getZ()-a.getBlockPos().getZ());}
    private static boolean link(FluidMachineEntity a,FluidMachineEntity b){var side=direction(a,b);return a.mode(side)!=3 && b.mode(side.getOpposite())!=3;}
    private static boolean flow(FluidMachineEntity a,FluidMachineEntity b){var side=direction(a,b);return link(a,b) && a.mode(side)!=1 && b.mode(side.getOpposite())!=2;}
    private static Storage<FluidVariant> endpoint(ServerLevel level,Endpoint ep){
        var pos=ep.pipe().getBlockPos().relative(ep.side());if(!loaded(level,pos))return null;
        return FluidStorage.SIDED.find(level,pos,ep.side().getOpposite());
    }
    private static void rebuild(ServerLevel level,Data d){
        d.dirty=false;d.networks.clear();d.owners.clear();var unseen=new HashSet<>(d.pipes.values());
        while(!unseen.isEmpty()){
            var start=unseen.iterator().next();unseen.remove(start);var n=new Network();var queue=new ArrayDeque<FluidMachineEntity>();queue.add(start);
            while(!queue.isEmpty()){
                var p=queue.removeFirst();if(p.isRemoved() || !loaded(level,p.getBlockPos()))continue;
                var state=p.getBlockState();var connected=state;
                for(var side:Direction.values()) {
                    var neighbor=p.getBlockPos().relative(side);
                    boolean link=p.mode(side)!=3&&loaded(level,neighbor)&&FluidStorage.SIDED.find(level,neighbor,side.getOpposite())!=null;
                    if(loaded(level,neighbor)&&level.getBlockEntity(neighbor) instanceof FluidMachineEntity other&&other.pipe()&&other.mode(side.getOpposite())==3)link=false;
                    connected=connected.setValue(FluidMachineBlock.property(side),link);
                }
                if(!connected.equals(state))level.setBlock(p.getBlockPos(),connected,2);
                n.pipes.add(p);d.owners.put(p,n);if(!p.tank.isResourceBlank())n.nonempty.add(p);
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
    private static Map<FluidMachineEntity,FluidMachineEntity> parents(Network n,FluidMachineEntity sink,Data data){
        return n.parents.computeIfAbsent(sink,k->{
            var result=new HashMap<FluidMachineEntity,FluidMachineEntity>();var queue=new ArrayDeque<FluidMachineEntity>();result.put(sink,sink);queue.add(sink);
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
        for(var input:n.inputs){var source=endpoint(level,input);if(source!=null)StorageUtil.move(source,input.pipe().storage(null),v->true,input.pipe().rate(),null);}
        if(n.nonempty.isEmpty())return;
        // Rotate output order for branching networks without multiplying the pipe's shared tick budget.
        int offset=n.outputs.isEmpty()?0:(int)Math.floorMod(level.getGameTime(),n.outputs.size());
        for(int i=0;i<n.outputs.size();i++){
            var output=n.outputs.get((i+offset)%n.outputs.size());var target=endpoint(level,output);if(target==null)continue;
            var parents=parents(n,output.pipe(),data);
            for(var source:List.copyOf(n.nonempty)){
                if(!parents.containsKey(source) || source.tank.isResourceBlank())continue;
                var path=new ArrayList<FluidMachineEntity>();var at=source;long limit=Long.MAX_VALUE;
                while(true){
                    if(!at.tank.isResourceBlank() && !source.tank.variant.equals(at.tank.variant)){limit=0;break;}
                    path.add(at);limit=Math.min(limit,at.remaining());if(at==output.pipe())break;at=parents.get(at);
                }
                if(limit<=0)continue;
                try(var tx=Transaction.openOuter()) {
                    long moved=StorageUtil.move(source.storage(null),target,v->true,limit,tx);
                    if(moved>0){for(var member:path)if(member!=source)member.reserve(moved,tx);tx.commit();}
                }
            }
        }
        // Retain actual per-pipe buffers when outputs are full or absent; no pooled fluid can be lost on splits.
        for(var edge:n.edges){
            var a=edge.a();var b=edge.b();
            if((long)a.tank.amount*b.tank.getCapacity()<(long)b.tank.amount*a.tank.getCapacity()){var swap=a;a=b;b=swap;}
            if(!flow(a,b) || a.tank.isResourceBlank())continue;
            long imbalance=(long)a.tank.amount*b.tank.getCapacity()-(long)b.tank.amount*a.tank.getCapacity();
            long amount=Math.min(a.remaining(),imbalance/(a.tank.getCapacity()+b.tank.getCapacity()));
            if(amount>0)StorageUtil.move(a.storage(null),b.storage(null),v->true,amount,null);
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
    public static void clear(){LEVELS.clear();}
}
