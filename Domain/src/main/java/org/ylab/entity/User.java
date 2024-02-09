package org.ylab.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ylab.enums.Role;

/**
 * User of app
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * ID of user
     */
    private Integer id;
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
