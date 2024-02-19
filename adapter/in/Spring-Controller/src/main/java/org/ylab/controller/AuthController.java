package org.ylab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public UserDto register(@RequestBody UserDto dtoToRegister) {
        UserEntity userToRegister = userMapper.toUser(dtoToRegister);
        UserEntity createdUser = userService.create(userToRegister);
        return userMapper.toUserDto(createdUser);
    }

    @PostMapping(path = "/login")
    public String login(HttpServletRequest request, @RequestBody UserDto dtoToLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dtoToLogin.getEmail(),
                        dtoToLogin.getPassword()));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,securityContext);
        return "Logged in";
    }

}
