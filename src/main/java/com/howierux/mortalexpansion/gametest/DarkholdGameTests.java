package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.neoforged.neoforge.gametest.*;
import io.netty.buffer.Unpooled;
import java.util.List;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DarkholdGameTests {
    @GameTest(template="deferred_items_test",batch="darkhold")
    public static void final_craft_unlocks_dead_secrets_but_damned_wait_for_seventy(GameTestHelper h){
        var level=h.getLevel();var previous=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());
        var p=h.makeMockServerPlayerInLevel();
        try{
            level.getDataStorage().set(DarkholdWorldData.KEY,new DarkholdWorldData());
            var menu=p.inventoryMenu;
            menu.getSlot(1).set(EctceteraProgression.book(3));menu.getSlot(4).set(new ItemStack(ModItems.RITUAL_STAR.get()));
            menu.slotsChanged(menu.getSlot(1).container);
            h.assertFalse(DarkholdWorldData.get(level).awakened(),"Previewing the recipe must not awaken the world");
            var book=menu.getSlot(0).remove(1);menu.getSlot(0).onTake(p,book);
            h.assertTrue(EctceteraProgression.tier(book)==4 && menu.getSlot(1).getItem().isEmpty() && menu.getSlot(4).getItem().isEmpty(),"Purified book and one Ritual Star make the final tier");
            h.assertTrue(DarkholdWorldData.get(level).awakened() && p.getData(ModAttachments.ECTCETERA_KNOWLEDGE)==127,"Taking crafted output awakens the world and teaches every normal secret");
            h.assertTrue(SpectralInsight.secrets(book)==127 && !EctceteraResearchMap.unlocked("demons",4,127),"Normal mastery cannot prematurely reveal the first damned secret");
            h.assertTrue(!EctceteraResearchMap.unlocked("darkhold",3,255) && EctceteraResearchMap.unlocked("darkhold",4,127),"Damned chapter requires the final upgrade even with copied knowledge");
            PlayerHauntManager.setValue(p,70);DarkholdProgression.absorb(p,book);
            h.assertTrue(book.getOrDefault(ModDataComponents.DAMNED_SECRETS.get(),0)==3 && EctceteraResearchMap.unlocked("demons",4,255),"Entering 70 reveals the first damned secret");
            h.assertTrue(EctceteraProgression.tier(new ItemStack(ModItems.BOOK_OF_ECTCETERA.get()))==3,"Legacy books never become Darkholds by migration");
        }finally{p.discard();level.getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(previous,level.registryAccess()));}
        h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="darkhold")
    public static void nested_carriers_gain_once_per_interval_and_overflow_persists(GameTestHelper h){
        var level=h.getLevel();var previous=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());
        var p=h.makeMockServerPlayerInLevel();
        try{
            var book=EctceteraProgression.book(4);
            var inner=new ItemStack(Items.BUNDLE);inner.set(DataComponents.BUNDLE_CONTENTS,new BundleContents(List.of(book)));
            var outer=new ItemStack(Items.SHULKER_BOX);outer.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(List.of(inner)));
            p.getInventory().setItem(0,outer);
            h.assertTrue(DarkholdProgression.contains(outer),"Native container and bundle nesting is recognized without opening the backpack");
            DarkholdProgression.pulse(p,1000);PlayerHauntManager.setValue(p,69);PlayerHauntManager.addHundredths(p,99);
            h.assertFalse(DarkholdProgression.pulse(p,1199),"No early exposure pulse");
            h.assertTrue(DarkholdProgression.pulse(p,1200) && PlayerHauntManager.getHundredths(p)==7000,"One hundredth crosses the old ceiling at ten seconds");
            p.getInventory().setItem(1,book.copy());p.getInventory().setItem(2,book.copy());
            DarkholdProgression.pulse(p,1400);
            h.assertTrue(PlayerHauntManager.getHundredths(p)==7001,"Multiple nested and direct copies do not stack exposure");
            PlayerHauntManager.setValue(p,79);PlayerHauntManager.addHundredths(p,99);DarkholdProgression.pulse(p,1600);
            h.assertTrue(PlayerHauntManager.getHundredths(p)==8000,"Exposure continues beyond 79");
            var saved=p.saveWithoutId(new CompoundTag());PlayerHauntManager.setValue(p,0);p.load(saved);
            h.assertTrue(PlayerHauntManager.getHundredths(p)==8000 && p.getData(ModAttachments.DAMNED_KNOWLEDGE)==7,"Overflow and revelation survive player saving");
            p.getInventory().clearContent();h.assertFalse(DarkholdProgression.pulse(p,1800),"Removing all copies stops exposure");
            h.assertTrue(PlayerHauntManager.getHundredths(p)==8000,"Removing the book does not erase accrued Haunt");
        }finally{p.discard();level.getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(previous,level.registryAccess()));}
        h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="darkhold")
    public static void system_crafting_unlocks_demons_only_in_demonic_chunks(GameTestHelper h){
        var level=h.getLevel();var previous=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());
        var pos=h.absolutePos(new BlockPos(4,2,4));var chunk=level.getChunkAt(pos);int units=HauntManager.getHundredths(level,chunk.getPos());
        try{
            level.getDataStorage().set(DarkholdWorldData.KEY,new DarkholdWorldData());HauntManager.setValue(level,chunk.getPos(),45);
            h.assertFalse(HauntSpawnHandler.canSpawnDemon(level,pos),"High Haunt cannot summon natural Demons before the first Darkhold");
            EctceteraProgression.book(4).onCraftedBySystem(level);
            h.assertTrue(HauntSpawnHandler.canSpawnDemon(level,pos),"Native automated crafting awakens Demons without a player owner");
            HauntManager.setValue(level,chunk.getPos(),39);h.assertFalse(HauntSpawnHandler.canSpawnDemon(level,pos),"Dead Rising remains below Demon requirement");
            HauntManager.setValue(level,chunk.getPos(),40);h.assertTrue(HauntSpawnHandler.canSpawnDemon(level,pos),"Demonic minimum is inclusive");
            var persisted=DarkholdWorldData.load(DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess()),level.registryAccess());
            h.assertTrue(persisted.awakened(),"First crafting flag survives world persistence");
            var demon=ModEntities.DEMON.get().create(level);var ghost=ModEntities.GHOST.get().create(level);
            h.assertTrue(demon.getMaxHealth()>ghost.getMaxHealth() && demon.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)>ghost.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE),"Demons are stronger Ghost-like hunters");
            h.assertTrue(demon.getType().is(com.howierux.mortalexpansion.entity.ModEntityTags.INTANGIBLE),"Demons use shared intangibility defenses");
            var saved=demon.saveWithoutId(new CompoundTag());var restored=ModEntities.DEMON.get().create(level);restored.load(saved);
            h.assertTrue(demon.variant()==restored.variant(),"Demon color survives unloading");
        }finally{HauntManager.setValue(level,chunk.getPos(),0);HauntManager.addHundredths(level,chunk.getPos(),units);level.getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(previous,level.registryAccess()));}
        h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="darkhold")
    public static void extended_emf_and_darkhold_components_survive_network_roundtrip(GameTestHelper h){
        int[] values={69,70,80,90,100,110,120,200};int[] stages={6,7,8,9,10,11,12,12};
        for(int i=0;i<values.length;i++)h.assertTrue(EmfScale.modelLevel(values[i])==stages[i] && EmfScale.litBulbs(values[i])==6,"Overflow bands replace colors rather than adding bulbs");
        h.assertTrue(EmfScale.bulbColor(0,70)==0xFFFF2020 && EmfScale.bulbColor(3,100)==0xFF510080 && EmfScale.bulbColor(1,70)==EmfScale.bulbColor(1),"First three overflow bulbs are red, latter three purple, others retain their normal colors");
        var reading=new EmfReading(h.getLevel().dimension().location(),0,0,java.util.UUID.randomUUID(),20075,20075);
        var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),h.getLevel().registryAccess());
        try{
            EmfReading.STREAM_CODEC.encode(buffer,reading);var restored=EmfReading.STREAM_CODEC.decode(buffer);
            h.assertTrue(restored.chunkHundredths()==6900 && restored.personalHundredths()==20075 && EmfScale.decimal(restored.personalHundredths()).equals("200.75"),"Precise personal reading extends without raising chunk capacity");
            var book=EctceteraProgression.book(4);book.set(ModDataComponents.DAMNED_SECRETS.get(),1);
            ItemStack.STREAM_CODEC.encode(buffer,book);var restoredBook=ItemStack.STREAM_CODEC.decode(buffer);
            h.assertTrue(EctceteraProgression.tier(restoredBook)==4 && restoredBook.getOrDefault(ModDataComponents.DAMNED_SECRETS.get(),0)==1,"Final tier and damned secret synchronize to the book screen");
        }finally{buffer.release();}
        h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="darkhold")
    public static void demon_vision_is_private_revealed_knowledge_and_survives_book_removal(GameTestHelper h){
        var level=h.getLevel();var previous=DarkholdWorldData.get(level).save(new CompoundTag(),level.registryAccess());
        var reader=h.makeMockServerPlayerInLevel();var stranger=h.makeMockServerPlayerInLevel();
        try{
            h.assertFalse(DarkholdProgression.canSeeDemon(reader) || DarkholdProgression.canSeeDemon(stranger),"Uninitiated viewers cannot see Demons");
            var book=EctceteraProgression.book(4);DarkholdProgression.absorb(reader,book);
            h.assertFalse(DarkholdProgression.canSeeDemon(reader),"Owning a Darkhold and knowing all normal secrets does not reveal Demons");
            PlayerHauntManager.setValue(stranger,70);
            h.assertFalse(DarkholdProgression.canSeeDemon(stranger),"High Haunt without the Darkhold revelation grants no vision");
            PlayerHauntManager.setValue(reader,70);DarkholdProgression.absorb(reader,book);
            h.assertTrue(DarkholdProgression.canSeeDemon(reader) && !DarkholdProgression.canSeeDemon(stranger),"Revelation changes only its recipient's sight");
            reader.getInventory().clearContent();PlayerHauntManager.setValue(reader,15);
            var saved=reader.saveWithoutId(new CompoundTag());reader.setData(ModAttachments.DAMNED_KNOWLEDGE,0);reader.load(saved);
            h.assertTrue(DarkholdProgression.canSeeDemon(reader),"Learned vision persists without the book and after lowering Haunt or reloading");
            var buffer=Unpooled.buffer();
            try{
                var codec=com.howierux.mortalexpansion.network.DarkholdKnowledgePayload.CODEC;
                codec.encode(buffer,new com.howierux.mortalexpansion.network.DarkholdKnowledgePayload(1));
                h.assertTrue(codec.decode(buffer).secrets()==1,"Owner-only vision knowledge survives packet serialization");
                codec.encode(buffer,new com.howierux.mortalexpansion.network.DarkholdKnowledgePayload(0));
                h.assertTrue(codec.decode(buffer).secrets()==0,"Login/respawn can explicitly reset an uninitiated viewer's state");
            }finally{buffer.release();}
        }finally{reader.discard();stranger.discard();level.getDataStorage().set(DarkholdWorldData.KEY,DarkholdWorldData.load(previous,level.registryAccess()));}
        h.succeed();
    }

}
