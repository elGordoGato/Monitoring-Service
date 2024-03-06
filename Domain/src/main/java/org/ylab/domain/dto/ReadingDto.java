package org.ylab.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data transfer object for Reading entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingDto {
    @NotNull(message = "Meter id can not be absent")
    private Short meterType;

    @NotNull(message = "Reading value can not be absent")
    @PositiveOrZero(message = "Readings value can not be negative")
    private Long reading;

    private String collectedDate;
}
