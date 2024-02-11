package org.ylab.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ylab.annotations.Loggable;
import org.ylab.entity.User;
import org.ylab.user.UserService;
import org.ylab.validation.EmailValidator;
import org.ylab.validation.PasswordValidator;

import java.time.Instant;

@Loggable
@WebServlet(urlPatterns = "/auth/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;
    private ServletContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = getServletContext();
        final Object userServiceFromContext = context.getAttribute("userService");

        if (userServiceFromContext instanceof UserService) {
            this.userService = (UserService) userServiceFromContext;
        } else {
            throw new IllegalStateException("Repo has not been initialized!");
        }
    }

    /**
     * @param req  Should contain parameters "email" and "password"
     * @param resp Http status 200 if successfully logged in
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        context.log(Instant.now() + " - Received request to log in with:" +
                "\nemail: " + email + "\npassword: " + password);

        validateRequestParams(email, password);

        User currentUser = userService.authenticate(email, password);
        req.getSession().setAttribute("user", currentUser);
        resp.setStatus(HttpServletResponse.SC_OK);
        context.log(Instant.now() + " - User logged in with id: " + currentUser.getId());
    }

    private void validateRequestParams(String email, String password) {
        EmailValidator.validateEmail(email);
        PasswordValidator.validatePassword(password);
    }
}
