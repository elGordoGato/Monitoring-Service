package org.ylab.servlets.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ylab.entity.User;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import static java.util.Objects.nonNull;

/**
 * Authentication filter.
 */

@WebFilter("/*")
public class AuthFilter extends HttpFilter {
    /**
     * Checking if user has been authenticated and if not throws AccessDeniedException
     *
     * @param req   httpRequest
     * @param res   httpResponse
     * @param chain Filter chain
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String path = req.getServletPath();
        if (path.startsWith("/auth")) {
            chain.doFilter(req, res);
            return;
        }

        final HttpSession session = req.getSession();

        if (nonNull(session)) {
            Object user = session.getAttribute("user");

            if (nonNull(user) && user instanceof User) {
                chain.doFilter(req, res);
                return;
            }
        }
        throw new AccessDeniedException("You don't have access to this resource");
    }
}
