package com.app.Exceptions;

public class FileTypeNotPDFException extends RuntimeException {
    public FileTypeNotPDFException(String message) {
        super(message);
    }
}
