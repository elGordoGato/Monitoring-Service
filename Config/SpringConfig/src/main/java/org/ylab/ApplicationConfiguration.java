package org.ylab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;
import org.ylab.user.UserService;


@Configuration
@EnableAspectJAutoProxy
public class ApplicationConfiguration {

    public ApplicationConfiguration() {
        ManualConfig.setJdbcRepository();
    }

    @Bean
    public UserService userService() {
        return ManualConfig.getUserService();
    }

    @Bean
    public MeterService meterService() {
        return ManualConfig.getMeterService();
    }

    @Bean
    public ReadingService adminReadingService() {
        return ManualConfig.getReadingServiceByAdmin();
    }

    @Bean
    public ReadingService userReadingService() {
        return ManualConfig.getReadingServiceByUser();
    }
}

