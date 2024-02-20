package org.ylab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ylab.dto.UserDto;
import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;
import org.ylab.mapper.UserMapper;
import org.ylab.user.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("Test class for Auth Controller")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthController controller;
    private MockMvc mockMvc;

    private UserDto userDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        userDto = new UserDto("user@example.com", "password",
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
