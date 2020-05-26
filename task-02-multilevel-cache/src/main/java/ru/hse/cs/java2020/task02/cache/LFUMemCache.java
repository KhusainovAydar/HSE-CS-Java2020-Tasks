package ru.hse.cs.java2020.task02.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.naming.LimitExceededException;

public class LFUMemCache implements MemCache {
    private final long memorySize;

    private Map<Long, String> keyToValue = new HashMap<>();
    private Map<Long, Integer> keyToCounters = new HashMap<>();
    private Map<Integer, LinkedHashSet<Long>> lists = new HashMap<>();

    private long usedMemory = 0;
    private int minimum = 0;

    public LFUMemCache(long memorySize) {
        this.memorySize = memorySize;
    }

    @Override
    public String get(long key) throws IOException {
        if (!keyToValue.containsKey(key)) {
            return null;
        }

        int count = keyToCounters.get(key);
        keyToCounters.put(key, count + 1);
        lists.get(count).remove(key);

        if (count == minimum && lists.get(count).size() == 0) {
            minimum++;
        }

        if (!lists.containsKey(count + 1)) {
            lists.put(count + 1, new LinkedHashSet<>());
        }
        lists.get(count + 1).add(key);
        return keyToValue.get(key);
    }

    @Override
    public String put(long key, String value) throws IOException, LimitExceededException {
        if (value == null) {
            throw new NullPointerException("Value can not be empty");
        }

        long valueSize = calculateSize(value);
        if (valueSize > memorySize) {
            throw new LimitExceededException("Value is bigger than memory size.");
        }

        String oldValue = keyToValue.get(key);
        int count = 0;
        long oldValueSize = calculateSize(oldValue);
        long keySize = oldValue == null ? Long.BYTES : 0;
        long finalPutSize = valueSize;
        if (oldValue != null) {
            finalPutSize -= oldValueSize;
            count = keyToCounters.get(key);
        } else {
            finalPutSize += keySize;
        }

        while (finalPutSize + usedMemory > memorySize) {
            long evit = lists.get(minimum).iterator().next();
            lists.get(minimum).remove(evit);
            String delValue = keyToValue.get(evit);
            usedMemory -= calculateSize(delValue);
            usedMemory -= Long.BYTES;
            keyToValue.remove(evit);
            keyToCounters.remove(evit);
        }

        usedMemory += finalPutSize;
        keyToCounters.put(key, count + 1);
        lists.get(count).remove(key);

        if (count == minimum && lists.get(count).size() == 0) {
            minimum++;
        }

        if (!lists.containsKey(count + 1)) {
            lists.put(count + 1, new LinkedHashSet<>());
        }
        lists.get(count + 1).add(key);
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
