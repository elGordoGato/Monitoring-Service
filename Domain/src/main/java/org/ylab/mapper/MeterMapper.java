package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.ylab.dto.MeterDto;
import org.ylab.entity.Meter;

@Mapper
public interface MeterMapper {
    Meter dtoToEntity(MeterDto dto);

    MeterDto entityToDto(Meter entity);
}
