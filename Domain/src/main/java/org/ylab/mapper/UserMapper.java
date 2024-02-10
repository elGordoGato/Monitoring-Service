package org.ylab.mapper;

import org.mapstruct.Mapper;
import org.ylab.dto.UserDto;
import org.ylab.entity.User;

/**
 * Utils class for mapping user entity to it's dto and vice-versa
 */
@Mapper
public interface UserMapper {
    User dtoToEntity(UserDto userDto);

    UserDto entityToDto(User entity);
}
