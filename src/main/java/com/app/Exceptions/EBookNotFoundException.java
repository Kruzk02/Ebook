package com.app.Exceptions;

public class EBookNotFoundException extends RuntimeException{
    public EBookNotFoundException(String name) {
        super(name);
    }
}
