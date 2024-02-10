package org.ylab.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ylab.dto.UserDto;
import org.ylab.entity.User;
import org.ylab.mapper.UserMapper;
import org.ylab.mapper.UserMapperImpl;
import org.ylab.user.UserService;
import org.ylab.validation.EmailValidator;
import org.ylab.validation.PasswordValidator;
import org.ylab.validation.UserDtoValidator;

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

    /**
     * @param config
     */
    @Override
    public void init(ServletConfig config) {
        final Object userServiceFromContext = getServletContext().getAttribute("userService");

        if (userServiceFromContext instanceof UserService) {
            this.userService = (UserService) userServiceFromContext;
        } else {
            throw new IllegalStateException("Repo has not been initialized!");
        }
    }

    /**
     * @param req
     * @param resp
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws JsonProcessingException {
        final String userJson = req.getParameter("user");
        UserDto userToCreate = new ObjectMapper().readValue(userJson, UserDto.class);

        UserDtoValidator.validateUserDto(userToCreate);

        User createdUser = userService.create(
                userMapper.dtoToEntity(userToCreate));

        req.getSession().setAttribute("user", createdUser);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
}