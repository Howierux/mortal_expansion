package com.howierux.mortalexpansion.guide;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

/** Ordered secret families; thresholds are independent of recipe ingredient availability. */
public enum InsightSecret {
    ECTOTHREAD(1,2,2,0), GHOST_GARB(2,2,3,1), HAINT_GARB(4,2,4,3), SPECTRALITE_HAINT(8,3,5,7), ALTAR_OF_IMPURITY(16,2,3,0), ALTAR_OF_PURITY(32,3,4,16);
    public final int bit,bookTier,personalTier,prerequisites;
    InsightSecret(int bit,int bookTier,int personalTier,int prerequisites){this.bit=bit;this.bookTier=bookTier;this.personalTier=personalTier;this.prerequisites=prerequisites;}
    public String key(){return name().toLowerCase(java.util.Locale.ROOT);}
    public List<ResourceLocation> recipes(){
        List<String> paths=new ArrayList<>();
        switch(this){
            case ALTAR_OF_IMPURITY -> paths.add("altar_of_impurity");
            case ALTAR_OF_PURITY -> paths.add("altar_of_purity");
            case ECTOTHREAD -> paths.add("ectothread");
            case GHOST_GARB -> {for(String part:parts())paths.add("ghost_garb_"+part);}
            case HAINT_GARB -> {paths.add("purified_ectothread");for(String part:parts()){paths.add("haint_garb_"+part);paths.add("haint_garb_"+part+"_smithing");}}
            case SPECTRALITE_HAINT -> {for(String part:parts())paths.add("spectralite_haint_"+part+"_smithing");}
        }
        return paths.stream().map(p->ResourceLocation.fromNamespaceAndPath("mortal_expansion",p)).toList();
    }
    private static String[] parts(){return new String[]{"helmet","chestplate","leggings","boots"};}
    public static int required(ResourceLocation id){for(var secret:values())if(secret.recipes().contains(id))return secret.bit;return 0;}
}
