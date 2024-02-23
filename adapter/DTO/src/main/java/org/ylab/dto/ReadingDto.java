package org.ylab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;


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
