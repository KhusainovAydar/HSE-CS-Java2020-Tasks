package ru.hse.cs.java2020.task03.startrek.exceptions;

import org.apache.commons.httpclient.ProtocolException;

public class ConflictException extends ProtocolException {

    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
