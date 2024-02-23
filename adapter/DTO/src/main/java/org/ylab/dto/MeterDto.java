package org.ylab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
