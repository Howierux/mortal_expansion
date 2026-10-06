package com.howierux.mortalexpansion.guide;
import java.util.*;
/** Presentation relationships only. Chapter/progression logic remains the sole access authority. */
public final class EctceteraResearchMap {
    public record Node(EctceteraGuide.Chapter chapter,String parent,int x,int y){}
    private static final Map<String,String> LINKS=Map.ofEntries(
        Map.entry("haunt","first_steps"),Map.entry("personal","haunt"),Map.entry("biomes","haunt"),Map.entry("haunt_events","personal"),Map.entry("unlock_bone","haunt_events"),
        Map.entry("intangibles","graves"),Map.entry("creatures","intangibles"),Map.entry("spiritwells","graves"),Map.entry("unlock_raw","creatures"),
        Map.entry("cauldrons","ectoplasm"),Map.entry("salt","cauldrons"),Map.entry("emf_reader","salt"),Map.entry("torches","ectoplasm"),Map.entry("insight","ectoplasm"),
        Map.entry("ectothread","insight"),Map.entry("equipment","ectoplasm"),Map.entry("ghost_garb","ectothread"),Map.entry("furnaces","salt"),Map.entry("unlock_pure","cauldrons"),
        Map.entry("wards","sanctified"),Map.entry("ectchantment","sanctified"),Map.entry("generators","ectchantment"),Map.entry("station","ectchantment"),Map.entry("scepter","station"),Map.entry("mastery","wards"));
    private EctceteraResearchMap(){}
    public static List<Node> nodes(int category){
        var chapters=EctceteraGuide.CHAPTERS.stream().filter(ch->ch.tier()==category).toList();var nodes=new ArrayList<Node>();
        for(int i=0;i<chapters.size();i++)nodes.add(new Node(chapters.get(i),LINKS.getOrDefault(chapters.get(i).key(),""),45+(i%3)*125,40+(i/3)*90));
        return List.copyOf(nodes);
    }
    public static boolean unlocked(String key,int tier,int secrets){return EctceteraGuide.chapters(Math.clamp(tier,0,3),secrets).stream().anyMatch(ch->ch.key().equals(key));}
    public static int secret(String key){return switch(key){case "ectothread"->1;case "ghost_garb"->2;case "haint_garb"->4;default->0;};}
}
