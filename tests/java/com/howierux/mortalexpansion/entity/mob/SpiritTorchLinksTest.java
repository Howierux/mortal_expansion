package com.howierux.mortalexpansion.entity.mob;

import java.util.*;
import static com.howierux.mortalexpansion.entity.mob.SpiritTorchLinks.*;

public final class SpiritTorchLinksTest {
    private static Set<Long> walls(long... torches) {
        Set<Long> endpoints = new HashSet<>();
        for (long torch : torches) endpoints.add(torch);
        return build(endpoints, column -> true);
    }

    public static void main(String[] args) {
        assert walls(key(0, 0)).isEmpty() : "A lone torch creates no wall";
        Set<Long> range = walls(key(0, 0), key(64, 0));
        assert range.size() == 65 && range.contains(key(64, 0)) : "64 is inclusive";
        assert walls(key(0, 0), key(65, 0)).isEmpty() : "65 exceeds range";
        assert walls(key(0, 0), key(1, 1)).isEmpty() : "Diagonal pairs do not link";
        Set<Long> negative = walls(key(-20, -64), key(-20, 0));
        assert negative.size() == 65 && negative.contains(key(-20, -32));
        assert x(key(Integer.MIN_VALUE, Integer.MAX_VALUE)) == Integer.MIN_VALUE;
        assert z(key(Integer.MIN_VALUE, Integer.MAX_VALUE)) == Integer.MAX_VALUE;
        assert walls(key(Integer.MIN_VALUE, 0), key(Integer.MAX_VALUE, 0)).isEmpty();
        assert walls(key(Integer.MAX_VALUE - 1, 0), key(Integer.MAX_VALUE, 0)).size() == 2;
        Set<Long> rectangle = walls(key(0, 0), key(4, 0), key(0, 4), key(4, 4));
        assert rectangle.size() == 16;
        assert rectangle.contains(key(2, 0)) && rectangle.contains(key(0, 2));
        assert !rectangle.contains(key(2, 2)) : "Interior remains passable";
        assert walls(key(0, 0), key(64, 0), key(128, 0)).size() == 129;
        Set<Long> pair = Set.of(key(-16, 0), key(16, 0));
        assert build(pair, column -> x(column) != 0).isEmpty() : "Unloaded gap breaks link";
        assert build(pair, column -> true).size() == 33 : "Reload restores link";
        Set<Long> endpoints = new HashSet<>(pair);
        endpoints.remove(key(16, 0));
        assert build(endpoints, column -> true).isEmpty() : "Removing endpoint removes wall";
        Set<Long> crossing = walls(key(-2, 0), key(2, 0), key(0, -2), key(0, 2));
        assert crossing.size() == 9;
        System.out.println("Spirit torch link checks passed");
    }
}
