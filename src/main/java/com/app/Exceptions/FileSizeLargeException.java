package com.app.Exceptions;

public class FileSizeLargeException extends RuntimeException {
    public FileSizeLargeException(String message) {
        super(message);
    }
}
