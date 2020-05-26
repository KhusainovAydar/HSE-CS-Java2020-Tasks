package ru.hse.cs.java2020.task02.cache;

import java.io.IOException;

import javax.naming.LimitExceededException;

public interface Cache {
    String get(long key) throws IOException;

    String put(long key, String value) throws IOException, LimitExceededException;
}
