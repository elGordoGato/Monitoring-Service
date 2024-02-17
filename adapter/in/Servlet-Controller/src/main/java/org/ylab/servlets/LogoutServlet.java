package org.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ylab.annotations.Loggable;
import org.ylab.entity.User;

import java.time.Instant;

@Loggable
@WebServlet(urlPatterns = "/auth/logout")
public class LogoutServlet extends HttpServlet {
    /**
     * Handles logging out of user account
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        User userAttribute = ((User) session.getAttribute("user"));
        if (userAttribute != null) {
            int id = userAttribute.getId();
            session.invalidate();
            resp.setStatus(HttpServletResponse.SC_OK);
            getServletContext().log(Instant.now() + " - User logged out with id: " + id);
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            getServletContext().log(Instant.now() + " - Logout request failed: no proper session found");
        }

    }
}
