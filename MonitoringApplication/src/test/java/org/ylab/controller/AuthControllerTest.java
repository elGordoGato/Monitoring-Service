package org.ylab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.ylab.authentication.SecurityConfig;
import org.ylab.domain.dto.UserDto;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.enums.Role;
import org.ylab.domain.mapper.UserMapper;
import org.ylab.usecase.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("Test class for Auth Controller")
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "user@example.com", "password",
                "FirstName", "LastName");
        userEntity = new UserEntity(1, "user@example.com",
                "FirstName", "LastName", "password", Role.USER);
    }

    @DisplayName("Test register endpoint, should return created user from request body")
    @Test
    void register_shouldReturnCreatedUser() throws Exception {
        // given
        when(userMapper.toUser(userDto)).thenReturn(userEntity);
        when(userService.create(userEntity)).thenReturn(userEntity);
        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("user@example.com")))
                .andExpect(jsonPath("$.firstName", is("FirstName")))
                .andExpect(jsonPath("$.lastName", is("LastName")));
    }

    @DisplayName("Test login endpoint, should return message that user successfully logged in")
    @Test
    void login_shouldReturnSuccessMessage() throws Exception {
        // given
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(userEntity, null));
        when(userMapper.toUserDto(userEntity)).thenReturn(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("user@example.com")))
                .andExpect(jsonPath("$.firstName", is("FirstName")))
                .andExpect(jsonPath("$.lastName", is("LastName")));
    }
}
