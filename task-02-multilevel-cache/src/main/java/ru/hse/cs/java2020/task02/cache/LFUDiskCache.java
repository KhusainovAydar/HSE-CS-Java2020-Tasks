package ru.hse.cs.java2020.task02.cache;

import java.io.IOException;

class LFUDiskCache extends DiskCache {
    LFUDiskCache(long diskSizeLimit, String dumpDirectory) throws IOException {
        super(diskSizeLimit, dumpDirectory);
    }

    @Override
    protected long updateMeta(long oldMeta) {
        return oldMeta + 1;
    }
}
