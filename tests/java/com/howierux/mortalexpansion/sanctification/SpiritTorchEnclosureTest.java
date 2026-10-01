package com.howierux.mortalexpansion.sanctification;

import com.howierux.mortalexpansion.entity.mob.SpiritTorchLinks;
import java.util.*;

public final class SpiritTorchEnclosureTest {
    private static long key(int x, int z) { return SaltEnclosure.key(x, z); }
    private static Set<Long> rectangle(int size) {
        return new HashSet<>(Set.of(key(0, 0), key(size, 0), key(0, size), key(size, size)));
    }
    private static Set<Long> walls(Set<Long> torches) {
        return SpiritTorchLinks.build(torches, cell -> true);
    }
    private static SaltEnclosure.Result find(int x, int z, Set<Long> walls) {
        return SaltEnclosure.find(key(x, z), walls::contains, cell -> true);
    }
    public static void main(String[] args) {
        Set<Long> torches = rectangle(8);
        Set<Long> boundary = walls(torches);
        SaltEnclosure.Result closed = find(4, 4, boundary);
        assert closed.enclosed() && closed.interior().size() == 49;
        assert !find(-1, 4, boundary).enclosed() : "Exterior is not sanctified";
        assert !find(0, 4, boundary).enclosed() : "Wall is not interior";

        torches.remove(key(8, 8));
        assert !find(4, 4, walls(torches)).enclosed() : "Breaking endpoint opens enclosure";
        torches.add(key(8, 8));
        assert find(4, 4, walls(torches)).enclosed() : "Replacement closes enclosure";

        SaltEnclosure.Result max = find(32, 32, walls(rectangle(64)));
        assert max.enclosed() && max.interior().size() == 3969 : "64-block links support largest square";
        assert !find(32, 32, walls(rectangle(65))).enclosed() : "65-block endpoints do not link";
        assert !SaltEnclosure.find(key(4, 4), boundary::contains,
                cell -> cell != key(3, 3)).enclosed() : "Unloaded interior is not sanctified";

        Set<Long> mixed = new HashSet<>(walls(Set.of(key(0, 0), key(8, 0))));
        assert !find(4, 4, mixed).enclosed();
        for (int i = 0; i <= 8; i++) {
            mixed.add(key(0, i));
            mixed.add(key(8, i));
            mixed.add(key(i, 8));
        }
        assert find(4, 4, mixed).enclosed() : "Salt and torch walls can close one boundary";
        mixed.remove(key(4, 8));
        assert !find(4, 4, mixed).enclosed() : "A salt gap opens mixed boundary";
        System.out.println("Spirit torch enclosure checks passed");
    }
}
