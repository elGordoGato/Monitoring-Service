package org.ylab.dto;

import lombok.Builder;
import lombok.Data;


/**
 * Data transfer object for Reading entity
 */
@Data
@Builder
public class ReadingDto {
    private int meterType;
    private long reading;
}
