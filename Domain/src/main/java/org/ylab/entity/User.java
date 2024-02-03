package org.ylab.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ylab.enums.Role;

import java.util.UUID;

/**
 * User of app
 */
@Getter
@Setter
@NoArgsConstructor
public class User {
    /**
     * ID of user
     */
    private Long id;
    /**
     * Email of user
     */
    private String email;
    /**
     * First name of user
     */
    private String firstName;
    /**
     * Lasst name of user
     */
    private String lastName;
    /**
     * Password of user
     */
    private String password;
    /**
     * Role of user(USER, ADMIN) depending on what control of rights are implemented
     */
    private Role role = Role.USER;
}
