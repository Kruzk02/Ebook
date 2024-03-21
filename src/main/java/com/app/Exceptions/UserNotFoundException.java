package com.app.Exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String name) {
        super(name);
    }
}
