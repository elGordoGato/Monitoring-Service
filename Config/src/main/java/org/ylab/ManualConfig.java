package org.ylab;

import org.ylab.meter.MeterService;
import org.ylab.meter.MeterServiceImpl;
import org.ylab.port.MeterRepository;
import org.ylab.port.ReadingRepository;
import org.ylab.port.UserRepository;
import org.ylab.reading.ReadingAdminServiceImpl;
import org.ylab.reading.ReadingService;
import org.ylab.reading.ReadingServiceImpl;
import org.ylab.repository.*;
import org.ylab.user.UserService;
import org.ylab.user.UserServiceImpl;

public class ManualConfig {
    private static ReadingRepository readingRepository;
    private static UserRepository userRepository;
    private static MeterRepository meterRepository;

    public static void setInMemoryRepository() {
        readingRepository = new ReadingRepositoryInMemory();
        userRepository = new UserRepositoryInMemory();
        meterRepository = new MeterRepositoryInMemory();
    }

    public static void setJdbcRepository() {
        String dbDriver = DatabaseConfig.getDriver();
        String connectionUrl = DatabaseConfig.getURL();
        String userName = DatabaseConfig.getUserName();
        String password = DatabaseConfig.getPassword();

        ConnectionManager connectionManager = new ConnectionManager(
                dbDriver, connectionUrl, userName, password);
        MigrationManager.migrateDB(connectionManager.getConnection());

        readingRepository = new ReadingJdbcRepository(connectionManager);
        userRepository = new UserJdbcRepository(connectionManager);
        meterRepository = new MeterJdbcRepository(connectionManager);
    }

    public static UserService getUserService() {
        return new UserServiceImpl(userRepository);
    }

    public static ReadingService getReadingServiceByUser() {
        return new ReadingServiceImpl(readingRepository);
    }

    public static ReadingService getReadingServiceByAdmin() {
        return new ReadingAdminServiceImpl(readingRepository);
    }

    public static MeterService getMeterService() {
        return new MeterServiceImpl(meterRepository);
    }
}
