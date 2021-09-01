package com.ss.task.api.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(final String message) {
        super(message);
    }
}
