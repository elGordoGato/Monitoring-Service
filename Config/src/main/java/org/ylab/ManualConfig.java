package org.ylab;

import org.ylab.meter.MeterService;
import org.ylab.meter.MeterServiceImpl;
import org.ylab.port.MeterRepository;
import org.ylab.port.ReadingRepository;
import org.ylab.port.UserRepository;
import org.ylab.reading.ReadingAdminServiceImpl;
import org.ylab.reading.ReadingService;
import org.ylab.reading.ReadingServiceImpl;
import org.ylab.user.UserService;
import org.ylab.user.UserServiceImpl;

public class ManualConfig {
    private static ReadingRepository readingRepository;
    private static UserRepository userRepository;
    private static MeterRepository meterRepository;

    public static void setInMemoryRepo(){
        readingRepository = new ReadingRepositoryInMemory();
        userRepository = new UserRepositoryInMemory();
        meterRepository = new MeterRepositoryInMemory();
    }

    public static UserService getUserService(){
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
