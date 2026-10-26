package com.howierux.mortalexpansion.discovery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

/** Stable identity and named destination metadata, independent of eventual travel rules. */
public record SpectralSite(String kind, String name, ResourceLocation dimension, BlockPos anchor, BoundingBox bounds) {
    public static final Codec<SpectralSite> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("kind").forGetter(SpectralSite::kind), Codec.STRING.fieldOf("name").forGetter(SpectralSite::name),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(SpectralSite::dimension),
            BlockPos.CODEC.fieldOf("anchor").forGetter(SpectralSite::anchor), BoundingBox.CODEC.fieldOf("bounds").forGetter(SpectralSite::bounds)
    ).apply(i, SpectralSite::new));
    public String id() {
        return dimension + "/" + kind + "/" + (kind.equals("spiritwell") ? anchor.asLong() : bounds.minX()+","+bounds.minZ());
    }
    public double distanceSquared(Vec3 from) {
        double x = Math.max(bounds.minX(), Math.min(bounds.maxX()+1.0, from.x));
        double z = Math.max(bounds.minZ(), Math.min(bounds.maxZ()+1.0, from.z));
        double y = kind.equals("spiritwell") ? anchor.getY()+0.5 : anchor.getY();
        if (kind.equals("spiritwell")) { x=anchor.getX()+0.5; z=anchor.getZ()+0.5; }
        return from.distanceToSqr(x,y,z);
    }
    public static SpectralSite create(String kind, ResourceLocation dimension, BlockPos anchor, BoundingBox bounds, long seed) {
        String key = dimension+"/"+kind+"/"+bounds.minX()+","+bounds.minZ()+","+(kind.equals("spiritwell")?anchor.getY():0);
        var random = new java.util.Random(seed ^ key.hashCode());
        String[] cemeteries={"Blackwell","Ravenwood","Hollow Creek","Grim Hollow","Mourningwood","Ashcroft","Dreadmoor","Briar Hollow","Nightshade","Ravenscroft","Silent Hill","Wraithwood"};
        String[] wells={"Whispering","Veiled","Pale","Forgotten","Moonlit","Hushed","Wandering","Silver"};
        String[] places={"Hollow","Reach","Rest","Crossing","Echo","Glen","Watch","Refuge"};
        String name = kind.equals("cemetery") ? cemeteries[random.nextInt(cemeteries.length)]+" Cemetery"
                : wells[random.nextInt(wells.length)]+" "+places[random.nextInt(places.length)]+" Spiritwell";
        return new SpectralSite(kind,name,dimension,anchor.immutable(),bounds);
    }
}
