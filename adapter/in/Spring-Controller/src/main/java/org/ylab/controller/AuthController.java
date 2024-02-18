package org.ylab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylab.dto.UserDto;
import org.ylab.entity.UserEntity;
import org.ylab.mapper.UserMapper;
import org.ylab.user.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto register(UserDto dtoToRegister) {
        UserEntity userToRegister = userMapper.toUser(dtoToRegister);
        UserEntity createdUser = userService.create(userToRegister);
        return userMapper.toUserDto(createdUser);
    }

    @PostMapping(path = "/login")
    public String login(UserDto dtoToLogin) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dtoToLogin.getEmail(),
                        dtoToLogin.getPassword()));
        return "Logged in";
    }

}
