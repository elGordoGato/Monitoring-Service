package org.ylab.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ylab.enums.Role;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role = Role.USER;
}
