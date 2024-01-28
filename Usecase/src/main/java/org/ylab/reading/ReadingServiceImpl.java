package org.ylab.reading;

import org.ylab.entity.Reading;
import org.ylab.entity.Meter;
import org.ylab.entity.User;
import org.ylab.exception.ConflictException;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class ReadingServiceImpl implements ReadingService {
    ReadingRepository readingRepository;

    public ReadingServiceImpl(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    /**
     * @param user
     * @return
     */
    @Override
    public List<Reading> getActual(User user) {
        return readingRepository.findActualByUser(user);
    }

    @Override
    public Reading create(User user, Meter type, long reading) {
        readingRepository.findLastByUserAndType(user, type)
                .ifPresent(r -> {
                    if (LocalDate.ofInstant(r.getCollectedDate(), ZoneId.systemDefault()).getMonth()
                            .equals(LocalDate.now().getMonth())) {
                        throw new ConflictException("Readings fot this meter were already collected in this month");
                    }
                });
        Reading meterReading = new Reading();
        meterReading.setMeter(type);
        meterReading.setOwner(user);
        meterReading.setReading(reading);
        return readingRepository.save(meterReading);
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
        return readingRepository.findAllByOwnerAndDateBetween(currentUser, start, end);
    }

    /**
     * @param currentUser
     * @return
     */
    @Override
    public List<Reading> getAllByUser(User currentUser) {
        return readingRepository.findAllByOwner(currentUser);
    }
}
