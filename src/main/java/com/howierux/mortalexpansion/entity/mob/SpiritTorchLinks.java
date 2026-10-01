package com.howierux.mortalexpansion.entity.mob;

import java.util.*;
import java.util.function.LongPredicate;

/** Horizontal torch links, projected through the entire build height. */
public final class SpiritTorchLinks {
    public static final int MAX_RANGE = 64;
    private SpiritTorchLinks() {}

    public static long key(int x, int z) {
        return ((long)x << 32) | (z & 0xffffffffL);
    }
    public static int x(long key) { return (int)(key >> 32); }
    public static int z(long key) { return (int)key; }

    public static Set<Long> build(Set<Long> torches, LongPredicate loaded) {
        Map<Integer, List<Integer>> rows = new HashMap<>();
        Map<Integer, List<Integer>> columns = new HashMap<>();
        for (long torch : torches) {
            rows.computeIfAbsent(z(torch), unused -> new ArrayList<>()).add(x(torch));
            columns.computeIfAbsent(x(torch), unused -> new ArrayList<>()).add(z(torch));
        }
        Set<Long> walls = new HashSet<>();
        connect(rows, true, loaded, walls);
        connect(columns, false, loaded, walls);
        return walls;
    }

    private static void connect(Map<Integer, List<Integer>> lines, boolean alongX,
            LongPredicate loaded, Set<Long> walls) {
        for (var line : lines.entrySet()) {
            List<Integer> positions = line.getValue();
            positions.sort(Integer::compare);
            // Adjacent endpoints cover exactly the same columns as all valid pairs.
            for (int i = 1; i < positions.size(); i++) {
                int start = positions.get(i - 1), end = positions.get(i);
                if ((long)end - start > MAX_RANGE) continue;
                List<Long> segment = new ArrayList<>();
                for (long coordinate = start; coordinate <= end; coordinate++) {
                    long column = alongX ? key((int)coordinate, line.getKey())
                            : key(line.getKey(), (int)coordinate);
                    if (!loaded.test(column)) {
                        segment.clear();
                        break;
                    }
                    segment.add(column);
                }
                walls.addAll(segment);
            }
        }
    }
}
