package ru.hse.cs.java2020.task03.startrek.exceptions;

import org.apache.commons.httpclient.ProtocolException;

public class NotFoundException extends ProtocolException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
