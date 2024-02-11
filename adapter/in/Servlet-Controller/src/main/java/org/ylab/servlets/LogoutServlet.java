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
        int id = ((User) session.getAttribute("user")).getId();
        session.invalidate();
        resp.setStatus(HttpServletResponse.SC_OK);
        getServletContext().log(Instant.now() + " - User logged out with id: " + id);
    }
}
