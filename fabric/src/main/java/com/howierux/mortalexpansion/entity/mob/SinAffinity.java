package com.howierux.mortalexpansion.entity.mob;
import net.minecraft.util.StringRepresentable;
/** Stable saved identities; network indices are explicit rather than enum ordinals. */
public enum SinAffinity implements StringRepresentable {
    PRIDE(0,"pride",0xFF7461DA), ENVY(1,"envy",0xFF55CE68), GLUTTONY(2,"gluttony",0xFFF39A38),
    WRATH(3,"wrath",0xFFEC4D4D), SLOTH(4,"sloth",0xFF5899E7), GREED(5,"greed",0xFFF0D34A), LUST(6,"lust",0xFFC06FE4);
    private final int index,color;private final String id;
    SinAffinity(int index,String id,int color){this.index=index;this.id=id;this.color=color;}
    public int index(){return index;}
    public int color(){return color;}
    @Override public String getSerializedName(){return id;}
    public static SinAffinity fromIndex(int index){for(var sin:values())if(sin.index==index)return sin;return PRIDE;}
    public static SinAffinity fromId(String id){for(var sin:values())if(sin.id.equals(id))return sin;return PRIDE;}
    /** Preserve the old red/violet/blue/orange color identities when reading pre-affinity Demons. */
    public static SinAffinity fromLegacyVariant(int variant){return switch(variant){case 1->LUST;case 2->SLOTH;case 3->GLUTTONY;default->WRATH;};}
}
