package org.ylab.adapter.in.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import org.ylab.adapter.in.dto.model.UserDto;
import org.ylab.domain.entity.UserEntity;

/**
 * Utils class for mapping user entity to it's dto and vice-versa
 */
@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUser(UserDto userDto);

    UserDto toUserDto(UserEntity entity);
}
