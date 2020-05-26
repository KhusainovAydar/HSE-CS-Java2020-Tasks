package ru.hse.cs.java2020.task02.cache;

public class KeyValueMeta {
    private final long key;
    private final String value;
    private final long meta;

    public KeyValueMeta(long key, String value, long meta) {
        this.key = key;
        this.value = value;
        this.meta = meta;
    }

    public long getKey() {
        return key;
    }

    public long getMeta() {
        return meta;
    }

    public String getValue() {
        return value;
    }
}
