package ru.hse.cs.java2020.task02.cache;

public interface MemCache extends Cache {
    boolean warmUp(KeyValueMeta kvm) throws Exception;
}
