package org.ylab.validation;

import org.ylab.dto.UserDto;


public class UserDtoValidator {

    /**
     * @param userDto This method validates a UserDto object
     */
    public static void validateUserDto(UserDto userDto) {

        if (userDto == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        EmailValidator.validateEmail(userDto.getEmail());

        validateName(userDto.getFirstName(), "First name");

        validateName(userDto.getLastName(), "Last name");

        PasswordValidator.validatePassword(userDto.getPassword());
    }

    private static void validateName(String name, String nameType) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(nameType + " cannot be null or empty");
        }

        if (name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException(nameType +" must have a length between 2 and 50");
        }
    }

}
