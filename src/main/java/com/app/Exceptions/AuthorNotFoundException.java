package com.app.Exceptions;

public class AuthorNotFoundException extends RuntimeException{
    public AuthorNotFoundException(String name){
        super(name);
    }
}
