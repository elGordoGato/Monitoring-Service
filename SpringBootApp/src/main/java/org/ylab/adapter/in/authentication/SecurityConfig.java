package org.ylab.adapter.in.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.ylab.usecase.service.UserService;

@Configuration
@EnableWebSecurity
@ComponentScan("org.ylab")
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(UserService userService) {
        this.authenticationProvider = new CustomAuthenticationProvider(userService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**", "/v3/**", "/swagger-ui/**")
                                .permitAll()
                                .requestMatchers("/auth/logout")
                                .authenticated()
                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")
                                .anyRequest()
                                .hasRole("USER"))
                .authenticationProvider(authenticationProvider);
        return http.getOrBuild();
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return this.authenticationProvider;
    }
}