package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.ylab.dto.MeterDto;
import org.ylab.entity.Meter;

/**
 * Utils class for mapping meter entity to it's dto and vice-versa
 */
@Mapper(componentModel = "spring")
public interface MeterMapper {
    Meter toMeter(MeterDto dto);

    MeterDto toMeterDto(Meter entity);
}
