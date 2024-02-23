package org.ylab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylab.marker.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
