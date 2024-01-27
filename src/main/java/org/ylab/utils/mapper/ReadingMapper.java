package org.ylab.utils.mapper;

import org.ylab.dto.ReadingDto;
import org.ylab.entity.MeterReading;

import java.util.List;
import java.util.stream.Collectors;

public class ReadingMapper {
    public static ReadingDto dtoFromEntity(MeterReading entity){
        return ReadingDto.builder()
                .meterType(entity.getMeter().getId())
                .reading(entity.getReading())
                .build();
    }

    public static List<ReadingDto> dtoListFromEntity(List<MeterReading> entityList){
        return entityList.stream().map(ReadingMapper::dtoFromEntity).collect(Collectors.toList());
    }
}
