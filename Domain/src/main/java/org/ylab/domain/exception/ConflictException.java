package org.ylab.domain.exception;

/**
 * Thrown when there is a conflict between data
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
