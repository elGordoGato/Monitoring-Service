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
    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 16)
    private String password;

    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 2, max = 100)
    private String firstName;

    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 2, max = 100)
    private String lastName;
}
