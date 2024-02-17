package org.ylab.validation;

import org.ylab.exception.BadRequestException;

public class EmailValidator {
    private static final String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

    /**
     * @param email This method validates an email using a regex
     */
    public static void validateEmail(String email) {

        if (email == null || email.isEmpty()) {
            throw new BadRequestException("email cannot be null or empty");
        }
        if (!email.matches(regex)) {
            throw new BadRequestException("email must be a well-formed email address");
        }
    }
}
