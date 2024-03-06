package org.ylab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ylab.enableConfig.EnableLoggingExecutionTimeAspect;

@SpringBootApplication
@EnableLoggingExecutionTimeAspect
public class MonitoringServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonitoringServiceApplication.class, args);
    }
}
