package ru.hse.cs.java2020.task02.cache;

import java.io.IOException;

class LRUDiskCache extends DiskCache {
    private int counter = 0;

    LRUDiskCache(long diskSizeLimit, String dumpDirectory) throws IOException {
        super(diskSizeLimit, dumpDirectory);
    }

    @Override
    protected long updateMeta(long oldMeta) {
        return ++counter;
    }
}
