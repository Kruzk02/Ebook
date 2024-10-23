package com.app.Exceptions;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthorNotFoundException(AuthorNotFoundException ex) {
        ErrorResponse response = new ErrorResponse("Author Not Found");
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EBookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEBookNotFoundException(EBookNotFoundException ex) {
        ErrorResponse response = new ErrorResponse("EBook Not Found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse response = new ErrorResponse("User Not Found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex){
        ErrorResponse response = new ErrorResponse("Comment Not Found");
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileTypeNotPDFException.class)
    public ResponseEntity<ErrorResponse> handleFileTypeNotPDFException(FileTypeNotPDFException ex){
        ErrorResponse response = new ErrorResponse("File type is not pdf");
        return new ResponseEntity<>(response,HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(FileSizeLargeException.class)
    public ResponseEntity<ErrorResponse> handleFileSizeLargeException(FileSizeLargeException ex){
        ErrorResponse response = new ErrorResponse("File is large than 10mb");
        return new ResponseEntity<>(response,HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
