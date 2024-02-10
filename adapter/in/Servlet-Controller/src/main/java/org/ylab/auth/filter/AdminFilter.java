package org.ylab.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ylab.entity.User;
import org.ylab.enums.Role;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@WebFilter("/admin/*")
public class AdminFilter extends HttpFilter {
    /**
     * @param req
     * @param res
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        final HttpSession session = req.getSession();

        User loggedUser = (User) session.getAttribute("user");

        if (!loggedUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You don't have access to this resource");
        }
        chain.doFilter(req, res);
    }


}