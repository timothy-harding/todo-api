package com.ss.task.api.exception;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException(final String message) {
        super(message);
    }
}
