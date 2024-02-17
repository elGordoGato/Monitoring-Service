package org.ylab.dto;

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
    private Short meterType;
    private Long reading;
    private String collectedDate;
}
