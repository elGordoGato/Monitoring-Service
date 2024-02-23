package org.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.dto.UserDto;
import org.ylab.entity.UserEntity;
import org.ylab.user.UserService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Tests for Register Servlet functionality")
@ExtendWith(MockitoExtension.class)
class RegisterServletTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @InjectMocks
    private RegisterServlet servlet;
    private UserDto userDto;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("secret");
        userDto.setFirstName("Test First Name");
        userDto.setLastName("Test Last Name");

        user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("secret");
        user.setFirstName("Test First Name");
        user.setLastName("Test Last Name");
    }

    @Test
    public void testDoPostWithValidUserDto() throws IOException {
        String userJson = objectMapper.writeValueAsString(userDto);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(userJson.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        when(request.getReader()).thenReturn(bufferedReader);
        when(request.getSession()).thenReturn(session);
        when(userService.create(any(UserEntity.class))).thenReturn(user);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(session).setAttribute("user", user);
    }
}