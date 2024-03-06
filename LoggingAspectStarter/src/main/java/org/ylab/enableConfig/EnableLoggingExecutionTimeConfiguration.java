package org.ylab.enableConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ylab.aspects.LoggableAspect;

@Configuration
public class EnableLoggingExecutionTimeConfiguration {
    @Bean
    LoggableAspect loggableAspect() {
        return new LoggableAspect();
    }
}
