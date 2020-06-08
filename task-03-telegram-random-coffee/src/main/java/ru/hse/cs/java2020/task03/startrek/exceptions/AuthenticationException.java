package ru.hse.cs.java2020.task03.startrek.exceptions;

import org.apache.commons.httpclient.ProtocolException;

public class AuthenticationException extends ProtocolException {

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}

