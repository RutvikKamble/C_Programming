package com.example.assets_management.exception;

/**
 * Thrown when a requested entity (Maintenance, Warranty, Document, Request, Audit)
 * does not exist in the database. Handled globally by GlobalExceptionHandler,
 * which converts it into a 404 NOT_FOUND response instead of a raw stack trace.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
