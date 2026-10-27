package com.howierux.mortalexpansion.travel;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;
public final class GraveEntity extends BlockEntity {
 private long lastDay=Long.MIN_VALUE;private UUID spiritWorker;
 private String inscription="";private UUID namingOwner,respawnOwner;
 public GraveEntity(BlockPos pos,BlockState state){super(JourneyRuntime.GRAVE,pos,state);}
 public String inscription(){return inscription;}
 public void placed(ServerPlayer p){namingOwner=p.getUUID();setChanged();}
 public boolean canName(ServerPlayer p){return inscription.isEmpty()&&p.getUUID().equals(namingOwner)&&!getBlockState().getValue(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVE_NATURAL);}
 public boolean name(ServerPlayer p,String requested){if(!canName(p))return false;String text=requested.strip().replaceAll("[\\p{Cntrl}]","");if(text.isEmpty()||text.length()>32)return false;inscription=text;namingOwner=null;if(text.equalsIgnoreCase(p.getGameProfile().getName())){respawnOwner=p.getUUID();JourneyRuntime.grave(p,level.dimension().location()+"|"+worldPosition.getX()+"|"+worldPosition.getY()+"|"+worldPosition.getZ());}setChanged();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);return true;}
 public boolean owned(ServerPlayer p){return p.getUUID().equals(respawnOwner)&&inscription.equalsIgnoreCase(p.getGameProfile().getName());}
 protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putLong("NaturalizationDay",lastDay);if(spiritWorker!=null)tag.putUUID("SpiritWorker",spiritWorker);tag.putString("Inscription",inscription);if(namingOwner!=null)tag.putUUID("NamingOwner",namingOwner);if(respawnOwner!=null)tag.putUUID("RespawnOwner",respawnOwner);}
 protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);lastDay=tag.contains("NaturalizationDay")?tag.getLong("NaturalizationDay"):Long.MIN_VALUE;spiritWorker=tag.hasUUID("SpiritWorker")?tag.getUUID("SpiritWorker"):null;inscription=tag.getString("Inscription");namingOwner=tag.hasUUID("NamingOwner")?tag.getUUID("NamingOwner"):null;respawnOwner=tag.hasUUID("RespawnOwner")?tag.getUUID("RespawnOwner"):null;}
 public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
 public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
 public UUID getSpiritWorker(){return spiritWorker;}
 public void setSpiritWorker(UUID worker){spiritWorker=worker;setChanged();}
 public static boolean flowers(net.minecraft.server.level.ServerLevel level,BlockPos pos){for(var at:BlockPos.betweenClosed(pos.offset(-3,-2,-3),pos.offset(3,2,3)))if(Math.pow(at.getX()-pos.getX(),2)+Math.pow(at.getZ()-pos.getZ(),2)<=9&&level.hasChunkAt(at)&&level.getBlockState(at).is(net.minecraft.tags.BlockTags.FLOWERS))return true;return false;}
 public void tick(net.minecraft.server.level.ServerLevel level){com.howierux.mortalexpansion.spiritwell.GravePorting.loaded(level,worldPosition);if(level.getGameTime()%20!=0)return;com.howierux.mortalexpansion.entity.mob.SpiritWorkstations.tick(level,this);long day=Math.floorDiv(level.getDayTime(),24000);if(lastDay==Long.MIN_VALUE){lastDay=day;setChanged();return;}if(lastDay==day)return;long old=lastDay;lastDay=day;setChanged();if(day>old&&!getBlockState().getValue(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVE_NATURAL)&&com.howierux.mortalexpansion.haunt.HauntManager.getTier(level,worldPosition).id()>=2&&flowers(level,worldPosition)&&level.random.nextFloat()<.2F){level.setBlock(worldPosition,getBlockState().setValue(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVE_NATURAL,true),3);com.howierux.mortalexpansion.haunt.HauntManager.addPoints(level,worldPosition,-1);}}
}
