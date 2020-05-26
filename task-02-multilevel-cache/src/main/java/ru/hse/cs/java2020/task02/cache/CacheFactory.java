package ru.hse.cs.java2020.task02.cache;

public final class CacheFactory {

    private CacheFactory() { }

    public static Cache buildCache(long memorySize, long diskSize, String path, EvictionPolicy evictionPolicy)
            throws Exception {
        switch (evictionPolicy) {
            case LRUDiskCache:
                return new LRUDiskCache(diskSize, path);
            case LFUDiskCache:
                return new LFUDiskCache(diskSize, path);
            case LRUMemCache:
                return new LRUMemCache(memorySize);
            case LFUMemCache:
                return new LFUMemCache(memorySize);
            case LRUMemDiskCache:
            case LFUMemDiskCache:
                return new MemDiskCache(memorySize, diskSize, path, evictionPolicy);
            default:
                throw new Exception("Unknown eviction policy: " + evictionPolicy.name());
        }
    }
}
