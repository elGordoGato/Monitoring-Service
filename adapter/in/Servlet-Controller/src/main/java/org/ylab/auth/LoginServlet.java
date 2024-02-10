package org.ylab.auth;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ylab.entity.User;
import org.ylab.user.UserService;
import org.ylab.validation.EmailValidator;
import org.ylab.validation.PasswordValidator;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        validateRequestParams(email, password);

        User currentUser = userService.authenticate(email, password);
        req.getSession().setAttribute("user", currentUser);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void validateRequestParams(String email, String password) {
        EmailValidator.validateEmail(email);
        PasswordValidator.validatePassword(password);
    }
}
