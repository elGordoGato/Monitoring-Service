package org.ylab.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ylab.aspects.AuditableLoggingAspect;

@Configuration
@ConditionalOnProperty(name = "audit.api.enabled", havingValue = "true", matchIfMissing = true)
public class LogAuditAutoConfiguration {

    @Bean
    public AuditableLoggingAspect auditableLoggingAspect() {
        return new AuditableLoggingAspect();
    }


}