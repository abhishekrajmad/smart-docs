package com.madocde.smartdocs.exception;

public class DocumentProcessingException extends RuntimeException {
    public DocumentProcessingException(String message) {
        super(message);
    }
    public DocumentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
