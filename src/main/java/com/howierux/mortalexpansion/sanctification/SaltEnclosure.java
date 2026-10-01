package com.howierux.mortalexpansion.sanctification;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.LongPredicate;

/** Bounded planar flood fill. Salt columns alone form walls; unloaded space is exterior. */
public final class SaltEnclosure {
    private SaltEnclosure() {}
    public static final int MAX_AREA = 4096;
    public static long key(int x, int z) { return ((long) x << 32) | (z & 0xffffffffL); }
    public static int x(long key) { return (int) (key >> 32); }
    public static int z(long key) { return (int) key; }
    public record Result(boolean enclosed, Set<Long> interior) {}

    public static Result find(long start, LongPredicate salt, LongPredicate loaded) {
        Set<Long> visited = new HashSet<>();
        if (salt.test(start) || !loaded.test(start)) return new Result(false, visited);
        ArrayDeque<Long> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            long cell = queue.removeFirst();
            int x = x(cell), z = z(cell);
            long[] neighbors = {key(x + 1, z), key(x - 1, z), key(x, z + 1), key(x, z - 1)};
            for (long next : neighbors) {
                if (!loaded.test(next)) return new Result(false, visited);
                if (salt.test(next) || visited.contains(next)) continue;
                if (visited.size() >= MAX_AREA) return new Result(false, visited);
                visited.add(next);
                queue.addLast(next);
            }
        }
        return new Result(true, visited);
    }
}
