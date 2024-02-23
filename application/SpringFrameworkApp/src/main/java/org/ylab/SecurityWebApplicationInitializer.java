package org.ylab;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.ylab.authentication.SecurityConfig;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    public SecurityWebApplicationInitializer() {
        super(SecurityConfig.class);
    }
}