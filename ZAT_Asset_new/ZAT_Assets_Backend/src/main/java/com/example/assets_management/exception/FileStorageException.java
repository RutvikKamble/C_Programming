package com.example.assets_management.exception;

/**
 * Thrown for any failure while storing, reading, or validating a file on disk
 * (unsupported type, oversized file, missing file, IO failure). Handled by
 * GlobalExceptionHandler and returned as 400/404/500 depending on cause.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
