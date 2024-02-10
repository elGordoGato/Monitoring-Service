package org.ylab.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import static java.util.Objects.isNull;

/**
 * Acidification filter.
 */

@WebFilter("/")
public class AuthFilter extends HttpFilter {
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
        String path = req.getServletPath();
        if (path.startsWith("/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        final HttpSession session = req.getSession();

        if (isNull(session) || isNull(session.getAttribute("user"))) {
            throw new AccessDeniedException("You don't have access to this resource");
        }
        chain.doFilter(req, res);
    }
}
