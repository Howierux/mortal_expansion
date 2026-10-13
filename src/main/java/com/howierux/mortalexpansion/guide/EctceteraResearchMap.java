package com.howierux.mortalexpansion.guide;
import java.util.*;
/** Presentation relationships only. Chapter/progression logic remains the sole access authority. */
public final class EctceteraResearchMap {
    public record Node(EctceteraGuide.Chapter chapter,String parent,int x,int y){}
    private static final Map<String,String> LINKS=Map.ofEntries(
        Map.entry("mortality_research_station",""),Map.entry("altar_of_impurity","insight"),Map.entry("altar_of_purity","altar_of_impurity"),Map.entry("haunt","first_steps"),Map.entry("personal","haunt"),Map.entry("biomes","haunt"),Map.entry("haunt_events","personal"),Map.entry("unlock_bone","haunt_events"),
        Map.entry("intangibles","graves"),Map.entry("creatures","intangibles"),Map.entry("spiritwells","graves"),Map.entry("unlock_raw","creatures"),
        Map.entry("cauldrons","ectoplasm"),Map.entry("salt","cauldrons"),Map.entry("emf_reader","salt"),Map.entry("torches","ectoplasm"),Map.entry("insight","ectoplasm"),
        Map.entry("dreamcatcher","ectothread"),Map.entry("ectothread","insight"),Map.entry("equipment","ectoplasm"),Map.entry("ghost_garb","ectothread"),Map.entry("furnaces","salt"),Map.entry("unlock_pure","cauldrons"),
        Map.entry("wards","sanctified"),Map.entry("ectchantment","sanctified"),Map.entry("generators","ectchantment"),Map.entry("station","ectchantment"),Map.entry("scepter","station"),Map.entry("mastery","wards"));
    private EctceteraResearchMap(){}
    public static List<Node> nodes(int category){
        var chapters=EctceteraGuide.CHAPTERS.stream().filter(ch->category==4
            ? ch.key().equals("insight") || secret(ch.key())!=0
            : ch.tier()==category && !ch.key().equals("insight") && secret(ch.key())==0)
            .map(ch->ch.key().equals("haint_garb")?new EctceteraGuide.Chapter(ch.key(),2,ch.tier()):ch).toList();
        if(category==4){var grouped=new ArrayList<>(chapters);grouped.add(0,new EctceteraGuide.Chapter("mortality_research_station",1,0));grouped.add(new EctceteraGuide.Chapter("dreamcatcher",1,2));grouped.add(new EctceteraGuide.Chapter("spectralite_haint",1,3));grouped.add(new EctceteraGuide.Chapter("altar_of_impurity",1,2));grouped.add(new EctceteraGuide.Chapter("altar_of_purity",1,3));chapters=List.copyOf(grouped);}
        var nodes=new ArrayList<Node>();
        for(int i=0;i<chapters.size();i++)nodes.add(new Node(chapters.get(i),chapters.get(i).key().equals("haint_garb")?"ghost_garb":chapters.get(i).key().equals("spectralite_haint")?"haint_garb":LINKS.getOrDefault(chapters.get(i).key(),""),45+(i%3)*125,40+(i/3)*90));
        return List.copyOf(nodes);
    }
    public static boolean unlocked(String key,int tier,int secrets){if(key.equals("dreamcatcher"))return tier>=2 && (secrets&1)!=0;if(key.equals("mortality_research_station"))return (secrets&64)!=0;if(key.equals("altar_of_impurity"))return tier>=2 && (secrets&16)!=0;if(key.equals("altar_of_purity"))return tier>=3 && (secrets&32)!=0;if(key.equals("spectralite_haint"))return tier>=3 && (secrets&8)!=0;return EctceteraGuide.chapters(Math.clamp(tier,0,3),secrets).stream().anyMatch(ch->ch.key().equals(key));}
    public static int secret(String key){return switch(key){case "mortality_research_station"->64;case "ectothread","dreamcatcher"->1;case "ghost_garb"->2;case "haint_garb"->4;case "spectralite_haint"->8;case "altar_of_impurity"->16;case "altar_of_purity"->32;default->0;};}
}
