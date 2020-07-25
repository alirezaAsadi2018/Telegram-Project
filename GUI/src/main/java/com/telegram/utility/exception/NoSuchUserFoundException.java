package com.telegram.utility.exception;

public class NoSuchUserFoundException extends Exception{
    public NoSuchUserFoundException() {
    }

    public NoSuchUserFoundException(String message) {
        super(message);
    }
}
