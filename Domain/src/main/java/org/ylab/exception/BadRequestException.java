package org.ylab.exception;

/**
 * Class that thrown when request from user is not valid
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
