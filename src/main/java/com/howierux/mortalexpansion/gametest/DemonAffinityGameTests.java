package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.mob.*;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DemonAffinityGameTests {
    @GameTest(template="deferred_items_test")
    public static void seven_sin_identities_persist_and_legacy_colors_migrate(GameTestHelper h){
        var level=h.getLevel();var ids=new java.util.HashSet<String>();var colors=new java.util.HashSet<Integer>();
        for(var sin:SinAffinity.values()){
            var demon=ModEntities.DEMON.get().create(level);demon.setSinAffinity(sin);
            var saved=demon.saveWithoutId(new CompoundTag());
            h.assertTrue(saved.getString("SinAffinity").equals(sin.getSerializedName()),"Stable named affinity is written instead of an ordinal");
            var restored=ModEntities.DEMON.get().create(level);restored.load(saved);
            h.assertTrue(restored.sinAffinity()==sin && restored.variant()==sin.index() && restored.tint()==sin.color(),"Each affinity retains its network variant and color after reload");
            h.assertTrue(SinAffinity.fromIndex(sin.index())==sin && SinAffinity.fromId(sin.getSerializedName())==sin,"All seven synchronized IDs resolve to their saved identity");
            ids.add(sin.getSerializedName());colors.add(sin.color());
        }
        h.assertTrue(ids.size()==7 && colors.size()==7,"There are seven distinct sin identities and color mappings");
        var expected=new SinAffinity[]{SinAffinity.WRATH,SinAffinity.LUST,SinAffinity.SLOTH,SinAffinity.GLUTTONY};
        for(int old=0;old<4;old++){
            var tag=new CompoundTag();tag.putInt("DemonVariant",old);
            var demon=ModEntities.DEMON.get().create(level);demon.readAdditionalSaveData(tag);
            h.assertTrue(demon.sinAffinity()==expected[old],"Old red/violet/blue/orange Demon maps to its matching sin");
            var saved=demon.saveWithoutId(new CompoundTag());var restored=ModEntities.DEMON.get().create(level);restored.load(saved);
            h.assertTrue(restored.sinAffinity()==expected[old],"Migrated identity remains stable on subsequent saves");
        }
        h.assertTrue(SinAffinity.fromIndex(999)==SinAffinity.PRIDE && SinAffinity.fromId("unknown")==SinAffinity.PRIDE,"Unknown future/invalid identifiers have a safe deterministic fallback");
        h.succeed();
    }
}
