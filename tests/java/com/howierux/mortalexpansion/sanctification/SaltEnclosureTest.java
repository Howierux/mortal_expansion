package com.howierux.mortalexpansion.sanctification;

import java.util.HashSet;
import java.util.Set;

/** Standalone regression checks: run with assertions enabled, no Minecraft runtime required. */
public final class SaltEnclosureTest {
    private static Set<Long> ring(int minX, int minZ, int maxX, int maxZ) {
        Set<Long> salt = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            salt.add(SaltEnclosure.key(x, minZ)); salt.add(SaltEnclosure.key(x, maxZ));
        }
        for (int z = minZ; z <= maxZ; z++) {
            salt.add(SaltEnclosure.key(minX, z)); salt.add(SaltEnclosure.key(maxX, z));
        }
        return salt;
    }
    private static SaltEnclosure.Result find(int x, int z, Set<Long> salt) {
        return SaltEnclosure.find(SaltEnclosure.key(x,z), salt::contains,
                k -> Math.abs(SaltEnclosure.x(k)) < 100 && Math.abs(SaltEnclosure.z(k)) < 100);
    }
    public static void main(String[] args) {
        Set<Long> salt = ring(-3,-3,3,3);
        assert find(0,0,salt).enclosed();
        assert find(0,0,salt).interior().size() == 25;
        assert !find(8,8,salt).enclosed();
        assert !find(3,0,salt).enclosed(); // Boundary is not interior.
        salt.remove(SaltEnclosure.key(3,0));
        assert !find(0,0,salt).enclosed(); // Single missing piece opens the region.
        salt.add(SaltEnclosure.key(3,0));
        assert find(0,0,salt).enclosed(); // Repair restores sanctification.
        assert !SaltEnclosure.find(SaltEnclosure.key(0,0),salt::contains,
                k -> SaltEnclosure.x(k) < 2).enclosed(); // Unloaded border cannot seal.
        salt = ring(-5,-5,5,5);
        for (int z = -4; z <= 0; z++) salt.add(SaltEnclosure.key(0,z));
        for (int x = -4; x <= 0; x++) salt.add(SaltEnclosure.key(x,0));
        assert find(-2,-2,salt).enclosed(); // Concave subdivision.
        assert find(2,2,salt).enclosed();
        assert find(-2,-2,salt).interior().size() == 16;
        assert !find(0,0,ring(-40,-40,40,40)).enclosed(); // Area limit fails safely.
        assert SaltEnclosure.x(SaltEnclosure.key(-123,-456)) == -123;
        assert SaltEnclosure.z(SaltEnclosure.key(-123,-456)) == -456;
        System.out.println("Salt enclosure regression checks passed");
    }
}
