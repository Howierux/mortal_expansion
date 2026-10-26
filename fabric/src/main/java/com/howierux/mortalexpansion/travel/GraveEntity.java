package com.howierux.mortalexpansion.travel;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;
public final class GraveEntity extends BlockEntity {
 private String inscription="";private UUID namingOwner,respawnOwner;
 public GraveEntity(BlockPos pos,BlockState state){super(JourneyRuntime.GRAVE,pos,state);}
 public String inscription(){return inscription;}
 public void placed(ServerPlayer p){namingOwner=p.getUUID();setChanged();}
 public boolean canName(ServerPlayer p){return inscription.isEmpty()&&p.getUUID().equals(namingOwner)&&!getBlockState().getValue(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVE_NATURAL);}
 public boolean name(ServerPlayer p,String requested){if(!canName(p))return false;String text=requested.strip().replaceAll("[\\p{Cntrl}]","");if(text.isEmpty()||text.length()>32)return false;inscription=text;namingOwner=null;if(text.equalsIgnoreCase(p.getGameProfile().getName())){respawnOwner=p.getUUID();JourneyRuntime.grave(p,level.dimension().location()+"|"+worldPosition.getX()+"|"+worldPosition.getY()+"|"+worldPosition.getZ());}setChanged();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);return true;}
 public boolean owned(ServerPlayer p){return p.getUUID().equals(respawnOwner)&&inscription.equalsIgnoreCase(p.getGameProfile().getName());}
 protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putString("Inscription",inscription);if(namingOwner!=null)tag.putUUID("NamingOwner",namingOwner);if(respawnOwner!=null)tag.putUUID("RespawnOwner",respawnOwner);}
 protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);inscription=tag.getString("Inscription");namingOwner=tag.hasUUID("NamingOwner")?tag.getUUID("NamingOwner"):null;respawnOwner=tag.hasUUID("RespawnOwner")?tag.getUUID("RespawnOwner"):null;}
 public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
 public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
