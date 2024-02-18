package org.ylab.servlets.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@WebFilter("/admin/*")
public class AdminFilter extends HttpFilter {
    /**
     * Checking if the authenticated user has role of admin to access specified resources,
     * if not throws AccessDeniedException
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        final HttpSession session = req.getSession();

        UserEntity loggedUser = (UserEntity) session.getAttribute("user");

        if (!loggedUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You don't have access to this resource");
        }
        chain.doFilter(req, res);
    }


}