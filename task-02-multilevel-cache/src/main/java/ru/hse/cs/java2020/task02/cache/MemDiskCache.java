package ru.hse.cs.java2020.task02.cache;

import java.io.IOException;

import javax.naming.LimitExceededException;

public class MemDiskCache implements Cache {
    private final MemCache memCache;
    private final DiskCache diskCache;

    public MemDiskCache(long memorySize, long diskSize, String path, EvictionPolicy evictionPolicy)
            throws Exception {
        switch (evictionPolicy) {
            case LRUMemDiskCache:
                this.memCache = new LRUMemCache(memorySize);
                this.diskCache = new LRUDiskCache(diskSize, path);
                break;
            case LFUMemDiskCache:
                this.memCache = new LFUMemCache(memorySize);
                this.diskCache = new LFUDiskCache(diskSize, path);
            default:
                throw new Exception("Unknown MemDiskCache type " + evictionPolicy.name());
        }

        warmUp();
    }

    @Override
    public String get(long key) throws IOException {
        String value = memCache.get(key);
        if (value == null) {
            value = diskCache.get(key);
        }
        return value;
    }

    @Override
    public String put(long key, String value) throws IOException, LimitExceededException {
        String oldValue = memCache.put(key, value);
        diskCache.put(key, value);
        return oldValue;
    }

    void warmUp() throws Exception {
        for (long key : diskCache.getWarmestKeys()) {
            if (memCache.warmUp(diskCache.getFullInfoByKey(key))) {
                return;
            }
        }
    }
}
