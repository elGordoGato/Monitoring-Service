package org.ylab;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;
import org.ylab.user.UserService;

import java.time.Instant;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ManualConfig.setJdbcRepository();
        final ServletContext servletContext =
                sce.getServletContext();

        UserService userService = ManualConfig.getUserService();
        ReadingService readingServiceByUser = ManualConfig.getReadingServiceByUser();
        ReadingService readingServiceByAdmin = ManualConfig.getReadingServiceByAdmin();
        MeterService meterService = ManualConfig.getMeterService();

        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("readingServiceByUser", readingServiceByUser);
        servletContext.setAttribute("readingServiceByAdmin", readingServiceByAdmin);
        servletContext.setAttribute("meterService", meterService);
        System.out.println(Instant.now() + " - Context initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.closeConnection();
        System.out.println(Instant.now() + " - Context destroyed");
    }
}