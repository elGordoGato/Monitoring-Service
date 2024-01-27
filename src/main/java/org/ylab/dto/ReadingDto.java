package org.ylab.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReadingDto {
    private int meterType;
    private long reading;
}
