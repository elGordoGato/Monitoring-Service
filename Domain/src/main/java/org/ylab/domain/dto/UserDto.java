package org.ylab.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.ylab.domain.marker.Marker;


/**
 * DTO for User entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull(message = "Email must be entered")
    @Email(message = "Wrong format for email")
    private String email;

    @ToString.Exclude
    @NotNull(message = "Password must be entered")
    @NotBlank(message = "Password can not be blank")
    @Size(min = 5, max = 16, message = "Password length should be in range of 5 - 16 symbols")
    private String password;

    @NotNull(groups = Marker.OnCreate.class, message = "First name must be entered")
    @NotBlank(groups = Marker.OnCreate.class, message = "First name must not be blank")
    @Size(min = 2, max = 100, message = "Allowed length for first name is 2 - 100")
    private String firstName;

    @NotNull(groups = Marker.OnCreate.class, message = "Last name must be entered")
    @NotBlank(groups = Marker.OnCreate.class, message = "Last name must not be blank")
    @Size(min = 2, max = 100, message = "Allowed length for last name is 2 - 100")
    private String lastName;
}
