package org.ylab.domain.mapper;

import org.ylab.domain.dto.MeterDto;
import org.ylab.domain.entity.Meter;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * Utils class for mapping meter entity to it's dto and vice-versa
 */
@Component
@Mapper(componentModel = "spring")
public interface MeterMapper {
    Meter toMeter(MeterDto dto);

    MeterDto toMeterDto(Meter entity);
}
