package ru.hse.cs.java2020.task02;

import ru.hse.cs.java2020.task02.cache.Cache;
import ru.hse.cs.java2020.task02.cache.CacheFactory;
import ru.hse.cs.java2020.task02.cache.EvictionPolicy;

public class Main {
    private static final int MEMORY_SIZE = 100;
    private static final int CYCLE_SIZE = 1000;
    private static final int DISK_SIZE = 17408;
    private static final int MAGIC_CONST = 500;
    public static void main(String[] args) throws Exception {
        Cache cache = CacheFactory.buildCache(MEMORY_SIZE, DISK_SIZE, "/Users/aydarboss/Desktop/scripts", EvictionPolicy.LRUMemDiskCache);

        for (int i = 0; i != CYCLE_SIZE; ++i) {
            cache.put(i, Integer.valueOf(i * MAGIC_CONST).toString());
        }

        for (int i = 0; i != CYCLE_SIZE; ++i) {
            System.out.println(cache.get(i));
        }
    }
}
