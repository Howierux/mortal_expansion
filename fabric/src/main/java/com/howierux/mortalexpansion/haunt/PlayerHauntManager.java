package com.howierux.mortalexpansion.haunt;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.entity.player.Player;

/** Same ordinary Haunt rules as NeoForge; Fabric owns saving, death-copying and private synchronization. */
public final class PlayerHauntManager {
    private PlayerHauntManager(){}
    public static int getHundredths(Player p){return ((AttachmentTarget)p).getAttachedOrCreate(FabricHauntData.PLAYER);}
    public static int getValue(Player p){return getHundredths(p)/100;}
    public static double getPreciseValue(Player p){return getHundredths(p)/100.0;}
    public static HauntTier getTier(Player p){return HauntTier.fromValue(getValue(p));}
    public static int setValue(Player p,int value){int clamped=HauntTier.clampValue(value);((AttachmentTarget)p).setAttached(FabricHauntData.PLAYER,clamped*100);return clamped;}
    public static HauntTier setTier(Player p,HauntTier tier){setValue(p,tier.midpointValue());return tier;}
    public static void addHundredths(Player p,long amount){((AttachmentTarget)p).setAttached(FabricHauntData.PLAYER,FractionalHaunt.add(getHundredths(p),amount));}
    public static int addPoints(Player p,int amount){addHundredths(p,(long)amount*100);return getValue(p);}
    // missing function: Darkhold awakening and above-Eldritch overflow await the book/personal-Haunt progression port.
}
