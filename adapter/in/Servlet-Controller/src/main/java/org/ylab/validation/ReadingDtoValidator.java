package org.ylab.validation;

import org.ylab.dto.ReadingDto;
import org.ylab.exception.BadRequestException;

import static java.util.Objects.isNull;

public class ReadingDtoValidator {
    public static void validateReadingDto(ReadingDto readingDto) {
        if (readingDto == null) {
            throw new BadRequestException("meter cannot be null");
        }

        Short type = readingDto.getMeterType();
        Long readingValue = readingDto.getReading();
        if (isNull(type)) {
            throw new BadRequestException("Type cannot be null");
        }
        if (isNull(readingValue) || readingValue < 0) {
            throw new BadRequestException("Reading value cannot be null or negative");
        }
    }
}
