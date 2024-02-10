package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Reading;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils class for mapping reading entity to it's dto and vice-versa
 */
@Mapper
public interface ReadingMapper {
    @Mapping(target = "meterType", source = "meter.id")
    ReadingDto dtoFromEntity(Reading entity);

    List<ReadingDto> dtoListFromEntity(List<Reading> entityList);
}
