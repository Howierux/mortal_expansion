package com.howierux.mortalexpansion.haunt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import java.util.*;

/** Snapshot planning preserves no-chain spread; only already-loaded chunks participate. */
public final class HauntSimulation {
    private HauntSimulation(){}
    private static final Map<ServerLevel,Set<Long>> LOADED=new WeakHashMap<>();
    private static final Map<ServerLevel,Long> LAST=new WeakHashMap<>();
    private static final int[][] SIDES={{1,0},{-1,0},{0,1},{0,-1}};
    public static void track(ServerLevel level,ChunkPos pos){if(NaturalHauntGenerator.supportsDimension(level))LOADED.computeIfAbsent(level,k->new HashSet<>()).add(pos.toLong());}
    public static void untrack(ServerLevel level,ChunkPos pos){var tracked=LOADED.get(level);if(tracked!=null)tracked.remove(pos.toLong());}
    public static void clear(){LOADED.clear();LAST.clear();}
    public static float spreadChance(int value,int phase){
        float weight=.5F+.25F*(4-Math.abs(Math.floorMod(phase,8)-4));
        return value<=0?0:Math.min(1,(2F/3F)*weight);
    }
    public static boolean spreadCheckDue(long previous,long current){return current>previous && Math.floorDiv(current,8000)>Math.floorDiv(previous,8000);}
    public static float leakChance(int value){return .0025F*HauntTier.fromValue(value).id();}
    public static boolean leak(ServerLevel level,net.minecraft.server.level.ServerPlayer p,float roll){
        int personal=PlayerHauntManager.getValue(p);var pos=p.chunkPosition();
        if(level.getChunkSource().getChunkNow(pos.x,pos.z)==null || personal<=HauntManager.getValue(level,pos) || roll>=leakChance(personal))return false;
        if(PurificationWardTracker.isChunkProtected(level,pos))return false;
        HauntManager.addPoints(level,pos,1);PlayerHauntManager.addPoints(p,-1);return true;
    }
    public static Map<Long,Integer> plan(Map<Long,Integer> starting,Map<Long,Integer> floors,boolean spread,boolean daily,int moon,RandomSource random){
        return plan(starting,floors,spread,daily,moon,random,Set.of());
    }
    public static Map<Long,Integer> plan(Map<Long,Integer> starting,Map<Long,Integer> floors,boolean spread,boolean daily,int moon,RandomSource random,Set<Long> protectedChunks){
        var planned=new HashMap<>(starting);
        if(daily)for(long key:protectedChunks)if(planned.containsKey(key))planned.put(key,Math.max(0,planned.get(key)-1));
        for(var entry:starting.entrySet()){
            long key=entry.getKey();int value=entry.getValue();
            if(spread && !protectedChunks.contains(key) && value>0 && random.nextFloat()<spreadChance(value,moon)){
                var at=new ChunkPos(key);int[] side=SIDES[random.nextInt(4)];long target=ChunkPos.asLong(at.x+side[0],at.z+side[1]);
                if(starting.containsKey(target)&&!protectedChunks.contains(target))planned.compute(target,(k,v)->Math.min(HauntTier.MAX_VALUE,v+1));
            }
            if(daily && !protectedChunks.contains(key) && random.nextFloat()<.10F){
                int floor=floors.getOrDefault(key,HauntTier.NATURAL.midpointValue()),current=planned.get(key);
                planned.put(key,current+Integer.compare(floor,current));
            }
        }
        return planned;
    }
    public static void tick(ServerLevel level){
        if(!NaturalHauntGenerator.supportsDimension(level))return;
        if(level.getGameTime()%200==0)for(var p:level.players())leak(level,p,level.random.nextFloat());
        long now=level.getDayTime();Long previous=LAST.put(level,now);if(previous==null)return;boolean spread=spreadCheckDue(previous,now),bloodstone=BloodstoneFormation.due(previous,now);if(!spread&&!bloodstone)return;
        boolean daily=Math.floorDiv(now,24000)>Math.floorDiv(previous,24000);
        var starting=new HashMap<Long,Integer>();var floors=new HashMap<Long,Integer>();
        for(long key:List.copyOf(LOADED.getOrDefault(level,Set.of()))){
            var pos=new ChunkPos(key);var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);if(chunk==null)continue;
            if(daily)GraveyardStoneWeathering.sample(level,chunk);
            starting.put(key,HauntManager.getValue(level,pos));
            if(daily)floors.put(key,NaturalHauntGenerator.determineInitialTier(level,chunk).midpointValue());
        }
        var protectedChunks=PurificationWardTracker.protectedLoadedChunks(level,starting.keySet());
        var results=plan(starting,floors,spread,daily,level.getMoonPhase(),level.random,protectedChunks);
        if(bloodstone)for(var entry:starting.entrySet())if(entry.getValue()>=30&&level.random.nextFloat()<BloodstoneFormation.chance(entry.getValue())){
            var pos=new ChunkPos(entry.getKey());var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);
            if(chunk!=null&&BloodstoneFormation.convert(level,chunk,level.random)){
                int floor=NaturalHauntGenerator.determineInitialTier(level,chunk).midpointValue(),planned=results.get(entry.getKey());
                if(planned>floor)results.put(entry.getKey(),Math.max(floor,planned-1));
            }
        }
        for(var entry:results.entrySet()){
            int change=entry.getValue()-starting.get(entry.getKey());if(change!=0)HauntManager.addPoints(level,new ChunkPos(entry.getKey()),change);
        }
    }
}
