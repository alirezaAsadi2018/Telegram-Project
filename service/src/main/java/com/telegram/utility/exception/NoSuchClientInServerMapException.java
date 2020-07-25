package com.telegram.utility.exception;

public class NoSuchClientInServerMapException extends Exception {
    public NoSuchClientInServerMapException() {
    }

    public NoSuchClientInServerMapException(String message) {
        super(message);
    }
}
