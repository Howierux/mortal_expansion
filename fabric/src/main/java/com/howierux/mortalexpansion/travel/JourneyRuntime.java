package com.howierux.mortalexpansion.travel;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.entity.PlayerCorpseEntity;
import com.howierux.mortalexpansion.network.JourneyPayloads;
import com.howierux.mortalexpansion.spiritwell.*;
import com.howierux.mortalexpansion.haunt.*;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.Vec3;
public final class JourneyRuntime {
 public static final AttachmentType<CompoundTag> DATA=AttachmentRegistry.create(Catalog.id("spirit_journey"),b->b.initializer(CompoundTag::new).persistent(CompoundTag.CODEC).copyOnDeath());
 public static final AttachmentType<CompoundTag> ESCROW=AttachmentRegistry.create(Catalog.id("deferred_death_items"),b->b.initializer(CompoundTag::new).persistent(CompoundTag.CODEC).copyOnDeath());
 public static final BlockEntityType<GraveEntity> GRAVE=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("grave"),BlockEntityType.Builder.of(GraveEntity::new,CatalogBlocks.GRAVE).build(null));
 public static final BlockEntityType<SpiritwellBlockEntity> WELL=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("spiritwell"),BlockEntityType.Builder.of(SpiritwellBlockEntity::new,CatalogBlocks.SPIRITWELL).build(null));
 public static final EntityType<PlayerCorpseEntity> CORPSE=Registry.register(BuiltInRegistries.ENTITY_TYPE,Catalog.id("player_corpse"),EntityType.Builder.<PlayerCorpseEntity>of(PlayerCorpseEntity::new,MobCategory.MISC).sized(.6F,.3F).clientTrackingRange(10).build(null));
 public static CompoundTag data(ServerPlayer p){return ((AttachmentTarget)p).getAttachedOrCreate(DATA).copy();}
 private static void data(ServerPlayer p,CompoundTag tag){((AttachmentTarget)p).setAttached(DATA,tag);}
 public static CompoundTag escrow(ServerPlayer p){return ((AttachmentTarget)p).getAttachedOrCreate(ESCROW);}
 public static void escrow(ServerPlayer p,CompoundTag tag){((AttachmentTarget)p).setAttached(ESCROW,tag);}
 public static String body(ServerPlayer p){return data(p).getString("Corpse");}
 public static void body(ServerPlayer p,String id){var tag=data(p);tag.putString("Corpse",id);data(p,tag);}
 public static void grave(ServerPlayer p,String point){var tag=data(p);tag.putString("Grave",point);data(p,tag);}
 public record GraveTarget(ServerLevel level,BlockPos pos){}
 public static GraveTarget graveTarget(ServerPlayer p){String[] parts=data(p).getString("Grave").split("\\|");if(parts.length!=4)return null;try{var level=p.getServer().getLevel(net.minecraft.resources.ResourceKey.create(Registries.DIMENSION,net.minecraft.resources.ResourceLocation.parse(parts[0])));var pos=new BlockPos(Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));if(level==null||!level.getWorldBorder().isWithinBounds(pos))return null;return level.getBlockEntity(pos) instanceof GraveEntity grave&&grave.owned(p)?new GraveTarget(level,pos):null;}catch(RuntimeException error){return null;}}
 public static boolean canChoose(ServerPlayer p){return p.isDeadOrDying()&&body(p).isEmpty()&&!p.getServer().isHardcore()&&graveTarget(p)!=null;}
 public static boolean active(ServerPlayer p){return p.isAlive()&&p.isSpectator()&&!body(p).isEmpty();}
 public static void sync(ServerPlayer p){if(!ServerPlayNetworking.canSend(p,JourneyPayloads.Ui.TYPE))return;var tag=new CompoundTag();tag.putString("kind","state");tag.putBoolean("active",active(p));tag.putBoolean("eligible",canChoose(p));ServerPlayNetworking.send(p,new JourneyPayloads.Ui(tag));}
 public static boolean choose(ServerPlayer p){if(!canChoose(p)){sync(p);return false;}var corpse=CORPSE.create(p.serverLevel());if(corpse==null)return false;corpse.initialize(p);if(!p.serverLevel().addFreshEntity(corpse))return false;var tag=data(p);tag.putString("Corpse",corpse.getUUID().toString());tag.putBoolean("PendingJourney",true);tag.putInt("ReturnMode",p.gameMode.getGameModeForPlayer().getId());data(p,tag);p.connection.player=p.getServer().getPlayerList().respawn(p,false,Entity.RemovalReason.KILLED);return true;}
 public static void respawn(ServerPlayer old,ServerPlayer p,boolean alive){if(alive){sync(p);return;}var tag=data(p);boolean pending=tag.getBoolean("PendingJourney");boolean failed=!body(p).isEmpty()&&!pending;
  if(failed){body(p,"");p.setGameMode(GameType.SURVIVAL);DeferredDeathItems.abandonJourney(p);DeferredDeathItems.resolveChoice(p,false);sync(p);return;}
  DeferredDeathItems.resolveChoice(p,pending);var target=graveTarget(p);
  if(pending){tag=data(p);tag.putBoolean("PendingJourney",false);data(p,tag);p.setGameMode(GameType.SPECTATOR);if(target!=null)p.teleportTo(target.level,target.pos.getX()+.5,target.pos.getY()+1,target.pos.getZ()+.5,p.getYRot(),0);else{body(p,"");p.setGameMode(GameType.SURVIVAL);DeferredDeathItems.abandonJourney(p);}}
  else if(target!=null){Vec3 safe=safe(p,target.level,target.pos);if(safe!=null)p.teleportTo(target.level,safe.x,safe.y,safe.z,p.getYRot(),0);}
  if(target!=null&&p.serverLevel()==target.level&&p.distanceToSqr(target.pos.getCenter())<16)consumeRespawnGrave(p,target);
  sync(p);
 }
 private static void consumeRespawnGrave(ServerPlayer p,GraveTarget target){if(target.level.getBlockEntity(target.pos) instanceof GraveEntity grave&&grave.owned(p)&&!grave.getBlockState().getValue(CatalogBlocks.GRAVE_NATURAL)){PlayerHauntManager.addPoints(p,1);target.level.destroyBlock(target.pos,false);grave(p,"");}}
 public static Vec3 safe(ServerPlayer p,ServerLevel level,BlockPos anchor){for(int dy=0;dy<=4;dy++)for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){var at=anchor.offset(dx,dy,dz).getBottomCenter();var box=p.getBoundingBox().move(at.subtract(p.position()));if(level.hasChunkAt(BlockPos.containing(at))&&level.getWorldBorder().isWithinBounds(BlockPos.containing(at))&&level.noCollision(null,box)&&!level.getBlockState(BlockPos.containing(at).below()).getCollisionShape(level,BlockPos.containing(at).below()).isEmpty()&&level.getFluidState(BlockPos.containing(at)).isEmpty())return at;}return null;}
 public static boolean recoverAtWell(ServerPlayer p){if(!active(p))return false;var level=p.serverLevel();var well=SpiritwellIndex.get(level).nearest(level,p.position());if(well==null)return false;var at=safe(p,level,well);if(at==null)return false;String corpse=body(p);var tag=data(p);tag.putString("Corpse","");data(p,tag);p.teleportTo(level,at.x,at.y,at.z,p.getYRot(),0);var mode=GameType.byId(tag.getInt("ReturnMode"));p.setGameMode(mode==GameType.SPECTATOR?GameType.SURVIVAL:mode);p.setHealth(p.getMaxHealth());p.clearFire();p.fallDistance=0;p.setDeltaMovement(Vec3.ZERO);PlayerHauntManager.addPoints(p,10);HauntManager.addPoints(level,well,10);DeferredDeathItems.recoverHeld(p);for(var world:p.getServer().getAllLevels())try{var entity=world.getEntity(java.util.UUID.fromString(corpse));if(entity instanceof PlayerCorpseEntity body&&body.owner().filter(p.getUUID()::equals).isPresent())body.discard();}catch(IllegalArgumentException ignored){}sync(p);return true;}
 public static void initialize(){
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(CORPSE,LivingEntity.createLivingAttributes());
  PayloadTypeRegistry.playS2C().register(JourneyPayloads.Ui.TYPE,JourneyPayloads.Ui.CODEC);PayloadTypeRegistry.playC2S().register(JourneyPayloads.Action.TYPE,JourneyPayloads.Action.CODEC);
  ServerPlayNetworking.registerGlobalReceiver(JourneyPayloads.Action.TYPE,(payload,context)->context.server().execute(()->{var p=context.player();switch(payload.action()){case "query"->sync(p);case "choose"->choose(p);case "well"->{if(!recoverAtWell(p))p.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.journey.no_well"),true);}case "name"->{if(p.isAlive()&&!p.isSpectator()&&p.distanceToSqr(payload.pos().getCenter())<=64&&p.serverLevel().hasChunkAt(payload.pos())&&p.serverLevel().mayInteract(p,payload.pos())&&p.serverLevel().getBlockEntity(payload.pos()) instanceof GraveEntity grave)grave.name(p,payload.text());}default->{}}}));
  ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{if(entity instanceof ServerPlayer p)sync(p);});

  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{DeferredDeathItems.login(handler.player);sync(handler.player);});
  ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be,level)->{if(be instanceof SpiritwellBlockEntity)SpiritwellIndex.get(level).add(be.getBlockPos());});
  UseBlockCallback.EVENT.register((player,level,hand,hit)->{if(player instanceof ServerPlayer p&&level.getBlockEntity(hit.getBlockPos()) instanceof GraveEntity grave&&grave.canName(p)){var tag=new CompoundTag();tag.putString("kind","grave");tag.putLong("pos",hit.getBlockPos().asLong());ServerPlayNetworking.send(p,new JourneyPayloads.Ui(tag));return net.minecraft.world.InteractionResult.SUCCESS;}if(player instanceof ServerPlayer p&&active(p)&&level.getBlockState(hit.getBlockPos()).is(CatalogBlocks.SPIRITWELL)){var tag=new CompoundTag();tag.putString("kind","journey");ServerPlayNetworking.send(p,new JourneyPayloads.Ui(tag));return net.minecraft.world.InteractionResult.SUCCESS;}return net.minecraft.world.InteractionResult.PASS;});
 }
}
