package org.ylab.validation;

import org.ylab.dto.MeterDto;
import org.ylab.exception.BadRequestException;

public class MeterDtoValidator {
    public static void validateMeterDto(MeterDto meterDto) {
        if (meterDto == null) {
            throw new BadRequestException("meter cannot be null");
        }

        String type = meterDto.getType();
        if (type == null || type.isBlank()) {
            throw new BadRequestException("Type cannot be null or blank");
        }
        if (type.length() < 2 || type.length() > 50) {
            throw new BadRequestException("Type must have a length between 2 and 50");
        }
    }

}
