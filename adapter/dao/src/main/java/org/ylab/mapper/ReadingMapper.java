package org.ylab.mapper;

import org.ylab.dto.ReadingDto;
import org.ylab.entity.Reading;

import java.util.List;
import java.util.stream.Collectors;

public class ReadingMapper {
    public static ReadingDto dtoFromEntity(Reading entity){
        return ReadingDto.builder()
                .meterType(entity.getMeter().getId())
                .reading(entity.getReading())
                .build();
    }

    public static List<ReadingDto> dtoListFromEntity(List<Reading> entityList){
        return entityList.stream().map(ReadingMapper::dtoFromEntity).collect(Collectors.toList());
    }
}
