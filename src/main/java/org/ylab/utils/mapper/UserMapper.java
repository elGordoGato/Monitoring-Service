package org.ylab.utils.mapper;

import org.ylab.dto.UserDto;
import org.ylab.entity.User;

public class UserMapper {
    public static User dtoToEntity(UserDto userDto){
        User entity = new User();
        entity.setEmail(userDto.getEmail());
        entity.setPassword(userDto.getPassword());
        entity.setFirstName(userDto.getFirstName());
        entity.setLastName(userDto.getLastName());
        return entity;
    }

    public static UserDto entityToDto(User entity){
        return UserDto.builder()
                .email(entity.getEmail())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }
}
