package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.ylab.dto.MeterDto;
import org.ylab.entity.Meter;

@Mapper
public interface MeterMapper {
    Meter toMeter(MeterDto dto);

    MeterDto toMeterDto(Meter entity);
}
