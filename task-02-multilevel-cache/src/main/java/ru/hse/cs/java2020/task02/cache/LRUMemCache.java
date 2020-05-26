package ru.hse.cs.java2020.task02.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.naming.LimitExceededException;

public class LRUMemCache implements MemCache {
    private final long memorySize;

    private Map<Long, String> cache = new HashMap<>();
    private Queue<Long> recentUsages = new LinkedList<>();

    private long usedMemory = 0;

    public LRUMemCache(long memorySize) {
        this.memorySize = memorySize;
    }

    @Override
    public String get(long key) throws IOException {
        String value = cache.get(key);
        if (value != null) {
            recentUsages.add(key);
        }
        return value;
    }

    @Override
    public String put(long key, String value) throws LimitExceededException {
        if (value == null) {
            throw new NullPointerException("Value can not be empty");
        }

        long valueSize = calculateSize(value);
        if (valueSize > memorySize) {
            throw new LimitExceededException("Value is bigger than memory size.");
        }

        String oldValue = cache.get(key);
        long oldValueSize = calculateSize(oldValue);
        long keySize = oldValue == null ? Long.BYTES : 0;
        long finalPutSize = valueSize;
        if (oldValue != null) {
            finalPutSize -= oldValueSize;
        } else {
            finalPutSize += keySize;
        }

        while (finalPutSize + usedMemory > memorySize) {
            Long delKey = recentUsages.poll();
            if (delKey != null && cache.containsKey(delKey)) {
                String delValue = cache.get(delKey);
                usedMemory -= calculateSize(delValue);
                usedMemory -= Long.BYTES;
                cache.remove(delKey);
            }
        }

        usedMemory += finalPutSize;
        cache.put(key, value);
        recentUsages.add(key);
        return oldValue;
    }

    @Override
    public boolean warmUp(KeyValueMeta kvm) throws Exception {
        boolean enough = Long.BYTES + calculateSize(kvm.getValue()) + usedMemory >= memorySize;
        put(kvm.getKey(), kvm.getValue());
        return enough;
    }

    private long calculateSize(String value) {
        return value == null ? 0 : value.length() * Character.BYTES;
    }
}
