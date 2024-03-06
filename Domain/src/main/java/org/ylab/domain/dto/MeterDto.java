package org.ylab.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterDto {
    private Short id;

    @NotNull(message = "Meter type can not be absent")
    @NotBlank(message = "Meter type can not be blank")
    @Size(min = 2, max = 255, message = "Meter type length should be in range of 2 - 255 symbols")
    private String type;
}
