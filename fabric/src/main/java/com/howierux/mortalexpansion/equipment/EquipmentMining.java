package com.howierux.mortalexpansion.equipment;

import com.howierux.mortalexpansion.item.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

/** Additional blocks are harvested by the real player, preserving Fabric cancellation, drops and tool wear.
 * No discovery loads chunks. A denied connected block cannot bridge into another protected region. */
public final class EquipmentMining {
 private record Face(BlockPos pos,Direction direction,ItemStack tool,long time){}
 private static final Map<ServerPlayer,Face> FACES=new WeakHashMap<>();
 private static final Map<ServerLevel,List<Job>> QUEUED=new WeakHashMap<>();
 private static final ThreadLocal<Boolean> BREAKING=ThreadLocal.withInitial(()->false);
 public enum Kind {PLANE,CONNECTED,TREE}
 public record Plan(Kind kind,Direction face,Set<BlockPos> logs){}
 private static final class Job {
  final ServerPlayer player;final ServerLevel level;final ItemStack tool;final BlockPos origin;final Plan plan;
  final ArrayDeque<BlockPos> queue=new ArrayDeque<>();final Set<BlockPos> seen=new HashSet<>();int count;
  Job(ServerPlayer p,BlockPos pos,Plan plan,boolean broken){player=p;level=p.serverLevel();tool=p.getMainHandItem();origin=pos.immutable();this.plan=plan;
   if(broken){seen.add(origin);count=1;if(plan.kind==Kind.PLANE)for(int a=-1;a<=1;a++)for(int b=-1;b<=1;b++){var next=plane(origin,plan.face,a,b);if(!next.equals(origin))queue.add(next);}else expand(origin);}
   else{queue.add(origin);seen.add(origin);}
  }
  void expand(BlockPos pos){
   if(plan.kind==Kind.TREE){for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){var next=pos.offset(x,y,z);if(plan.logs.contains(next)&&seen.add(next))queue.add(next);}}
   else for(var direction:Direction.values()){var next=pos.relative(direction);if(seen.add(next))queue.add(next);}
  }
 }
 public static void initialize(){ServerTickEvents.END_WORLD_TICK.register(EquipmentMining::tick);ServerWorldEvents.UNLOAD.register((server,level)->QUEUED.remove(level));ServerLifecycleEvents.SERVER_STOPPED.register(server->{QUEUED.clear();FACES.clear();});}
 public static BlockPos plane(BlockPos origin,Direction face,int a,int b){return switch(face.getAxis()){case X->origin.offset(0,a,b);case Y->origin.offset(a,0,b);case Z->origin.offset(a,b,0);};}
 public static void face(ServerPlayer p,BlockPos pos,Direction face,boolean start){if(start)FACES.put(p,new Face(pos.immutable(),face,p.getMainHandItem(),p.serverLevel().getGameTime()));else FACES.remove(p);}
 public static Plan plan(ServerPlayer p,BlockPos pos){
  if(BREAKING.get()||p.isShiftKeyDown()||!p.isAlive()||p.isSpectator()||!p.serverLevel().hasChunkAt(pos))return null;
  var tool=p.getMainHandItem();var state=p.serverLevel().getBlockState(pos);
  if(tool.getItem() instanceof TreeAxeItem&&state.is(BlockTags.LOGS)){var logs=TreeDiscovery.discover(p.serverLevel(),pos);return logs.size()>1?new Plan(Kind.TREE,null,logs):null;}
  if(!(tool.getItem() instanceof AreaShovelItem)||!state.is(BlockTags.MINEABLE_WITH_SHOVEL))return null;
  var face=FACES.remove(p);Direction direction=null;
  if(face!=null&&face.pos.equals(pos)&&face.tool==tool&&p.serverLevel().getGameTime()-face.time<=1200)direction=face.direction;
  if(direction==null){var hit=p.pick(p.blockInteractionRange(),1,false);if(hit instanceof net.minecraft.world.phys.BlockHitResult block&&block.getType()==net.minecraft.world.phys.HitResult.Type.BLOCK&&block.getBlockPos().equals(pos))direction=block.getDirection();}
  return direction==null?null:new Plan(Kind.PLANE,direction,Set.of());
 }
 public static void after(ServerPlayer p,BlockPos pos,Plan plan,BlockState before,boolean success){if(plan!=null&&success&&!p.serverLevel().getBlockState(pos).equals(before))QUEUED.computeIfAbsent(p.serverLevel(),unused->new ArrayList<>()).add(new Job(p,pos,plan,true));}
 public static void connected(ServerPlayer p,BlockPos pos){if(!p.isShiftKeyDown()&&p.getMainHandItem().is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_SHOVEL)&&p.distanceToSqr(pos.getCenter())<=Math.pow(p.blockInteractionRange()+1,2))QUEUED.computeIfAbsent(p.serverLevel(),unused->new ArrayList<>()).add(new Job(p,pos,new Plan(Kind.CONNECTED,null,Set.of()),false));}
 private static boolean allowed(Job job,BlockPos pos){var p=job.player;var l=job.level;
  if(!l.hasChunkAt(pos)||!l.getWorldBorder().isWithinBounds(pos)||l.isOutsideBuildHeight(pos)||!l.mayInteract(p,pos))return false;
  var state=l.getBlockState(pos);return state.is(job.plan.kind==Kind.TREE?BlockTags.LOGS:BlockTags.MINEABLE_WITH_SHOVEL)&&state.getDestroySpeed(l,pos)>=0&&(!state.requiresCorrectToolForDrops()||job.tool.isCorrectToolForDrops(state));
 }
 public static void tick(ServerLevel level){
  var jobs=QUEUED.remove(level);if(jobs==null)return;var remaining=new ArrayList<Job>();
  for(var job:jobs){var p=job.player;int limit=job.plan.kind==Kind.CONNECTED?64:job.plan.kind==Kind.TREE?512:9;
   if(!p.isAlive()||p.isSpectator()||p.hasDisconnected()||p.serverLevel()!=level||p.getMainHandItem()!=job.tool||job.tool.isEmpty()||p.isShiftKeyDown()||p.distanceToSqr(job.origin.getCenter())>Math.pow(p.blockInteractionRange()+3,2))continue;
   BREAKING.set(true);try{int budget=32;while(!job.queue.isEmpty()&&job.count<limit&&budget-->0&&!job.tool.isEmpty()&&p.getMainHandItem()==job.tool){var pos=job.queue.removeFirst();if(!allowed(job,pos))continue;var before=level.getBlockState(pos);if(p.gameMode.destroyBlock(pos)&&!level.getBlockState(pos).equals(before)){job.count++;if(job.plan.kind!=Kind.PLANE)job.expand(pos);}}}finally{BREAKING.set(false);}
   if(!job.queue.isEmpty()&&job.count<limit&&!job.tool.isEmpty())remaining.add(job);
  }if(!remaining.isEmpty())QUEUED.computeIfAbsent(level,unused->new ArrayList<>()).addAll(remaining);
 }
}
