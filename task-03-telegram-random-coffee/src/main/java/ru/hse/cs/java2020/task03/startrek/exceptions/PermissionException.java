package ru.hse.cs.java2020.task03.startrek.exceptions;

import org.apache.commons.httpclient.ProtocolException;

public class PermissionException extends ProtocolException {

    public PermissionException() {
        super();
    }

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
