package org.ylab.adapter.in.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylab.adapter.in.dto.model.UserDto;
import org.ylab.annotations.Loggable;
import org.ylab.domain.entity.UserEntity;
import org.ylab.adapter.in.dto.mapper.UserMapper;
import org.ylab.domain.marker.Marker;
import org.ylab.usecase.userService.UserService;


/**
 * Authentication controller with public access
 */
@Loggable
@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, UserMapper userMapper, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
    }

    /**
     * @param dtoToRegister UserDto to be registered
     * @return Dto of created User
     */
    @Validated(Marker.OnCreate.class)
    @PostMapping(path = "/register")
    public UserDto register(@RequestBody @Valid UserDto dtoToRegister) {
        UserEntity userToRegister = userMapper.toUser(dtoToRegister);
        UserEntity createdUser = userService.create(userToRegister);
        return userMapper.toUserDto(createdUser);
    }

    /**
     * @param request    HttpServletRequest to get session and set security key after successful authentication
     * @param dtoToLogin UserDto with email and password to authenticate
     * @return UserDto of authenticated user
     */
    @PostMapping(path = "/login")
    public UserDto login(HttpServletRequest request,
                         @RequestBody @Valid UserDto dtoToLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dtoToLogin.getEmail(),
                        dtoToLogin.getPassword()));
        UserEntity loggedUser = (UserEntity) authentication.getPrincipal();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        HttpSession session = request.getSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        return userMapper.toUserDto(loggedUser);
    }

}
