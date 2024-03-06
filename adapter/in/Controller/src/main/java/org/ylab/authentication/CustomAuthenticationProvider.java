package org.ylab.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.ylab.domain.entity.UserEntity;
import org.ylab.usecase.service.UserService;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = Optional.ofNullable(authentication.getCredentials())
                .orElseThrow(() -> new BadCredentialsException("Password is empty")).toString();

        UserEntity myUser = userService.authenticate(userName, password);
        UserDetails principal = User.builder()
                .username(String.valueOf(myUser.getId()))
                .password(myUser.getPassword())
                .roles(String.valueOf(myUser.getRole()))
                .build();
        return new UsernamePasswordAuthenticationToken(myUser, password, principal.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}