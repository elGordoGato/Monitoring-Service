package org.ylab.exception;

/**
 * Thrown when requested data is not found
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
