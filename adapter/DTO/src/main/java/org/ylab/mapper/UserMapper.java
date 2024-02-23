package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.ylab.dto.UserDto;
import org.ylab.entity.UserEntity;

/**
 * Utils class for mapping user entity to it's dto and vice-versa
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUser(UserDto userDto);

    UserDto toUserDto(UserEntity entity);
}
