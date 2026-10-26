package com.howierux.mortalexpansion.content;
import net.minecraft.world.entity.EntityType;
public final class VanillaHauntSpawns {
 public static float acceptance(EntityType<?> type,int tier){if(type==EntityType.CREEPER)return .85F;if(type==EntityType.ZOMBIE||type==EntityType.HUSK||type==EntityType.DROWNED)return tier<1?0:tier==1?.17F:.85F;if(type==EntityType.SKELETON||type==EntityType.STRAY||type==EntityType.BOGGED)return tier<2?0:.85F;if(type==EntityType.WITCH)return tier<2?0:1;if(type==EntityType.WITHER_SKELETON)return tier<4?0:tier==4?.25F:tier==5?.5F:1;if(type==EntityType.GHAST)return tier<5?0:1;if(type==EntityType.ENDERMAN)return tier<6?.25F:1;return 1;}
}
