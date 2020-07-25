package com.telegram.utility.exception;

public class NoSuchClientInClientMapException extends Exception{

    public NoSuchClientInClientMapException() {
    }

    public NoSuchClientInClientMapException(String message) {
        super(message);
    }
}
