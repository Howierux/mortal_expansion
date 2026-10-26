package com.howierux.mortalexpansion.haunt;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.entity.player.Player;

/** Same ordinary Haunt rules as NeoForge; Fabric owns saving, death-copying and private synchronization. */
public final class PlayerHauntManager {
    public static final int EXTENDED_MAX=20_000_000,MAX_PERSONAL_UNITS=EXTENDED_MAX*100;
    private PlayerHauntManager(){}
    private static int limit(Player p){return p.level() instanceof net.minecraft.server.level.ServerLevel level&&com.howierux.mortalexpansion.guide.DarkholdWorldData.get(level).awakened()?EXTENDED_MAX:HauntTier.MAX_VALUE;}
    public static int getHundredths(Player p){return ((AttachmentTarget)p).getAttachedOrCreate(FabricHauntData.PLAYER);}
    public static int getValue(Player p){return getHundredths(p)/100;}
    public static double getPreciseValue(Player p){return getHundredths(p)/100.0;}
    public static HauntTier getTier(Player p){return HauntTier.fromValue(getValue(p));}
    public static int setValue(Player p,int value){int clamped=Math.clamp(value,0,limit(p));((AttachmentTarget)p).setAttached(FabricHauntData.PLAYER,clamped*100);return clamped;}
    public static HauntTier setTier(Player p,HauntTier tier){setValue(p,tier.midpointValue());return tier;}
    public static void addHundredths(Player p,long amount){((AttachmentTarget)p).setAttached(FabricHauntData.PLAYER,(int)Math.clamp((long)getHundredths(p)+Math.clamp(amount,-MAX_PERSONAL_UNITS,MAX_PERSONAL_UNITS),0,(long)limit(p)*100));}
    public static int addPoints(Player p,int amount){addHundredths(p,(long)amount*100);return getValue(p);}

}
