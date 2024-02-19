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
    @NotNull
    private Short meterType;

    @NotNull
    @PositiveOrZero
    private Long reading;

    private String collectedDate;
}
