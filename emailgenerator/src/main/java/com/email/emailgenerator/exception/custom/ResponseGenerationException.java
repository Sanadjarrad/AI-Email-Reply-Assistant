package com.email.emailgenerator.exception.custom;

public class ResponseGenerationException extends RuntimeException {

    public ResponseGenerationException(String message) {
        super(message);
    }

    public ResponseGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
