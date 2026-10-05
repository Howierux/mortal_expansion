package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.discovery.*;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpectralDiscoveryGameTests {
    private static SpectralSite well(GameTestHelper h,BlockPos pos) {
        return SpectralSite.create("spiritwell",h.getLevel().dimension().location(),pos,new BoundingBox(pos),h.getLevel().getSeed());
    }
    @GameTest(template="deferred_items_test")
    public static void exact_twenty_block_discovery_is_personal_and_persistent(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var other=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(7,2,7));var site=well(h,pos);
        p.setPos(pos.getCenter().add(20.01,0,0));h.assertFalse(SpectralDiscovery.discover(p,site),"Outside twenty metres is undiscovered");
        p.setPos(pos.getCenter().add(20,0,0));h.assertTrue(SpectralDiscovery.discover(p,site),"Exactly twenty metres discovers");
        h.assertFalse(SpectralDiscovery.discover(p,site),"No duplicate record or discovery notice");
        h.assertFalse(SpectralDiscovery.known(other,site),"One player's discovery does not unlock another's");
        var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());p.setData(ModAttachments.DISCOVERED_SITES,java.util.List.of());p.load(saved);
        h.assertTrue(SpectralDiscovery.registeredDestination(p,site.id()).name().equals(site.name()),"Named travel hook survives save/load");
        p.setPos(pos.getCenter().add(0,20.01,0));h.assertTrue(site.distanceSquared(p.position())>400,"Vertical distance is respected");
        p.discard();other.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void cemetery_bounds_names_and_dimension_identity_are_stable(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(7,2,7));
        var box=new BoundingBox(pos.getX()-28,pos.getY(),pos.getZ()-28,pos.getX()+27,pos.getY(),pos.getZ()+27);
        var site=SpectralSite.create("cemetery",h.getLevel().dimension().location(),pos,box,h.getLevel().getSeed());
        p.setPos(box.maxX()+21.0,pos.getY(),pos.getZ());h.assertTrue(SpectralDiscovery.discover(p,site),"Cemetery uses its boundary, not its centre");
        var same=SpectralSite.create("cemetery",site.dimension(),pos,box,h.getLevel().getSeed());
        h.assertTrue(same.id().equals(site.id()) && same.name().equals(site.name()) && site.name().endsWith(" Cemetery"),"Same site has stable horror-themed naming");
        var elsewhere=SpectralSite.create("cemetery",net.minecraft.resources.ResourceLocation.withDefaultNamespace("the_nether"),pos,box,h.getLevel().getSeed());
        h.assertFalse(SpectralDiscovery.discover(p,elsewhere),"Other dimensions cannot be discovered remotely");
        h.assertFalse(elsewhere.id().equals(site.id()),"Identical coordinates in different dimensions remain distinct");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void nearest_excludes_discoveries_and_index_roundtrips(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var pos=h.absolutePos(new BlockPos(7,2,7));p.setPos(pos.getCenter());
        var index=new SpectralSiteIndex();var first=well(h,pos);var second=well(h,pos.offset(40,0,0));index.add(first);index.add(second);
        var restored=SpectralSiteIndex.load(index.save(new net.minecraft.nbt.CompoundTag(),h.getLevel().registryAccess()),h.getLevel().registryAccess());
        h.assertTrue(restored.find(first.id()).name().equals(first.name()) && restored.sites().size()==2,"Index retains positions and names");
        var live=SpectralSiteIndex.get(h.getLevel());live.add(first);live.add(second);
        p.setData(ModAttachments.DISCOVERED_SITES,live.sites().stream().filter(s->!s.id().equals(first.id()) && !s.id().equals(second.id())).toList());
        h.assertTrue(SpectralDiscovery.nearestUndiscovered(p).id().equals(first.id()),"Nearest undiscovered is chosen");
        SpectralDiscovery.discover(p,first);h.assertTrue(SpectralDiscovery.nearestUndiscovered(p).id().equals(second.id()),"Discovered sites are excluded");
        h.assertTrue(SpectralDiscovery.registeredDestination(p,second.id())==null,"Unvisited sites are not registered travel destinations");
        live.remove(first.id());live.remove(second.id());p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void footprint_payload_roundtrip_and_existing_well_indexing(GameTestHelper h) {
        var pos=h.absolutePos(new BlockPos(7,2,7));h.setBlock(new BlockPos(7,2,7),com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWELL.get());
        com.howierux.mortalexpansion.spiritwell.SpiritwellIndex.get(h.getLevel()).add(pos);SpectralDiscovery.refresh(h.getLevel());
        h.assertTrue(SpectralSiteIndex.get(h.getLevel()).find(well(h,pos).id())!=null,"Existing well positions migrate into named sites");
        var buf=io.netty.buffer.Unpooled.buffer();try{
            var payload=new com.howierux.mortalexpansion.network.SpectralTracksPayload(true,pos);
            com.howierux.mortalexpansion.network.SpectralTracksPayload.CODEC.encode(buf,payload);
            h.assertTrue(com.howierux.mortalexpansion.network.SpectralTracksPayload.CODEC.decode(buf).equals(payload),"Footprint target synchronizes exactly");
        }finally{buf.release();}h.setBlock(new BlockPos(7,2,7),net.minecraft.world.level.block.Blocks.AIR);SpectralDiscovery.refresh(h.getLevel());
        h.assertTrue(SpectralSiteIndex.get(h.getLevel()).find(well(h,pos).id())==null,"Removed loaded wells no longer attract tracks");
        h.assertFalse(com.howierux.mortalexpansion.spiritwell.SpiritwellIndex.get(h.getLevel()).knownPositions().contains(pos),"Removed wells cannot reappear in the site registry after chunk unload");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void loaded_cemetery_structure_metadata_registers_a_named_destination(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var origin=h.absolutePos(new BlockPos(7,2,7));p.setPos(origin.getCenter());
        var definition=h.getLevel().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE)
                .get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"graveyard_cemetery"));
        var chunk=h.getLevel().getChunkAt(origin);var original=new java.util.HashMap<>(chunk.getAllStarts());
        var piece=new com.howierux.mortalexpansion.worldgen.structure.GraveyardCemeteryPiece(origin.getX(),origin.getZ());
        var start=new net.minecraft.world.level.levelgen.structure.StructureStart(definition,chunk.getPos(),0,
                new net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer(java.util.List.of(piece)));
        int cx=origin.getX()+27,cz=origin.getZ()+27;
        h.getLevel().getChunk(cx>>4,cz>>4); // Explicit test fixture, not a runtime locate operation.
        String id=null;
        try {
            chunk.setStartForStructure(definition,start);SpectralDiscovery.refresh(h.getLevel());
            var site=SpectralSiteIndex.get(h.getLevel()).sites().stream().filter(v->v.kind().equals("cemetery")
                    && v.bounds().minX()==origin.getX() && v.bounds().minZ()==origin.getZ()).findFirst().orElseThrow();id=site.id();
            h.assertTrue(site.anchor().getX()==cx && site.anchor().getZ()==cz && site.name().endsWith(" Cemetery"),"Loaded real Cemetery structure metadata creates a stable named anchor");
        } finally {
            chunk.setAllStarts(original);if(id!=null)SpectralSiteIndex.get(h.getLevel()).remove(id);p.discard();
        }
        h.succeed();
    }
}


