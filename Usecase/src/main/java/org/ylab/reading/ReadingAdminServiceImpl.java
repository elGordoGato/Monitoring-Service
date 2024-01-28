package org.ylab.reading;

import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class ReadingAdminServiceImpl extends ReadingServiceImpl {

    public ReadingAdminServiceImpl(ReadingRepository readingRepository) {
        super(readingRepository);
    }

    /**
     * @param user
     * @return
     */
    @Override
    public List<Reading> getActual(User user) {
        return readingRepository.findActualByAdmin();
    }

    /**
     * @param currentUser
     * @param date
     * @return
     */
    @Override
    public List<Reading> getForMonth(User currentUser, LocalDate date) {
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return readingRepository.findAllByDateBetween(start, end);
    }

    /**
     * @param currentUser
     * @return
     */
    @Override
    public List<Reading> getAllByUser(User currentUser) {
        return readingRepository.findAll();
    }
}
