package org.ylab.validation;

import org.ylab.exception.BadRequestException;

public class PasswordValidator {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 16;

    /**
     * @param password This method validates a password using some rules
     */
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException("password cannot be null or empty");
        }

        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH || password.contains(" ")) {
            throw new BadRequestException("password must have a length between 8 and 20 and not contain spaces");
        }
    }
}
