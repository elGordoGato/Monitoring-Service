package org.ylab.entity;

import lombok.*;
import org.ylab.enums.Role;

/**
 * User of app
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
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
    @ToString.Exclude
    private String password;
    /**
     * Role of user(USER, ADMIN) depending on what control of rights are implemented
     */
    @ToString.Exclude
    private Role role = Role.USER;
}
