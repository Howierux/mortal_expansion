package com.howierux.mortalexpansion.guide;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import java.util.*;

/** Server-owned abilities. Only Greed has a terrain search, sliced into bounded per-tick work. */
public final class DamnedAbilities {
    private DamnedAbilities() {}
    private static final ThreadLocal<Boolean> GREED_BREAK = ThreadLocal.withInitial(() -> false);
    private static final Map<UUID,GreedSearch> SEARCHES = new HashMap<>();
    private static final Map<ServerLevel,String> VOTES = new WeakHashMap<>();
    public static boolean active(Player p,DamnedSecret secret){return p.isAlive() && !p.isSpectator() && secret.unlocked(p);}
    public static void hint(ServerPlayer p,String key){p.displayClientMessage(Component.translatable("damned.mortal_expansion."+key),true);}
    @SubscribeEvent public static void tick(PlayerTickEvent.Post event){
        if(!(event.getEntity() instanceof ServerPlayer p) || !p.isAlive() || p.isSpectator())return;
        var state=p.getData(ModAttachments.DAMNED_ABILITY_STATE);long now=p.level().getGameTime();
        if(active(p,DamnedSecret.LUST)){
            boolean crouch=p.isShiftKeyDown();
            if(crouch && !state.getBoolean("crouching")){
                int count=now-state.getLong("crouch_time")>60?1:state.getInt("crouches")+1;
                state.putLong("crouch_time",now);state.putInt("crouches",count);
                if(count>=3 && now>=state.getLong("lust_ready")){
                    state.putInt("crouches",0);state.putLong("lust_ready",now+200);
                    if(breed(p)>0)hint(p,"lust");
                }
            }
            state.putBoolean("crouching",crouch);
        }
        if(active(p,DamnedSecret.PRIDE)){
            if(!state.contains("pride_time") || now<state.getLong("pride_time"))state.putLong("pride_time",now);
            if(now-state.getLong("pride_time")>=200){state.putLong("pride_time",now);p.giveExperiencePoints(1);hint(p,"pride");}
        }
        if(active(p,DamnedSecret.GREED) && p.isUsingItem() && p.getUseItem().is(Items.DIAMOND) && p.getTicksUsingItem()>=40){
            p.stopUsingItem();
            if(!SEARCHES.containsKey(p.getUUID()) && now>=state.getLong("greed_ready")){
                state.putLong("greed_ready",now+100);SEARCHES.put(p.getUUID(),new GreedSearch(p));hint(p,"greed_search");
            }
        }
        var search=SEARCHES.get(p.getUUID());
        if(search!=null && search.advance(p))SEARCHES.remove(p.getUUID());
    }
    @SubscribeEvent public static void logout(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e){SEARCHES.remove(e.getEntity().getUUID());}
    @SubscribeEvent public static void stopped(net.neoforged.neoforge.event.server.ServerStoppedEvent e){SEARCHES.clear();VOTES.clear();}
    /** Vanilla courtship handles mating compatibility, child creation, cooldowns, and breed hooks. */
    public static int breed(ServerPlayer p){
        if(!active(p,DamnedSecret.LUST))return 0;
        int count=0;
        for(var animal:p.level().getEntitiesOfClass(Animal.class,p.getBoundingBox().inflate(8),a->a.isAlive() && a.getAge()==0 && a.canFallInLove())){
            animal.setInLove(p);p.serverLevel().sendParticles(ParticleTypes.HEART,animal.getX(),animal.getY()+.5,animal.getZ(),3,.2,.2,.2,.01);count++;
        }
        return count;
    }
    @SubscribeEvent public static void use(PlayerInteractEvent.RightClickItem e){
        var p=e.getEntity();
        if(e.getItemStack().is(Items.DIAMOND) && active(p,DamnedSecret.GREED)){
            p.startUsingItem(e.getHand());e.setCancellationResult(InteractionResult.CONSUME);e.setCanceled(true);
        }
    }
    @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void drops(BlockDropsEvent e){if(GREED_BREAK.get())e.setCanceled(true);}
    public static boolean isOre(BlockState state){return state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE);}
    public static boolean takeDiamond(ServerPlayer p,BlockPos pos){
        var level=p.serverLevel();
        if(!active(p,DamnedSecret.GREED) || !p.getMainHandItem().is(Items.DIAMOND) && !p.getOffhandItem().is(Items.DIAMOND)
                || !level.hasChunkAt(pos) || p.distanceToSqr(pos.getCenter())>64*64 || !isOre(level.getBlockState(pos))
                || !level.mayInteract(p,pos) || p.blockActionRestricted(level,pos,p.gameMode.getGameModeForPlayer()))return false;
        // Native mining posts cancellable BreakEvent and applies protection and block hooks.
        // Capture its loot: this secret always yields one diamond, independent of Fortune/Silk Touch.
        boolean previous=GREED_BREAK.get();GREED_BREAK.set(true);
        try{if(!p.gameMode.destroyBlock(pos) || isOre(level.getBlockState(pos)))return false;}
        finally{GREED_BREAK.set(previous);}
        var diamond=new ItemStack(Items.DIAMOND);if(!p.getInventory().add(diamond))p.drop(diamond,false);
        hint(p,"greed");return true;
    }
    private static final class GreedSearch {
        final net.minecraft.resources.ResourceKey<Level> dimension;final BlockPos origin;
        final List<BlockPos> sections=new ArrayList<>();int section,index;
        GreedSearch(ServerPlayer p){
            dimension=p.level().dimension();origin=p.blockPosition();var level=p.serverLevel();
            for(int x=(origin.getX()-64)>>4;x<=(origin.getX()+64)>>4;x++)for(int z=(origin.getZ()-64)>>4;z<=(origin.getZ()+64)>>4;z++){
                var chunk=level.getChunkSource().getChunkNow(x,z);if(chunk==null)continue;
                for(int s=0;s<chunk.getSections().length;s++)if(chunk.getSection(s).maybeHas(DamnedAbilities::isOre)){
                    int y=level.getMinBuildHeight()+s*16;
                    if(y<=origin.getY()+64 && y+15>=origin.getY()-64)sections.add(new BlockPos(x*16,y,z*16));
                }
            }
            sections.sort(Comparator.comparingDouble(b->b.distSqr(origin)));
        }
        boolean advance(ServerPlayer p){
            if(!active(p,DamnedSecret.GREED) || (!p.getMainHandItem().is(Items.DIAMOND) && !p.getOffhandItem().is(Items.DIAMOND)) || p.level().dimension()!=dimension || p.distanceToSqr(origin.getCenter())>16*16)return true;
            for(int work=0;work<4096 && section<sections.size();work++){
                var base=sections.get(section);int n=index++;
                var pos=base.offset(n&15,n>>8,(n>>4)&15);
                if(index==4096){index=0;section++;}
                if(pos.distSqr(origin)>64*64 || !p.serverLevel().hasChunkAt(pos))continue;
                if(isOre(p.level().getBlockState(pos)) && takeDiamond(p,pos))return true;
            }
            if(section==sections.size()){hint(p,"greed_none");return true;}return false;
        }
    }
    @SubscribeEvent public static void damage(LivingDamageEvent.Post e){if(e.getNewDamage()>0 && e.getEntity() instanceof ServerPlayer p)wrath(p);}
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void kill(LivingDeathEvent e){if(e.getSource().getEntity() instanceof ServerPlayer p)wrath(p);}
    public static void wrath(ServerPlayer p){
        if(!active(p,DamnedSecret.WRATH))return;
        var state=p.getData(ModAttachments.DAMNED_ABILITY_STATE);long now=p.level().getGameTime();
        int stacks=state.getLong("wrath_until")>now?Math.min(2,state.getInt("wrath_stacks")+1):1;
        state.putInt("wrath_stacks",stacks);state.putLong("wrath_until",now+200);
        var existing=p.getEffect(MobEffects.DAMAGE_BOOST);
        if(existing==null || existing.getAmplifier()<stacks-1 || existing.getAmplifier()==stacks-1 && existing.getDuration()<200)
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,200,stacks-1));
        hint(p,"wrath");
    }
    public static boolean dayVote(ServerLevel level){return level.players().stream().anyMatch(p->p.isSleeping() && active(p,DamnedSecret.SLOTH));}
    private static boolean day(ServerLevel level){return level.dimension()==Level.OVERWORLD && Math.floorMod(level.getDayTime(),24000)<12000;}
    @SubscribeEvent(priority=EventPriority.LOW) public static void sleep(CanPlayerSleepEvent e){
        var p=e.getEntity();
        if((!active(p,DamnedSecret.SLOTH) && !dayVote(p.serverLevel())) || !day(p.serverLevel()) || e.getProblem()!=Player.BedSleepingProblem.NOT_POSSIBLE_NOW)return;
        var at=e.getPos();
        // Daytime vanilla returns before the monster check: retain that check explicitly.
        if(!p.isCreative() && !p.level().getEntitiesOfClass(Monster.class,new AABB(at).inflate(8,5,8),m->m.isPreventingPlayerRest(p)).isEmpty()){
            e.setProblem(Player.BedSleepingProblem.NOT_SAFE);return;
        }
        e.setProblem(null);hint(p,"sloth_vote");
    }
    @SubscribeEvent public static void keepSleeping(CanContinueSleepingEvent e){
        if(e.getEntity() instanceof ServerPlayer p && (active(p,DamnedSecret.SLOTH) || dayVote(p.serverLevel())) && day(p.serverLevel())
                && e.getProblem()==Player.BedSleepingProblem.NOT_POSSIBLE_NOW)e.setContinueSleeping(true);
    }
    public static int majority(int players){return players/2+1;}
    @SubscribeEvent public static void levelTick(LevelTickEvent.Post e){
        if(!(e.getLevel() instanceof ServerLevel level) || !day(level) || !level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT))return;
        // One actual sleeper starts the vote. A strict majority of all online nonspectators must sleep.
        var online=level.getServer().getPlayerList().getPlayers().stream().filter(p->!p.isSpectator()).toList();
        long votes=online.stream().filter(p->p.serverLevel()==level && p.isSleepingLongEnough()).count();
        if(!dayVote(level)){VOTES.remove(level);return;}
        int needed=majority(online.size());
        long sleepers=online.stream().filter(p->p.serverLevel()==level && p.isSleeping()).count();
        String count=sleepers+"/"+needed;
        if(!count.equals(VOTES.put(level,count)))for(var p:online)
            p.sendSystemMessage(Component.translatable("damned.mortal_expansion.sloth_count",sleepers,needed));
        if(votes<needed)return;
        VOTES.remove(level);
        long day=level.getDayTime();level.setDayTime(day-Math.floorMod(day,24000)+13000);
        for(var p:List.copyOf(level.players()))if(p.isSleeping()){p.stopSleepInBed(false,true);hint(p,"sloth");}
    }
}
