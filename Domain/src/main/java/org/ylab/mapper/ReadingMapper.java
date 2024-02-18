package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Reading;

import java.util.List;

/**
 * Utils class for mapping reading entity to it's dto and vice-versa
 */
@Mapper(componentModel = "spring")
public interface ReadingMapper {
    @Mapping(target = "meterType", source = "meter.id",
            dateFormat = "dd-MM-yyyy HH:mm:ss")
    ReadingDto toReadingDto(Reading entity);

    List<ReadingDto> toReadingDtoList(List<Reading> entityList);
}
