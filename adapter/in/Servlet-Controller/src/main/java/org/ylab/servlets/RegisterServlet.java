package org.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ylab.annotations.Loggable;
import org.ylab.dto.UserDto;
import org.ylab.entity.UserEntity;
import org.ylab.mapper.UserMapper;
import org.ylab.mapper.UserMapperImpl;
import org.ylab.user.UserService;
import org.ylab.validation.UserDtoValidator;

import java.io.IOException;
import java.time.Instant;

@Loggable
@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private UserService userService;

    public RegisterServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.userMapper = new UserMapperImpl();

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final Object userServiceFromContext = getServletContext().getAttribute("userService");

        if (userServiceFromContext instanceof UserService) {
            this.userService = (UserService) userServiceFromContext;
        } else {
            throw new IllegalStateException(
                    String.format("%s - can't init %s: %s was not found in ServletContext",
                            Instant.now(), this.getClass().getName(), UserService.class.getName()));
        }
    }

    /**
     * @param req  expects userDto json in body
     * @param resp response status 201 if successful
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        UserDto userToCreate = objectMapper.readValue(
                req.getReader(), UserDto.class);

        UserDtoValidator.validateUserDto(userToCreate);

        UserEntity createdUser = userService.create(
                userMapper.toUser(userToCreate));

        req.getSession().setAttribute("user", createdUser);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        log("Registered new user: " + createdUser);
    }
}